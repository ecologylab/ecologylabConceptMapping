package ecologylab.semantics.concept.test;

import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.preparation.postparsing.WikiLink;

public class TestORM
{

	static void test()
	{
		Session session = SessionManager.newSession();
		
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		WikiConcept c1 = WikiConcept.getByTitle("Cognitive science", session);
		WikiConcept c2 = WikiConcept.getByTitle("Cognitive behavioral therapy", session);
		WikiConcept c3 = WikiConcept.getByTitle("Cognition", session);
		
		double r1 = c1.getRelatedness(c2);
		double r2 = c1.getRelatedness(c3);
		
		System.out.println(r1);
		System.out.println(r2);
		
		session.close();
	}
	
	static void test2()
	{
		Session session = SessionManager.newSession();
		
		Criteria q = session.createCriteria(WikiLink.class);
		q.setFetchSize(10);
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		while (sr.next())
		{
			WikiLink link = (WikiLink) sr.get(0);
			System.out.println(link.getFromId() + ", " + link.getToId() + ", " + link.getSurface());
		}
	}
	
	static void test3()
	{
		Session session = SessionManager.newSession();
		
		WikiSurface ws = WikiSurface.get("0", session);
		
		Transaction tx = session.beginTransaction();
		ws.setTotalOccurrence(0);
		tx.commit();
		
		session.close();
	}

	public static void main(String[] args)
	{
		test3();
	}

}
