package wikxplorer.messages;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.types.element.Mappable;

/**
 * The class used to hold information about a concept during transmission between server and
 * clients.
 * <p />
 * In cases like SingleSourceRelatedness, clients should fill out the title field and the server
 * will return information in relatedness field. In cases like SuggestedConcepts, the server will
 * fill out all the information.
 * <p />
 * This class implements Mappable so that it can be used with simpl_map. The key will be the title.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class Concept extends ElementState implements Mappable<String>
{

	public static final int	ERROR		= -1;

	public static final int	NONE		= 0;

	// use bits as flags
	
	public static final int	OUTLINK	= 0x1;

	public static final int	INLINK	= 0x2;

	/**
	 * The title of this concept.
	 */
	@simpl_scalar
	private String					title;

	/**
	 * The type of this concept (inlink / outlink). When set to NONE, the type is unclear.
	 */
	@simpl_scalar
	private int							type		= NONE;

	/**
	 * The returned relatedness value of this concept to a source one (if used with
	 * SingleSourceRelatedness or SuggestedConcepts).
	 */
	@simpl_scalar
	private double					relatedness;

	@Override
	public String key()
	{
		return title;
	}

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

}
