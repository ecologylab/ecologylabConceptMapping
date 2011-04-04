package wikxplorer.messages;

import java.util.HashMap;
import java.util.Map;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.simpl_inherit;

/**
 * A group of similar (related) concepts. Used in SuggestedConcepts. The idea is that suggested
 * related concepts of a concept could be grouped by their internal relatedness.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class ConceptGroup extends ElementState
{

	/**
	 * Concepts in this group.
	 */
	@simpl_map
	private Map<String, Concept>	concepts	= new HashMap<String, Concept>();

	/**
	 * The title of the top (most representative) concept in this group.
	 */
	@simpl_scalar
	private String								topTitle;

	/**
	 * The average relatedness of concepts in this group.
	 */
	@simpl_scalar
	private double								averageRelatedness;

	public String getTopTitle()
	{
		return topTitle;
	}

	public void setTopTitle(String topTitle)
	{
		this.topTitle = topTitle;
	}

	public double getAverageRelatedness()
	{
		return averageRelatedness;
	}

	public void setAverageRelatedness(double averageRelatedness)
	{
		this.averageRelatedness = averageRelatedness;
	}

	public Map<String, Concept> getConcepts()
	{
		return concepts;
	}

}
