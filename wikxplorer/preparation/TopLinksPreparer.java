package wikxplorer.preparation;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.preparation.postparsing.WikiLink;

public class TopLinksPreparer
{
	
	private Session session1;
	
	private Session session2;
	
	private int counter;
	
	public void prepare(int numOfPreparedConcepts)
	{
		session1 = SessionManager.newSession();
		counter = 0;
		
		// find top linked concepts
		Criteria q = session1.createCriteria(WikiLink.class);
		q.setCacheable(false);
		q.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("toId"), "id")
				.add(Projections.count("toId"), "count")
				);
//		q.addOrder(Order.desc("count"));
		q.setCacheMode(CacheMode.IGNORE);
		q.setFetchSize(100);
		q.setMaxResults(100);
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		while (sr.next())
		{
			int id = sr.getInteger(0);
			counter++;
			prepareConcept(id);
		}
		sr.close();
		
		session1.close();
		if (session2 != null)
			session2.close();
	}

	private void prepareConcept(int id)
	{
		if (session2 == null)
			session2 = SessionManager.newSession();
		
		WikiConcept concept = WikiConcept.getById(id, session2);
		if (concept != null)
		{
			concept.getOrCalculateTopRelatedInlinks(session2);
			concept.getOrCalculateTopRelatedOutlinks(session2);
			session2.evict(concept);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		TopLinksPreparer tlp = new TopLinksPreparer();
		tlp.prepare(100);
	}

}
