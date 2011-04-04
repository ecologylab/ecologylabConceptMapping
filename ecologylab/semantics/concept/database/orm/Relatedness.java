package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.CollectionUtils;

@Entity
@Table(name = "relatedness")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Relatedness implements Serializable
{

	public static final double	MIN_DIST	= 0;

	public static final double	MAX_DIST	= 1;

	@Id
	@Column(name = "concept_id1", nullable = false)
	private int									conceptId1;

	@Id
	@Column(name = "concept_id2", nullable = false)
	private int									conceptId2;

	@Column(name = "relatedness", nullable = false)
	private double							relatedness;

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

	public static double get(int id1, int id2, Session session)
	{
		if (id1 > id2)
			return get(id2, id1, session);
		
		if (id1 == id2)
			return MIN_DIST;
		
		Relatedness rel = new Relatedness();
		rel.setConceptId1(id1);
		rel.setConceptId2(id2);
		Object rst = session.get(Relatedness.class, rel);
		if (rst != null)
		{
			return ((Relatedness) rst).getRelatedness();
		}

		List<WikiLink> l1 = WikiLink.getByDestination(id1, session);
		List<WikiLink> l2 = WikiLink.getByDestination(id2, session);
		List<WikiLink> lcommon = CollectionUtils.commonSublist(l1, l2, new Comparator<WikiLink>()
		{
			@Override
			public int compare(WikiLink o1, WikiLink o2)
			{
				return o1.getFromId() - o2.getFromId();
			}
		});

		int s1 = l1.size();
		int s2 = l2.size();
		int scommon = lcommon.size();
		if (scommon == 0)
			return MAX_DIST;
		int smax = s1 > s2 ? s1 : s2;
		int smin = s1 > s2 ? s2 : s1;
		int total = Configs.getInt("db.total_concept_count");
		
		double r = (Math.log(smax) - Math.log(scommon)) / (Math.log(total) - Math.log(smin));
		
		rel.setConceptId1(id1);
		rel.setConceptId2(id2);
		rel.setRelatedness(r);
		session.save(rel);
		session.flush();
		
		return r;
	}
	
}
