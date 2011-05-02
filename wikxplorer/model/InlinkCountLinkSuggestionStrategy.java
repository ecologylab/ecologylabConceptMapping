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

import wikxplorer.messages.Concept;
import wikxplorer.messages.Link;

public class InlinkCountLinkSuggestionStrategy extends LinkSuggestionStrategy
{

	private static final String	SQL1	= "SELECT DISTINCT ON (from_id) from_id, inlink_count FROM wiki_links INNER JOIN inlink_count ON from_id = concept_id WHERE to_id = ? AND from_id != to_id ORDER BY from_id LIMIT ?;";

	private static final String	SQL2	= "SELECT DISTINCT ON (to_id) to_id, inlink_count FROM wiki_links INNER JOIN inlink_count ON to_id = concept_id WHERE from_id = ? AND from_id != to_id ORDER BY to_id LIMIT ?;";

	@Override
	public List<Link> suggestLinks(Concept concept, Map<String, Concept> context)
	{
		Session session = SessionManager.newSession();

		Set<Integer> inlinkIds = new HashSet<Integer>();
		Set<Integer> outlinkIds = new HashSet<Integer>();

		List<Integer> linkIds = new ArrayList<Integer>();
		List<Double> intvBoundaries = new ArrayList<Double>();
		double intvBoundary = 0;
		intvBoundaries.add(intvBoundary);

		System.out.println("retrieving inlinks for " + concept.getTitle() + "...");
		SQLQuery q1 = session.createSQLQuery(SQL1);
		q1.setCacheable(true);
		q1.setInteger(0, concept.getId());
		q1.setInteger(1, RANDOM_LINK_NUMBER);
		q1.addScalar("from_id", StandardBasicTypes.INTEGER);
		q1.addScalar("inlink_count", StandardBasicTypes.INTEGER);
		for (Object id : q1.list())
		{
			Object[] results = (Object[]) id;
			Integer iid = (Integer) results[0];
			Integer ic = (Integer) results[1];
			inlinkIds.add(iid);

			linkIds.add(iid);
			intvBoundary += Math.log(ic);
			intvBoundaries.add(intvBoundary);
		}

		System.out.println("retrieving outlinks for " + concept.getTitle() + "...");
		SQLQuery q2 = session.createSQLQuery(SQL2);
		q2.setCacheable(true);
		q2.setInteger(0, concept.getId());
		q2.setInteger(1, RANDOM_LINK_NUMBER);
		q2.addScalar("to_id", StandardBasicTypes.INTEGER);
		q2.addScalar("inlink_count", StandardBasicTypes.INTEGER);
		for (Object id : q2.list())
		{
			Object[] results = (Object[]) id;
			Integer oid = (Integer) results[0];
			Integer ic = (Integer) results[1];
			outlinkIds.add(oid);

			linkIds.add(oid);
			intvBoundary += Math.log(ic);
			intvBoundaries.add(intvBoundary);
		}

		// PPS sampling
		System.out.println("sampling...");
		Set<Integer> sampledLinkIds = new HashSet<Integer>();
		if (linkIds.size() <= RANDOM_LINK_NUMBER)
		{
			// no need to sample
			sampledLinkIds.addAll(linkIds);
		}
		else
		{
			// need sampling
			while (sampledLinkIds.size() < RANDOM_LINK_NUMBER)
			{
				double u = intvBoundaries.get(intvBoundaries.size() - 1);
				double rand = Math.random() * u; // 0 <= rand < u
				int i = 0;
				while (intvBoundaries.get(i) <= rand)
					i++;
				sampledLinkIds.add(linkIds.get(i - 1));
			}
		}

		// for debug
		System.out.print("sampled ids:");
		for (int id : sampledLinkIds)
		{
			System.out.print(" ");
			System.out.print(id);
		}
		System.out.println();

		List<Link> links = new ArrayList<Link>();
		int relevant = 0;
		for (int id : sampledLinkIds)
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

		if (n >= links.size())
			return links;
		else
			return links.subList(0, n);
	}

}
