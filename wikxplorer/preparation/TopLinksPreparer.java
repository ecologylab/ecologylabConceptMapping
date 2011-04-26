package wikxplorer.preparation;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import ecologylab.generic.HashMapArrayList;
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
		q.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("toId"), "id")
				.add(Projections.count("toId"), "count")
				);
		q.addOrder(Order.desc("count"));
		q.setCacheMode(CacheMode.IGNORE);
		q.setFetchSize(100);
		q.setMaxResults(1000);
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		while (sr.next())
		{
			int id = sr.getInteger(0);
			prepareConcept(id);
		}
	}

	private void prepareConcept(int id)
	{
		if (session2 == null)
			session2 = SessionManager.newSession();
		
		WikiConcept concept = WikiConcept.getById(id, session2);
		if (concept != null)
		{
			Map<WikiConcept, String> inlinks = concept.getInlinks();
			Map<WikiConcept, String> outlinks = concept.getOutlinks();
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
