package ecologylab.semantics.concept.database.orm;

public class Relatedness
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

	private int			conceptId1;

	private int			conceptId2;

	private double	relatedness;

	public int getConceptId1()
	{
		return conceptId1;
	}

	public void setConceptId1(int conceptId1)
	{
		this.conceptId1 = conceptId1;
	}

	public int getConceptId2()
	{
		return conceptId2;
	}

	public void setConceptId2(int conceptId2)
	{
		this.conceptId2 = conceptId2;
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
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Relatedness)
		{
			Relatedness other = (Relatedness) obj;
			if (conceptId1 == other.conceptId1 && conceptId2 == other.conceptId2)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		long pairing = (long) (conceptId1 + conceptId2) * (conceptId1 + conceptId2 + 1) / 2 + conceptId2;
		return (int) (pairing % 2147483647);
	}

}
