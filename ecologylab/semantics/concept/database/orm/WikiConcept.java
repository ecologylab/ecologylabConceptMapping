package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.criterion.Property;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.preparation.postparsing.WikiLink;
import ecologylab.semantics.concept.service.Configs;

@Entity
@Table(name = "wiki_concepts")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WikiConcept implements Serializable
{

	public static final double				MIN_DIST				= 0;

	public static final double				MAX_DIST				= 1;

	private static final int					TOP_LINK_COUNT	= 100;

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
	@CollectionTable(name = "top_related_links", joinColumns = @JoinColumn(name = "to_id"))
	@Column(name = "relatedness", nullable = false)
	@MapKeyJoinColumn(name = "from_id")
	private Map<WikiConcept, Double>	topRelatedInlinks;

	/**
	 * most related outlinks.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "top_related_links", joinColumns = @JoinColumn(name = "from_id"))
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

		Relatedness rel = new Relatedness();
		rel.setConceptId1(this.getId());
		rel.setConceptId2(concept.getId());

		Relatedness existingRel = (Relatedness) session.get(Relatedness.class, rel);
		if (existingRel != null)
			return existingRel.getRelatedness();

		session.beginTransaction();

		int scommon = 0;
		for (WikiConcept c : concept.getInlinks())
			if (this.getInlinks().contains(c))
				scommon++;

		if (scommon == 0)
			return MAX_DIST;

		int s1 = this.getInlinks().size();
		int s2 = concept.getInlinks().size();
		int smax = s1 > s2 ? s1 : s2;
		int smin = s1 > s2 ? s2 : s1;
		int total = Configs.getInt("db.total_concept_count");

		double r = (Math.log(smax) - Math.log(scommon)) / (Math.log(total) - Math.log(smin));

		rel.setRelatedness(r);
		session.save(rel);

		session.getTransaction().commit();
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
			Set<WikiConcept> tmpInlinks = getInlinks();
			calculateTopRelatedLinks(session, topInlinks, tmpInlinks);
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
			Set<WikiConcept> tmpOutlinks = getOutlinks();
			calculateTopRelatedLinks(session, topOutlinks, tmpOutlinks);
		}
		return topOutlinks;
	}

	/**
	 * utility method used by getOrCalculatetopRelated*links().
	 * 
	 * @param session
	 * @param buffer
	 *          for output.
	 * @param allLinks
	 *          either all inlinks or all outlinks.
	 */
	private void calculateTopRelatedLinks(Session session, Map<WikiConcept, Double> buffer,
			Set<WikiConcept> allLinks)
	{
		List<RelatedLinkRecord> records = new ArrayList<RelatedLinkRecord>();
		for (WikiConcept inlink : allLinks)
		{
			double rel = this.getRelatedness(inlink);
			if (rel > MIN_DIST)
				records.add(new RelatedLinkRecord(inlink, rel));
		}
		Collections.sort(records);
		Transaction tx = session.beginTransaction();
		for (int i = 0; i < records.size() && i < TOP_LINK_COUNT; ++i)
		{
			RelatedLinkRecord record = records.get(i);
			buffer.put(record.getRelatedLink(), record.getRelatedness());
		}
		session.update(this);
		tx.commit();
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
