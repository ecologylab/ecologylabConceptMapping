package wikxplorer.messages;

import ecologylab.collections.Scope;
import ecologylab.generic.HashMapArrayList;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.simpl_inherit;

/**
 * Relatedness values (in concept.contextualLinks).
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class RelatednessResponse extends ResponseMessage
{

	/**
	 * The source concept title from clients.
	 */
	@simpl_scalar
	private String													title;

	/**
	 * Links to other concepts in the context (note that non-link is seen as a link).
	 */
	@simpl_map("contextual_link")
	private HashMapArrayList<String, Link>	contextualLinks	= new HashMapArrayList<String, Link>();

	@simpl_scalar
	private boolean													ok							= false;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public HashMapArrayList<String, Link> getContextualLinks()
	{
		return contextualLinks;
	}

	public void setContextualLinks(HashMapArrayList<String, Link> contextualLinks)
	{
		this.contextualLinks = contextualLinks;
	}

	public void setOk(boolean ok)
	{
		this.ok = ok;
	}

	@Override
	public boolean isOK()
	{
		return ok;
	}

	@Override
	public void processResponse(Scope objectRegistry)
	{
		// for debug
		try
		{
			System.out.println();
			this.serialize(System.out);
			System.out.println();
		}
		catch (SIMPLTranslationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
