package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "wiki_surfaces")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WikiSurface implements Serializable
{

	// @Generated(value = GenerationTime.INSERT)
	// @Id
	// @Column(name = "id", nullable = false, insertable = false, updatable = false)
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	// private int id;

	@Id
	@Column(name = "surface", nullable = false)
	private String										surface;

	@Column(name = "total_occurrence", nullable = false)
	private int												totalOccurrence;

	@Column(name = "linked_occurrence", nullable = false)
	private int												linkedOccurrence;

	/* we assume that one surface can relate to only a reasonable number of concepts. */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "commonness", joinColumns = @JoinColumn(name = "surface"))
	@Column(name = "commonness", nullable = true)
	@MapKeyJoinColumn(name = "concept_id")
	private Map<WikiConcept, Double>	concepts;

	public String getSurface()
	{
		return surface;
	}

	public void setSurface(String surface)
	{
		this.surface = surface;
	}

	public int getTotalOccurrence()
	{
		return totalOccurrence;
	}

	public void setTotalOccurrence(int totalOccurrence)
	{
		this.totalOccurrence = totalOccurrence;
	}

	public int getLinkedOccurrence()
	{
		return linkedOccurrence;
	}

	public void setLinkedOccurrence(int linkedOccurrence)
	{
		this.linkedOccurrence = linkedOccurrence;
	}

	public double getKeyphraseness()
	{
		if (getTotalOccurrence() == 0)
			return 0;
		return 1.0 * getLinkedOccurrence() / getTotalOccurrence();
	}

	public Map<WikiConcept, Double> getConcepts()
	{
		return concepts;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof WikiSurface)
		{
			if (surface.equals(((WikiSurface) obj).surface))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return surface.hashCode();
	}

	public static WikiSurface get(String surface, Session session)
	{
		return (WikiSurface) session.get(WikiSurface.class, surface);
	}

}
