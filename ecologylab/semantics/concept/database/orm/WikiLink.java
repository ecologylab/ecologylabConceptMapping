package ecologylab.semantics.concept.database.orm;

public class WikiLink
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

	private int	fromId;

	private int	toId;

	String			surface;

	public String getSurface()
	{
		return surface;
	}

	public void setSurface(String surface)
	{
		this.surface = surface;
	}

	public void setFromId(int fromId)
	{
		this.fromId = fromId;
	}

	public int getFromId()
	{
		return fromId;
	}

	public void setToId(int toId)
	{
		this.toId = toId;
	}

	public int getToId()
	{
		return toId;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof WikiLink)
		{
			WikiLink other = (WikiLink) obj;
			if (fromId == other.fromId && toId == other.toId)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		// Cantor pairing function
		long pairing = (long)(fromId + toId) * (fromId + toId + 1) / 2 + toId;
		return (int) (pairing % 2147483647);
	}

}
