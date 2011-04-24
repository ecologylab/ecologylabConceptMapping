package wikxplorer.messages;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import wikxplorer.ScopeKeys;
import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.simpl_inherit;

/**
 * Request relatedness values from a single source to the context.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
// TODO this request should add the source concept into the context if it is not there
public class RelatednessRequest extends RequestMessage
{
	/**
	 * Title of the source concept.
	 */
	@simpl_scalar
	private String	source;

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	@Override
	public RelatednessResponse performService(Scope clientSessionScope)
	{
		Session session = (Session) clientSessionScope.get(ScopeKeys.SESSION);

		Map<String, WikiConcept> clippingContext = (Map<String, WikiConcept>) clientSessionScope
				.get(ScopeKeys.CLIPPING_CONTEXT);
		if (clippingContext == null)
		{
			clippingContext = new HashMap<String, WikiConcept>();
			clientSessionScope.put(ScopeKeys.CLIPPING_CONTEXT, clippingContext);
		}

		WikiConcept sourceConcept = null;
		if (clippingContext.containsKey(source))
		{
			sourceConcept = clippingContext.get(source);
		}
		else
		{
			sourceConcept = WikiConcept.getByTitle(source, session);
		}

		RelatednessResponse resp = new RelatednessResponse();
		if (sourceConcept != null)
		{
			for (String title : clippingContext.keySet())
			{
				WikiConcept target = clippingContext.get(title);
				double rel = sourceConcept.getRelatedness(target, session);

				Concept respConcept = new Concept();
				respConcept.setTitle(title);
				respConcept.setRelatedness(rel);
				resp.getTargets().put(title, respConcept);
			}
			resp.setOk(true);
		}

		try
		{
			System.out.println();
			System.out.println();
			resp.serialize(System.out);
			System.out.println();
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
