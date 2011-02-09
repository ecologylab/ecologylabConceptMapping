package ecologylab.semantics.concept.database.orm;

public class Commonness
{

	private int	id;

	public int getId()
	{
		return id;
	}

	private void setId(int id)
	{
		this.id = id;
	}

	private String	surface;

	private int			conceptId;

	private double	commonness;

	public String getSurface()
	{
		return surface;
	}

	public void setSurface(String surface)
	{
		this.surface = surface;
	}

	public int getConceptId()
	{
		return conceptId;
	}

	public void setConceptId(int conceptId)
	{
		this.conceptId = conceptId;
	}

	public double getCommonness()
	{
		return commonness;
	}

	public void setCommonness(double commonness)
	{
		this.commonness = commonness;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Commonness)
		{
			Commonness other = (Commonness) obj;
			if (surface.equals(other.surface) && conceptId == other.conceptId)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int a = surface.hashCode();
		long pairing = (long)(a + conceptId) * (a + conceptId + 1) / 2 + conceptId;
		return (int)(pairing % 2147483647);
	}

}
