package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "wiki_redirects")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WikiRedirect implements Serializable
{

	@Id
	@Column(name = "from_title", nullable = false)
	private String	fromTitle;

	@Column(name = "to_title", nullable = false)
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

	public static String getRedirected(String fromTitle, Session openSession)
	{
		WikiRedirect redirect = (WikiRedirect) openSession.get(WikiRedirect.class, fromTitle);
		return redirect != null ? redirect.getToTitle() : null;
	}

}