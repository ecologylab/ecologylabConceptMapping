package wikxplorer.messages;

import java.util.ArrayList;

import ecologylab.generic.HashMapArrayList;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.serialization.ElementState;
import ecologylab.serialization.simpl_inherit;

/**
 * This class holds information about a concept at server side. The same class can be used at client
 * side. Results of looking up contextual relatedness or suggested links are cached here for
 * efficiency.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class Concept extends ElementState
{

	/**
	 * Title of the concept.
	 */
	@simpl_scalar
	private String													title;

	/**
	 * Links to other concepts in the context (note that non-link is seen as a link).
	 */
	@simpl_map("contextual_link")
	private HashMapArrayList<String, Link>	contextualLinks				= new HashMapArrayList<String, Link>();

	/**
	 * How many concepts are suggested from this one. Not the number of groups.
	 */
	@simpl_scalar
	private int															suggestedLinkCount;

	/**
	 * Suggested concepts organized in groups.
	 */
	@simpl_collection("suggested_link_group")
	private ArrayList<LinkGroup>						suggestedLinkGroups		= new ArrayList<LinkGroup>();

	WikiConcept															wikiConcept;

	boolean																	dirtyContextualLinks	= false;

	boolean																	dirtySuggestedLinks		= false;

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

	public int getSuggestedLinkCount()
	{
		return suggestedLinkCount;
	}

	public void setSuggestedLinkCount(int suggestedLinkCount)
	{
		this.suggestedLinkCount = suggestedLinkCount;
	}

	public void setSuggestedLinkGroups(ArrayList<LinkGroup> suggestedLinkGroups)
	{
		this.suggestedLinkGroups = suggestedLinkGroups;
	}

	public ArrayList<LinkGroup> getSuggestedLinkGroups()
	{
		return suggestedLinkGroups;
	}

}
