package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "dbp_records")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DbpRecord implements Serializable
{

	@Id
	@Column(name = "dbp_title", nullable = false)
	private String	dbpTitle;

	@Column(name = "wiki_title", nullable = false)
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
