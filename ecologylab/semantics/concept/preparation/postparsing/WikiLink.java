package ecologylab.semantics.concept.preparation.postparsing;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * (Used only in parsing, for efficiency)
 * 
 * @author quyin
 *
 */
@Entity
@Table(name = "wiki_links")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WikiLink implements Serializable
{

	// @Generated(value = GenerationTime.INSERT)
	@Id
	@Column(name = "link_id", nullable = false, insertable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int	linkId;

	@Column(name = "from_id", nullable = false)
	private int	fromId;

	@Column(name = "to_id", nullable = false)
	private int	toId;

	@Column(name = "surface", nullable = false)
	String			surface;

	public int getLinkId()
	{
		return linkId;
	}

	public int getFromId()
	{
		return fromId;
	}

	public void setFromId(int fromId)
	{
		this.fromId = fromId;
	}

	public int getToId()
	{
		return toId;
	}

	public void setToId(int toId)
	{
		this.toId = toId;
	}

	public String getSurface()
	{
		return surface;
	}

	public void setSurface(String surface)
	{
		this.surface = surface;
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
		long pairing = (long) (fromId + toId) * (fromId + toId + 1) / 2 + toId;
		return (int) (pairing % 2147483647);
	}
	
	@Override
	public String toString()
	{
		return String.format("%d->%d:%s", getFromId(), getToId(), getSurface());
	}
	
}
