package wikxplorer.messages;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import wikxplorer.ScopeKeys;
import ecologylab.collections.Scope;
import ecologylab.oodss.messages.OkResponse;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.serialization.simpl_inherit;

/**
 * Update the context (represented by a set of concepts).
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

	/**
	 * The action (add / remove).
	 */
	@simpl_scalar
	private int							action				= ACTION_NONE;

	/**
	 * The title of the operand concept. Case insensitive.
	 */
	@simpl_scalar
	private String					title;

	public UpdateContextRequest()
	{

	}

	public UpdateContextRequest(int action, String title)
	{
		this.action = action;
		this.title = title;
	}

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
		if (session == null)
		{
			session = SessionManager.newSession();
			clientSessionScope.put(ScopeKeys.SESSION, session);
		}

		Map<String, Concept> clippingContext = (Map<String, Concept>) clientSessionScope
				.get(ScopeKeys.CLIPPING_CONTEXT);
		if (clippingContext == null)
		{
			clippingContext = new HashMap<String, Concept>();
			clientSessionScope.put(ScopeKeys.CLIPPING_CONTEXT, clippingContext);
		}

		switch (action)
		{
		case ACTION_ADD:
			WikiConcept ws = WikiConcept.getByTitle(title, session);
			if (ws != null)
			{
				makeAllDirty(clippingContext);
				Concept concept = new Concept();
				concept.setTitle(title);
				concept.wikiConcept = ws;
				clippingContext.put(title, concept);
			}
			break;
		case ACTION_REMOVE:
			clippingContext.remove(title);
			makeAllDirty(clippingContext);
			break;
		default:
			break;
		}

		return new OkResponse();
	}

	private void makeAllDirty(Map<String, Concept> clippingContext)
	{
		for (Concept c : clippingContext.values())
		{
			c.dirtyContextualLinks = true;
			c.dirtySuggestedLinks = true;
		}
	}

}
