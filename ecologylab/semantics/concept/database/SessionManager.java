package ecologylab.semantics.concept.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import ecologylab.generic.Debug;

/**
 * Manage Hibernate sessions.
 * 
 * @author quyin
 * 
 */
public class SessionManager extends Debug
{

	private static SessionFactory	factory;
	
	static
	{
		System.err.println("\nSetting up Hibernate SessionFactory ...\n\n");
		Configuration config = new Configuration();
		factory = config.configure("hibernate.cfg.xml").buildSessionFactory();
	}

	public static Session newSession()
	{
		return factory.openSession();
	}

	public static void clearSecondLevelCache()
	{
		factory.getCache().evictQueryRegions();
	}

}
