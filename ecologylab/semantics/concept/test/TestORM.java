package ecologylab.semantics.concept.test;

import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionPool;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;

public class TestORM
{

	static void test()
	{
		Session sess = SessionPool.get().getSession();
		
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sess.beginTransaction();
		WikiConcept c1 = WikiConcept.getByTitle("Cognitive science", sess);
		WikiConcept c2 = WikiConcept.getByTitle("Cognitive behavioral therapy", sess);
		
		System.out.print("Surfaces: ");
		for (WikiSurface s : c1.getSurfaces().keySet())
		{
			System.out.print(s.getSurface() + ", ");
		}
		System.out.println();
		
		System.out.print("Inlinks: ");
		for (WikiConcept inl : c1.getInlinks().keySet())
		{
			System.out.print(inl.getId() + ", ");
		}
		System.out.println();
		
		System.out.print("Outlinks: ");
		for (WikiConcept oul : c1.getOutlinks().keySet())
		{
			System.out.print(oul.getId() + ", ");
		}
		System.out.println();
		
		System.out.println(c1.getRelatedness(c2, sess));
		sess.getTransaction().commit();
		
		SessionPool.get().releaseSession(sess);
	}

	public static void main(String[] args)
	{
		test();
		SessionPool.get().closeAllSessions();
	}

}
