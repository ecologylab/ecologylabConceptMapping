package wikxplorer.messages;

import java.util.ArrayList;
import java.util.List;

import ecologylab.collections.Scope;
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

	@Override
	public void processResponse(Scope objectRegistry)
	{
		// just for testing
		System.out.println("SuggestionResponse: [" + totalSize + "]");
		for (ConceptGroup group : groups)
		{
			System.out.println("\tGroup: [" + group.getConcepts().size() + "]");
			System.out.println("\t\tAverage Relatedness: " + group.getAverageRelatedness());
			System.out.println("\t\tTop Title: " + group.getTopTitle());
			for (String title : group.getConcepts().keySet())
			{
				Concept concept = group.getConcepts().get(title);
				System.out.println(String.format("\t%s: %f", title, concept.getRelatedness()));
			}
		}
	}

}
