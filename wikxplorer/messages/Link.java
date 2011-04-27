package wikxplorer.messages;

import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.serialization.ElementState;
import ecologylab.serialization.types.element.Mappable;

/**
 * Represent a link between two concepts.
 * 
 * @author quyin
 * 
 */
public class Link extends ElementState implements Mappable<String>, Comparable<Link>
{

	public static final int	NONE		= 0;

	public static final int	INLINK	= 2;

	public static final int	OUTLINK	= 1;

	/**
	 * Title of the target concept.
	 */
	@simpl_scalar
	private String					title;

	/**
	 * Type of this link, from the perspective of the source concept.
	 */
	@simpl_scalar
	private int							type		= NONE;

	/**
	 * True or suggested relatedness.
	 */
	@simpl_scalar
	private double					relatedness;

	public WikiConcept			wikiConcept;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public double getRelatedness()
	{
		return relatedness;
	}

	public void setRelatedness(double relatedness)
	{
		this.relatedness = relatedness;
	}

	@Override
	public String key()
	{
		return title;
	}

	@Override
	public int compareTo(Link o)
	{
		return Double.compare(this.getRelatedness(), o.getRelatedness());
	}

}
