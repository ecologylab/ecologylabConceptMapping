package wikxplorer.messages;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.simpl_inherit;

/**
 * Suggested links in groups (in concept.suggestedLinkGroups).
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class SuggestionResponse extends ResponseMessage
{

	/**
	 * The source concept.
	 */
	@simpl_composite
	private Concept	concept;

	@simpl_scalar
	private boolean	ok	= false;

	public Concept getConcept()
	{
		return concept;
	}

	public void setConcept(Concept concept)
	{
		this.concept = concept;
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
