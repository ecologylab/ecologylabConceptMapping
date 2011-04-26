package wikxplorer.messages;

import ecologylab.generic.HashMapArrayList;
import ecologylab.serialization.ElementState;
import ecologylab.serialization.simpl_inherit;

/**
 * A group of similar (related) links (pointing to concepts). The idea is that suggested related
 * concepts of a concept could be grouped by their internal relatedness.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class LinkGroup extends ElementState implements Comparable<LinkGroup>
{

	/**
	 * Links in this gropu.
	 */
	@simpl_map("link")
	private HashMapArrayList<String, Link>	links	= new HashMapArrayList<String, Link>();

	/**
	 * Title of the most representative one in this group.
	 */
	@simpl_scalar
	private String													topTitle;

	/**
	 * The average relatedness of links in this group.
	 */
	@simpl_scalar
	private double													averageRelatedness;

	public HashMapArrayList<String, Link> getLinks()
	{
		return links;
	}

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

	@Override
	public int compareTo(LinkGroup o)
	{
		return Double.compare(o.getAverageRelatedness(), this.getAverageRelatedness());
	}

}
