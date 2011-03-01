package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "surface_occurrences")
public class SurfaceOccurrence implements Serializable
{

	@Id
	@Column(name = "surface", nullable = false)
	private String	surface;

	@Column(name = "total", nullable = false)
	private int			total;

	@Column(name = "linked", nullable = false)
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
