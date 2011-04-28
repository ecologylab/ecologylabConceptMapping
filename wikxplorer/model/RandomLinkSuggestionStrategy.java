package wikxplorer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;

import wikxplorer.Server;
import wikxplorer.messages.Concept;
import wikxplorer.messages.Link;

public class RandomLinkSuggestionStrategy implements LinkSuggestionStrategy
{

	private static final String	SQL1	= "SELECT from_id FROM wiki_links WHERE to_id = ? AND from_id != to_id ORDER BY random() LIMIT ?;";

	private static final String	SQL2	= "SELECT to_id FROM wiki_links WHERE from_id = ? AND from_id != to_id ORDER BY random() LIMIT ?;";

	private final double				WEIGHT_R0;

	private final double				WEIGHT_RC;

	private final double				WEIGHT_RA;

	private final double				MIN_DIST_THREASHOLD;

	private final int						RANDOM_LINK_NUMBER;

	private final int						MIN_LINKS_TO_RETURN;

	public RandomLinkSuggestionStrategy()
	{
		WEIGHT_R0 = Double.valueOf(Server.properties.getProperty("suggestion.weight_r0"));
		WEIGHT_RC = Double.valueOf(Server.properties.getProperty("suggestion.weight_rc"));
		WEIGHT_RA = Double.valueOf(Server.properties.getProperty("suggestion.weight_ra"));
		MIN_DIST_THREASHOLD = Double.valueOf(Server.properties.getProperty(
				"suggestion.min_dist_threashold"));
		RANDOM_LINK_NUMBER = Integer.valueOf(Server.properties.getProperty(
				"suggestion.random_link_number"));
		MIN_LINKS_TO_RETURN = Integer.valueOf(Server.properties
				.getProperty("suggestion.min_links_to_return"));
	}

	/**
	 * Note that this method may return different results at different invocation, because of its
	 * randomness.
	 */
	@Override
	public List<Link> suggestLinks(Concept concept, Map<String, Concept> context)
	{
		Session session = SessionManager.newSession();

		Set<Integer> inlinkIds = new HashSet<Integer>();
		Set<Integer> outlinkIds = new HashSet<Integer>();
		Set<Integer> linkIds = new HashSet<Integer>();

		System.out.println("retrieving inlinks for " + concept.getTitle() + "...");
		SQLQuery q1 = session.createSQLQuery(SQL1);
		q1.setCacheable(false);
		q1.setInteger(0, concept.getId());
		q1.setInteger(1, RANDOM_LINK_NUMBER);
		q1.addScalar("from_id", StandardBasicTypes.INTEGER);
		for (Object id : q1.list())
		{
			Integer iid = (Integer) id;
			inlinkIds.add(iid);
			linkIds.add(iid);
		}

		System.out.println("retrieving outlinks for " + concept.getTitle() + "...");
		SQLQuery q2 = session.createSQLQuery(SQL2);
		q2.setCacheable(false);
		q2.setInteger(0, concept.getId());
		q2.setInteger(1, RANDOM_LINK_NUMBER);
		q2.addScalar("to_id", StandardBasicTypes.INTEGER);
		for (Object id : q2.list())
		{
			Integer oid = (Integer) id;
			outlinkIds.add(oid);
			linkIds.add(oid);
		}

		List<Link> links = new ArrayList<Link>();
		int relevant = 0;
		for (int id : linkIds)
		{
			Link l = new Link();
			WikiConcept c = WikiConcept.getById(id, session); // c can't be null
			if (context.containsValue(c))
				continue; // don't suggest concepts that are already in the context
			l.wikiConcept = c;
			l.setTitle(c.getTitle());

			System.out.println("calculating relatedness for " + concept.getTitle() + " and "
					+ c.getTitle() + "...");
			double r0 = concept.wikiConcept.getRelatedness(c);
			double rel = getContextuallyRelatedness(context, c, r0);
			if (rel < MIN_DIST_THREASHOLD)
				relevant++;
			l.setRelatedness(rel);
			int type = Link.NONE;
			if (inlinkIds.contains(id))
				type |= Link.INLINK;
			if (outlinkIds.contains(id))
				type |= Link.OUTLINK;
			l.setType(type);

			links.add(l);
		}

		session.close();

		Collections.sort(links);

		int n = links.size();
		if (n > RANDOM_LINK_NUMBER)
			n = RANDOM_LINK_NUMBER;
		if (n > relevant)
			n = relevant;
		if (n < MIN_LINKS_TO_RETURN)
			n = MIN_LINKS_TO_RETURN;

		if (n == links.size())
			return links;
		else
			return links.subList(0, n);
	}

	private double getContextuallyRelatedness(Map<String, Concept> context, WikiConcept concept,
			double r0)
	{
		double rc = WikiConcept.MAX_DIST; // min distance to contextual concepts
		double ra = 0; // average distance to contextual concepts
		for (Concept c : context.values())
		{
			if (c.wikiConcept.equals(concept))
				continue; // if concept is already in the context, don't compare with itself

			double rel = concept.getRelatedness(c.wikiConcept);
			if (rel < rc)
				rc = rel;
			ra += rel;
		}
		ra /= context.size();
		double r = r0 * WEIGHT_R0 + rc * WEIGHT_RC + ra * WEIGHT_RA;
		return r;
	}

}
