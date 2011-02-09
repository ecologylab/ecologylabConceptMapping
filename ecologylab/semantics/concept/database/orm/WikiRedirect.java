package ecologylab.semantics.concept.database.orm;

public class WikiRedirect
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

	private String	fromTitle;

	private String	toTitle;

	public String getFromTitle()
	{
		return fromTitle;
	}

	public void setFromTitle(String fromTitle)
	{
		this.fromTitle = fromTitle;
	}

	public String getToTitle()
	{
		return toTitle;
	}

	public void setToTitle(String toTitle)
	{
		this.toTitle = toTitle;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof WikiRedirect)
		{
			if (fromTitle.equals(((WikiRedirect) obj).fromTitle))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return fromTitle.hashCode();
	}

}
