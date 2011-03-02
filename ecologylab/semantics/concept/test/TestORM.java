package ecologylab.semantics.concept.test;

import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.Commonness;
import ecologylab.semantics.concept.database.orm.WikiLink;

public class TestORM
{

	static void test1()
	{
		Session sess = SessionManager.getSession();
		sess.beginTransaction();

		Commonness comm = new Commonness();
		comm.setSurface("abc");
		comm.setConceptId(123);
		comm.setCommonness(0.5);
		sess.save(comm);

		sess.getTransaction().commit();

		sess.beginTransaction();

		Commonness query = new Commonness();
		query.setSurface("abc");
		query.setConceptId(123);
		query = (Commonness) sess.get(Commonness.class, query);
		System.out.println(query.getCommonness());

		sess.getTransaction().commit();
	}

	static void test2()
	{
		org.hibernate.Session sess = SessionManager.getSession();
		
		sess.beginTransaction();
		WikiLink wl1 = new WikiLink();
		wl1.setFromId(1);
		wl1.setToId(2);
		wl1.setSurface("abc");
		sess.save(wl1);
		sess.getTransaction().commit();
		System.out.println("wiki_link1.seq_id: " + wl1.getSeqId());
		
		sess.beginTransaction();
		WikiLink wl2 = new WikiLink();
		wl2.setFromId(1);
		wl2.setToId(2);
		wl2.setSurface("abc");
		sess.save(wl2);
		sess.getTransaction().commit();
		System.out.println("wiki_link2.seq_id: " + wl2.getSeqId());

		sess.flush();
	}

	public static void main(String[] args)
	{
		test1();
	}

}
