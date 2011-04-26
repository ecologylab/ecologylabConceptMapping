package wikxplorer.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import wikxplorer.ScopeKeys;
import wikxplorer.Server;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.simpl_inherit;

/**
 * Request suggestions for a concept.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class SuggestionRequest extends RequestMessage
{

	private static final double	WEIGHT_R0;

	private static final double	WEIGHT_RC;

	private static final double	WEIGHT_RA;

	private static final double	MIN_DIST_THREASHOLD;

	static
	{
		WEIGHT_R0 = Double.valueOf(Server.properties.getProperty("suggestion.weight_r0", "0.4"));
		WEIGHT_RC = Double.valueOf(Server.properties.getProperty("suggestion.weight_rc", "0.4"));
		WEIGHT_RA = Double.valueOf(Server.properties.getProperty("suggestion.weight_ra", "0.2"));
		MIN_DIST_THREASHOLD = Double.valueOf(Server.properties.getProperty(
				"suggestion.min_dist_threashold", "0.5"));
	}

	/**
	 * The source concept title.
	 */
	@simpl_scalar
	private String							title;

	public SuggestionRequest()
	{

	}

	public SuggestionRequest(String title)
	{
		this.title = title;
	}

	@Override
	public SuggestionResponse performService(Scope clientSessionScope)
	{
		SuggestionResponse resp = new SuggestionResponse();

		if (title != null && !title.isEmpty())
		{
			Session session = (Session) clientSessionScope.get(ScopeKeys.SESSION);
			Map<String, Concept> clippingContext = (Map<String, Concept>) clientSessionScope
					.get(ScopeKeys.CLIPPING_CONTEXT);

			Concept respConcept = null;
			if (clippingContext.containsKey(title))
			{
				respConcept = clippingContext.get(title);
			}
			else
			{
				WikiConcept ws = WikiConcept.getByTitle(title, session);
				if (ws != null)
				{
					respConcept = new Concept();
					respConcept.setTitle(title);
					respConcept.wikiConcept = ws;
				}
			}

			if (respConcept != null)
			{
				if (respConcept.getSuggestedLinkCount() > 0 || !respConcept.dirtySuggestedLinks)
				{
					Map<WikiConcept, Double> inlinks = respConcept.wikiConcept
							.getOrCalculateTopRelatedInlinks(session);
					Map<WikiConcept, Double> outlinks = respConcept.wikiConcept
							.getOrCalculateTopRelatedOutlinks(session);

					Map<String, Link> links = new HashMap<String, Link>();

					for (WikiConcept inlink : inlinks.keySet())
					{
						double r0 = inlinks.get(inlink);
						double r = getContextuallyRelatedness(clippingContext, inlink, r0);
						if (r < MIN_DIST_THREASHOLD)
						{
							Link link = new Link();
							link.wikiConcept = inlink;
							link.setTitle(inlink.getTitle());
							int type = Link.INLINK;
							if (outlinks.containsKey(inlink))
								type |= Link.OUTLINK;
							link.setType(type);
							link.setRelatedness(r);
							links.put(link.getTitle(), link);
						}
					}
					for (WikiConcept outlink : outlinks.keySet())
					{
						if (inlinks.containsKey(outlink))
							continue; // already processed in the previous loop
						double r0 = outlinks.get(outlink);
						double r = getContextuallyRelatedness(clippingContext, outlink, r0);
						if (r < MIN_DIST_THREASHOLD)
						{
							Link link = new Link();
							link.wikiConcept = outlink;
							link.setTitle(outlink.getTitle());
							link.setType(Link.OUTLINK);
							link.setRelatedness(r);
							links.put(link.getTitle(), link);
						}
					}

					ArrayList<LinkGroup> groups = getLinkGroups(links);
					respConcept.setSuggestedLinkCount(links.size());
					respConcept.setSuggestedLinkGroups(groups);
					respConcept.dirtySuggestedLinks = false;
				}

				resp.setTitle(title);
				resp.setSuggestedLinkCount(respConcept.getSuggestedLinkCount());
				resp.setSuggestedLinkGroups(respConcept.getSuggestedLinkGroups());
				resp.setOk(true);
			}
		}

		try
		{
			System.out.println();
			resp.serialize(System.out);
			System.out.println();
		}
		catch (SIMPLTranslationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resp;
	}

	private double getContextuallyRelatedness(Map<String, Concept> context, WikiConcept concept,
			double r0)
	{
		if (context.containsValue(concept))
			return WikiConcept.MAX_DIST;

		double rc = WikiConcept.MAX_DIST; // min distance to contextual concepts
		double ra = 0; // average distance to contextual concepts
		for (Concept c : context.values())
		{
			if (c.getTitle().equals(this.title))
				continue;

			double rel = concept.getRelatedness(c.wikiConcept);
			if (rel < rc)
				rc = rel;
			ra += rel;
		}
		ra /= context.size();

		double r = r0 * WEIGHT_R0 + rc * WEIGHT_RC + ra * WEIGHT_RA;
		return r;
	}

	private ArrayList<LinkGroup> getLinkGroups(Map<String, Link> links)
	{
		// TODO

		ArrayList<Link> linkList = new ArrayList<Link>();
		for (Link link : links.values())
		{
			linkList.add(link);
		}
		Collections.sort(linkList);

		ArrayList<LinkGroup> groups = new ArrayList<LinkGroup>();
		for (Link link : linkList)
		{
			LinkGroup lg = new LinkGroup();
			lg.getLinks().put(link.getTitle(), link);
			lg.setTopTitle(link.getTitle());
			lg.setAverageRelatedness(link.getRelatedness());
			groups.add(lg);
		}

		return groups;
	}

}
