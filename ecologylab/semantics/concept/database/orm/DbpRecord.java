package ecologylab.semantics.concept.database.orm;

//@Entity
public class DbpRecord
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

	private String	dbpTitle;

	private String	wikiTitle;

	public String getDbpTitle()
	{
		return dbpTitle;
	}

	public void setDbpTitle(String dbpTitle)
	{
		this.dbpTitle = dbpTitle;
	}

	public String getWikiTitle()
	{
		return wikiTitle;
	}

	public void setWikiTitle(String wikiTitle)
	{
		this.wikiTitle = wikiTitle;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other != null && other instanceof DbpRecord)
		{
			if (dbpTitle.equals(((DbpRecord) other).dbpTitle))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return dbpTitle.hashCode();
	}
	
}
