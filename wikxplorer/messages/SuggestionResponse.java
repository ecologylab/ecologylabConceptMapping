package wikxplorer.messages;

import java.util.ArrayList;
import java.util.List;

import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.serialization.simpl_inherit;

/**
 * Suggested concepts in groups.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class SuggestionResponse extends ResponseMessage
{

	/**
	 * The total number of suggested concepts. (Not the number of groups)
	 */
	@simpl_scalar
	private int									totalSize;

	/**
	 * Suggested concepts, organized in groups.
	 */
	@simpl_collection
	private List<ConceptGroup>	groups	= new ArrayList<ConceptGroup>();

	private boolean							ok			= false;

	public void setOk(boolean ok)
	{
		this.ok = ok;
	}

	@Override
	public boolean isOK()
	{
		return false;
	}

	public void setTotalSize(int totalSize)
	{
		this.totalSize = totalSize;
	}

	public int getTotalSize()
	{
		return totalSize;
	}

	public List<ConceptGroup> getGroups()
	{
		return groups;
	}

}
