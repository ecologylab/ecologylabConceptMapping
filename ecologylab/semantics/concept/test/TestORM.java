package ecologylab.semantics.concept.test;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

import ecologylab.semantics.concept.database.orm.Commonness;

public class TestORM
{

	public static void main(String[] args)
	{
		Configuration config = new Configuration();
		SessionFactory sessFact = config.configure(new File("hibernate.cfg.xml")).buildSessionFactory();
		Session sess = sessFact.openSession();
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

}
