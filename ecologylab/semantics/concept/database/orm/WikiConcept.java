package ecologylab.semantics.concept.database.orm;

public class WikiConcept
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

	private int			wikiId;

	private String	title;

	private String	text;

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	public void setWikiId(int wikiId)
	{
		this.wikiId = wikiId;
	}

	public int getWikiId()
	{
		return wikiId;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof WikiConcept)
		{
			if (wikiId == ((WikiConcept) obj).wikiId)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return wikiId;
	}

}
