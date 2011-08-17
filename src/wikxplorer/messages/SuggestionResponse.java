package wikxplorer.messages;

import java.util.ArrayList;

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
	 * The source concept title from clients.
	 */
	@simpl_scalar
	private String								title;

	/**
	 * How many concepts are suggested from this one. Not the number of groups.
	 */
	@simpl_scalar
	private int										suggestedLinkCount;

	/**
	 * Suggested concepts organized in groups.
	 */
	@simpl_collection("suggested_link_group")
	private ArrayList<LinkGroup>	suggestedLinkGroups	= new ArrayList<LinkGroup>();

	@simpl_scalar
	private boolean								ok									= false;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getSuggestedLinkCount()
	{
		return suggestedLinkCount;
	}

	public void setSuggestedLinkCount(int suggestedLinkCount)
	{
		this.suggestedLinkCount = suggestedLinkCount;
	}

	public ArrayList<LinkGroup> getSuggestedLinkGroups()
	{
		return suggestedLinkGroups;
	}

	public void setSuggestedLinkGroups(ArrayList<LinkGroup> suggestedLinkGroups)
	{
		this.suggestedLinkGroups = suggestedLinkGroups;
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
