package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.criterion.Property;
import org.hibernate.type.StandardBasicTypes;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.preparation.postparsing.WikiLink;
import ecologylab.semantics.concept.service.Configs;

@Entity
@Table(name = "wiki_concepts")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WikiConcept implements Serializable
{

	private static final String	CONFIG_TOTAL_CONCEPT_COUNT	= "db.total_concept_count";

	public static final double				MIN_DIST				= 0;

	public static final double				MAX_DIST				= 1;

	/**
	 * (This id comes from wikipedia)
	 */
	@Id
	@Column(name = "id", nullable = false)
	private int												id;

	/**
	 * concept title (or name).
	 */
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "title", nullable = false)
	private String										title;

	/**
	 * here text are pure text (after rendering to HTML), not wiki markups.
	 */
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "text", nullable = false)
	private String										text;

	/**
	 * inlinks.
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "wiki_links",
			joinColumns = @JoinColumn(name = "to_id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "from_id", nullable = false))
	private Set<WikiConcept>					inlinks;

	/**
	 * outlinks.
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "wiki_links",
			joinColumns = @JoinColumn(name = "from_id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "to_id", nullable = false))
	private Set<WikiConcept>					outlinks;

	/**
	 * most related inlinks.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "top_inlinks", joinColumns = @JoinColumn(name = "to_id"))
	@Column(name = "relatedness", nullable = false)
	@MapKeyJoinColumn(name = "from_id")
	private Map<WikiConcept, Double>	topRelatedInlinks;

	/**
	 * most related outlinks.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "top_outlinks", joinColumns = @JoinColumn(name = "from_id"))
	@Column(name = "relatedness", nullable = false)
	@MapKeyJoinColumn(name = "to_id")
	private Map<WikiConcept, Double>	topRelatedOutlinks;

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

	public Set<WikiConcept> getInlinks()
	{
		return inlinks;
	}

	public Set<WikiConcept> getOutlinks()
	{
		return outlinks;
	}

	public Map<WikiConcept, Double> getTopRelatedInlinks()
	{
		return topRelatedInlinks;
	}

	public Map<WikiConcept, Double> getTopRelatedOutlinks()
	{
		return topRelatedOutlinks;
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

	/**
	 * get relatedness of this concept to another one from table 'relatedness' or by calculation.
	 * 
	 * @param concept
	 * @return
	 */
	public double getRelatedness(WikiConcept concept)
	{
		if (this.getId() > concept.getId())
			return concept.getRelatedness(this);

		if (this.getId() == concept.getId())
			return MIN_DIST;
		
		Session session = SessionManager.newSession();
		Relatedness relEntity = new Relatedness();
		relEntity.setConceptId1(this.getId());
		relEntity.setConceptId2(concept.getId());
		relEntity = (Relatedness) session.get(Relatedness.class, relEntity);
		
		double r = MAX_DIST;
		if (relEntity == null)
		{
			SQLQuery q = session.createSQLQuery("SELECT calculate_relatedness(?, ?, ?) AS rel;");
			q.addScalar("rel", StandardBasicTypes.DOUBLE);
			q.setInteger(0, this.getId());
			q.setInteger(1, concept.getId());
			q.setInteger(2, Configs.getInt(CONFIG_TOTAL_CONCEPT_COUNT));
			r = (Double) q.uniqueResult();
		}
		else
		{
			r = relEntity.getRelatedness();
		}
		
		session.close();
		return r;
	}

	/**
	 * get most related inlinks from table 'top_related_links' or calculate and persist them.
	 * 
	 * @param session
	 * @return
	 */
	public Map<WikiConcept, Double> getOrCalculateTopRelatedInlinks(Session session)
	{
		Map<WikiConcept, Double> topInlinks = getTopRelatedInlinks();
		if (topInlinks.size() <= 0)
		{
			SQLQuery q = session.createSQLQuery("SELECT calculate_top_inlinks(?, ?);");
			q.setInteger(0, this.getId()).setInteger(1, Configs.getInt(CONFIG_TOTAL_CONCEPT_COUNT));
			q.setCacheable(false);
			q.list();
			session.refresh(this);
			topInlinks = getTopRelatedInlinks();
		}
		return topInlinks;
	}

	/**
	 * get most related outlinks from table 'top_related_links' or calculate and persist them.
	 * 
	 * @param session
	 * @return
	 */
	public Map<WikiConcept, Double> getOrCalculateTopRelatedOutlinks(Session session)
	{
		Map<WikiConcept, Double> topOutlinks = getTopRelatedOutlinks();
		if (topOutlinks.size() <= 0)
		{
			SQLQuery q = session.createSQLQuery("SELECT calculate_top_outlinks(?, ?);");
			q.setInteger(0, this.getId()).setInteger(1, Configs.getInt(CONFIG_TOTAL_CONCEPT_COUNT));
			q.setCacheable(false);
			q.list();
			session.refresh(this);
			topOutlinks = getTopRelatedOutlinks();
		}
		return topOutlinks;
	}

	public boolean isInlink(String title, Session session)
	{
		WikiConcept c = getByTitle(title, session);
		Criteria q = session.createCriteria(WikiLink.class);
		q.add(Property.forName("fromId").eq(c.getId())).add(Property.forName("toId").eq(this.getId()));
		return q.list().size() > 0;
	}

	public boolean isOutlink(String title, Session session)
	{
		WikiConcept c = getByTitle(title, session);
		Criteria q = session.createCriteria(WikiLink.class);
		q.add(Property.forName("fromId").eq(this.getId())).add(Property.forName("toId").eq(c.getId()));
		return q.list().size() > 0;
	}

	/**
	 * get concept with given title using given session.
	 * 
	 * @param title
	 * @param session
	 * @return
	 */
	public static WikiConcept getByTitle(String title, Session session)
	{
		// guesses for potential case problems
		char c0 = title.charAt(0);
		if (Character.isLowerCase(c0))
			title = Character.toUpperCase(c0) + title.substring(1);
		// TODO possibly other guesses for case problems

		// handle redirects
		String redirect = WikiRedirect.getRedirected(title);
		String trueTitle = (redirect == null) ? title : redirect;

		Criteria criteria = session.createCriteria(WikiConcept.class);
		criteria.add(Property.forName("title").eq(trueTitle));
		return (WikiConcept) criteria.uniqueResult();
	}

	/**
	 * get concept with give id using given session.
	 * 
	 * @param conceptId
	 * @param session
	 * @return
	 */
	public static WikiConcept getById(int conceptId, Session session)
	{
		return (WikiConcept) session.get(WikiConcept.class, conceptId);
	}

}
