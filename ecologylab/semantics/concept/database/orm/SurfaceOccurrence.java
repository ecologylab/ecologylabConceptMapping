package ecologylab.semantics.concept.database.orm;

public class SurfaceOccurrence
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

	private int			total;

	private int			linked;

	public String getSurface()
	{
		return surface;
	}

	public void setSurface(String surface)
	{
		this.surface = surface;
	}

	public int getTotal()
	{
		return total;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}

	public int getLinked()
	{
		return linked;
	}

	public void setLinked(int linked)
	{
		this.linked = linked;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof SurfaceOccurrence)
		{
			if (surface.equals(((SurfaceOccurrence) obj).surface))
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
