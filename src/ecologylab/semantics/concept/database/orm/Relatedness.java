package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * (this class is only for use by WikiConcept to look up relatedness.)
 * 
 * @author quyin
 *
 */
@Entity
@Table(name = "relatedness")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Relatedness implements Serializable
{

	@Id
	@Column(name = "concept_id1", nullable = false)
	private int conceptId1;
	
	@Id
	@Column(name = "concept_id2", nullable = false)
	private int conceptId2;
	
	@Column(name = "relatedness", nullable = false)
	private double relatedness;
	
	public int getConceptId1()
	{
		return conceptId1;
	}

	public void setConceptId1(int conceptId1)
	{
		this.conceptId1 = conceptId1;
	}

	public int getConceptId2()
	{
		return conceptId2;
	}

	public void setConceptId2(int conceptId2)
	{
		this.conceptId2 = conceptId2;
	}

	public double getRelatedness()
	{
		return relatedness;
	}

	public void setRelatedness(double relatedness)
	{
		this.relatedness = relatedness;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Relatedness)
		{
			Relatedness other = (Relatedness) obj;
			if (conceptId1 == other.conceptId1 && conceptId2 == other.conceptId2)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		long pairing = (long) (conceptId1 + conceptId2) * (conceptId1 + conceptId2 + 1) / 2
				+ conceptId2;
		return (int) (pairing % 2147483647);
	}

}
