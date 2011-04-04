package wikxplorer.messages;

import java.util.Map;

import org.hibernate.Session;

import wikxplorer.ScopeKeys;
import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.semantics.concept.database.orm.Relatedness;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.serialization.simpl_inherit;

/**
 * Request relatedness values from a single source to the context.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
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

		WikiConcept sourceConcept = null;
		if (clippingContext.containsKey(source))
		{
			sourceConcept = clippingContext.get(source);
		}
		else
		{
			sourceConcept = WikiConcept.get(source, session);
		}

		RelatednessResponse resp = new RelatednessResponse();
		if (sourceConcept != null)
		{
			for (String title : clippingContext.keySet())
			{
				WikiConcept target = clippingContext.get(title);
				int id1 = sourceConcept.getId();
				int id2 = target.getId();
				double rel = Relatedness.get(id1, id2, session);
				Concept respConcept = new Concept();
				respConcept.setTitle(title);
				respConcept.setRelatedness(rel);
				resp.getTargets().put(title, respConcept);
			}
			resp.setOk(true);
		}

		return resp;
	}

}
