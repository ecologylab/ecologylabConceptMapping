package wikxplorer.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import wikxplorer.ScopeKeys;
import wikxplorer.model.LinkGroupingStrategy;
import wikxplorer.model.LinkSuggestionStrategy;

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

	/**
	 * The source concept title.
	 */
	@simpl_scalar
	private String	title;

	/**
	 * The number of clusters. Set to 0 to use server determined number.
	 */
	@simpl_scalar
	private int			k;

	public SuggestionRequest()
	{

	}

	public SuggestionRequest(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public int getK()
	{
		return k;
	}

	public void setK(int k)
	{
		this.k = k;
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
				LinkSuggestionStrategy lss = (LinkSuggestionStrategy) clientSessionScope
						.get(ScopeKeys.LINK_SUGGESTION_STRATEGY);
				List<Link> suggestedLinks = lss.suggestLinks(respConcept, clippingContext);

				LinkGroupingStrategy lgs = (LinkGroupingStrategy) clientSessionScope
						.get(ScopeKeys.LINK_GROUPING_STRATEGY);
				ArrayList<LinkGroup> groups = lgs.groupLinks(suggestedLinks, k, clippingContext);

				resp.setTitle(title);
				resp.setSuggestedLinkCount(suggestedLinks.size());
				resp.setSuggestedLinkGroups(groups);
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

}
