package ecologylab.semantics.concept.database.orm;

public class Keyphraseness
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

	private double	keyphraseness;

	public String getSurface()
	{
		return surface;
	}

	public void setSurface(String surface)
	{
		this.surface = surface;
	}

	public double getKeyphraseness()
	{
		return keyphraseness;
	}

	public void setKeyphraseness(double keyphraseness)
	{
		this.keyphraseness = keyphraseness;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Keyphraseness)
		{
			if (surface.equals(((Keyphraseness)obj).surface))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return surface.hashCode();
	}

}
