package wikxplorer.messages;

import java.util.Map;

import org.hibernate.Session;

import wikxplorer.ScopeKeys;
import ecologylab.collections.Scope;
import ecologylab.oodss.messages.OkResponse;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.serialization.simpl_inherit;

/**
 * Update the context (represented by collected clippings).
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class UpdateContextRequest extends RequestMessage
{

	public static final int	ACTION_NONE		= 0;

	public static final int	ACTION_ADD		= 1;

	public static final int	ACTION_REMOVE	= 2;

	@simpl_scalar
	private int							action				= ACTION_NONE;

	@simpl_scalar
	private String					title;

	public int getAction()
	{
		return action;
	}

	public void setAction(int action)
	{
		this.action = action;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public ResponseMessage performService(Scope clientSessionScope)
	{
		Session session = (Session) clientSessionScope.get(ScopeKeys.SESSION);

		Map<String, WikiConcept> clippingContext = (Map<String, WikiConcept>) clientSessionScope
				.get(ScopeKeys.CLIPPING_CONTEXT);

		switch (action)
		{
		case ACTION_ADD:
			WikiConcept concept = WikiConcept.get(title, session);
			if (concept != null)
				clippingContext.put(title, concept);
			break;
		case ACTION_REMOVE:
			clippingContext.remove(title);
			break;
		default:
			break;
		}

		return new OkResponse();
	}

}
