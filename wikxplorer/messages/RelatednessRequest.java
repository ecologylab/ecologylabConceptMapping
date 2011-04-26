package wikxplorer.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.hibernate.Session;

import wikxplorer.ScopeKeys;
import ecologylab.collections.Scope;
import ecologylab.generic.HashMapArrayList;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.simpl_inherit;

/**
 * Request relatedness values from a single source concept to the context. The source concept does
 * not need to exist in the context. This operation will not add the source concept to the context.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
// TODO this request should add the source concept into the context if it is not there
public class RelatednessRequest extends RequestMessage
{

	/**
	 * The source concept title.
	 */
	@simpl_scalar
	private String	title;

	public RelatednessRequest()
	{

	}

	public RelatednessRequest(String title)
	{
		this.title = title;
	}

	@Override
	public RelatednessResponse performService(Scope clientSessionScope)
	{
		RelatednessResponse resp = new RelatednessResponse();

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
				HashMapArrayList<String, Link> contextualLinks = respConcept.getContextualLinks();
				if (contextualLinks.size() <= 0 || !respConcept.dirtyContextualLinks)
				{
					ArrayList<Link> links = new ArrayList<Link>();
					for (Link link : contextualLinks)
						if (clippingContext.containsKey(link.getTitle()))
							links.add(link);

					for (String t : clippingContext.keySet())
					{
						if (t.equals(title))
							continue;

						if (contextualLinks.containsKey(t))
							continue; // already in links

						Concept c = clippingContext.get(t);
						double r = respConcept.wikiConcept.getRelatedness(c.wikiConcept);

						Link l = new Link();
						l.setTitle(t);
						l.setRelatedness(r);
						int type = Link.NONE;
						if (respConcept.wikiConcept.isInlink(t, session))
							type |= Link.INLINK;
						if (respConcept.wikiConcept.isOutlink(t, session))
							type |= Link.OUTLINK;
						l.setType(type);

						links.add(l);
					}

					Collections.sort(links);
					contextualLinks.clear();
					for (Link link : links)
						contextualLinks.put(link.getTitle(), link);
					respConcept.dirtyContextualLinks = false;
				}

				resp.setTitle(title);
				resp.setContextualLinks(respConcept.getContextualLinks());
				resp.setOk(true);
			}
		}

		// for debug
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

}
