package ecologylab.semantics.concept.preparation.postparsing;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * (Used only in CommonnessCalculator, for efficiency)
 * 
 * @author quyin
 *
 */
@Entity
@Table(name = "commonness")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Commonness implements Serializable
{

	@Id
	@Column(name = "surface", nullable = false)
	private String	surface;

	@Id
	@Column(name = "concept_id", nullable = false)
	private int			conceptId;

	@Column(name = "commonness", nullable = false)
	private double	commonness;

	public String getSurface()
	{
		return surface;
	}

	public void setSurface(String surface)
	{
		this.surface = surface;
	}

	public int getConceptId()
	{
		return conceptId;
	}

	public void setConceptId(int conceptId)
	{
		this.conceptId = conceptId;
	}

	public double getCommonness()
	{
		return commonness;
	}

	public void setCommonness(double commonness)
	{
		this.commonness = commonness;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Commonness)
		{
			Commonness other = (Commonness) obj;
			if (surface.equals(other.surface) && conceptId == other.conceptId)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int a = surface.hashCode();
		long pairing = (long) (a + conceptId) * (a + conceptId + 1) / 2 + conceptId;
		return (int) (pairing % 2147483647);
	}

}
