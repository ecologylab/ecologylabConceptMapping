package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "wiki_links")
public class WikiLink implements Serializable
{

	@Id
	@Column(name = "seq_id", nullable = false)
	private int	seqId;

	@Column(name = "from_id", nullable = false)
	private int	fromId;

	@Column(name = "to_id", nullable = false)
	private int	toId;

	@Column(name = "surface", nullable = false)
	String			surface;

	public int getSeqId()
	{
		return seqId;
	}

	public void setSeqId(int seqId)
	{
		this.seqId = seqId;
	}

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
		long pairing = (long) (fromId + toId) * (fromId + toId + 1) / 2 + toId;
		return (int) (pairing % 2147483647);
	}

}
