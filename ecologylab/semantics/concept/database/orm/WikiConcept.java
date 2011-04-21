package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.criterion.Property;

import ecologylab.semantics.concept.service.Configs;

@Entity
@Table(name = "wiki_concepts")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WikiConcept implements Serializable
{

	public static final double				MIN_DIST	= 0;

	public static final double				MAX_DIST	= 1;

	/**
	 * (This id comes from wikipedia)
	 */
	@Id
	@Column(name = "id", nullable = false)
	private int												id;

	@Column(name = "title", nullable = false)
	private String										title;

	/**
	 * here text are pure text (after rendering to HTML), not wiki markups.
	 */
	@Column(name = "text", nullable = false)
	private String										text;

	@ElementCollection
	@CollectionTable(name = "wiki_links", joinColumns = @JoinColumn(name = "to_id"))
	@Column(name = "surface", nullable = false)
	@MapKeyJoinColumn(name = "from_id")
	private Map<WikiConcept, String>	inlinks;

	@ElementCollection
	@CollectionTable(name = "wiki_links", joinColumns = @JoinColumn(name = "from_id"))
	@Column(name = "surface", nullable = false)
	@MapKeyJoinColumn(name = "to_id")
	private Map<WikiConcept, String>	outlinks;

	@ElementCollection
	@CollectionTable(name = "relatedness", joinColumns = @JoinColumn(name = "concept_id1"))
	@Column(name = "relatedness", nullable = false)
	@MapKeyJoinColumn(name = "concept_id2")
	private Map<WikiConcept, Double>	relatedConcepts;

	@ElementCollection
	@CollectionTable(name = "commonness", joinColumns = @JoinColumn(name = "concept_id"))
	@Column(name = "commonness", nullable = false)
	@MapKeyJoinColumn(name = "surface")
	private Map<WikiSurface, Double>	surfaces;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	public Map<WikiConcept, String> getInlinks()
	{
		return inlinks;
	}

	public Map<WikiConcept, String> getOutlinks()
	{
		return outlinks;
	}

	public void setRelatedConcepts(Map<WikiConcept, Double> relatedConcepts)
	{
		this.relatedConcepts = relatedConcepts;
	}

	public Map<WikiConcept, Double> getRelatedConcepts()
	{
		return relatedConcepts;
	}

	public Map<WikiSurface, Double> getSurfaces()
	{
		return surfaces;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof WikiConcept)
		{
			if (id == ((WikiConcept) obj).id)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	public double getRelatedness(WikiConcept concept, Session session)
	{
		if (this.getId() > concept.getId())
			return concept.getRelatedness(this, session);

		if (this.getId() == concept.getId())
			return MIN_DIST;

		Map<WikiConcept, Double> rels = this.getRelatedConcepts();
		if (rels.containsKey(concept))
			return rels.get(concept);

		int scommon = 0;
		for (WikiConcept c : concept.getInlinks().keySet())
			if (this.getInlinks().containsKey(c))
				scommon++;

		if (scommon == 0)
			return MAX_DIST;

		int s1 = this.getInlinks().size();
		int s2 = concept.getInlinks().size();
		int smax = s1 > s2 ? s1 : s2;
		int smin = s1 > s2 ? s2 : s1;
		int total = Configs.getInt("db.total_concept_count");

		double r = (Math.log(smax) - Math.log(scommon)) / (Math.log(total) - Math.log(smin));

		rels.put(concept, r);
		session.save(this);

		return r;
	}

	public static WikiConcept getByTitle(String title, Session session)
	{
		// guesses for potential case problems
		char c0 = title.charAt(0);
		if (Character.isLowerCase(c0))
			title = Character.toUpperCase(c0) + title.substring(1);
		// TODO possibly other guesses for case problems

		// handle redirects
		String redirect = WikiRedirect.getRedirected(title, session);
		String trueTitle = (redirect == null) ? title : redirect;

		Criteria criteria = session.createCriteria(WikiConcept.class);
		criteria.add(Property.forName("title").eq(trueTitle));
		return (WikiConcept) criteria.uniqueResult();
	}

	public static WikiConcept getById(int conceptId, Session session)
	{
		return (WikiConcept) session.get(WikiConcept.class, conceptId);
	}

}
