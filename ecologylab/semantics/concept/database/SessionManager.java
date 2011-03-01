package ecologylab.semantics.concept.database;

import java.io.File;

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

	private static Session session;
	
	static
	{
		System.err.println("\nSetting up ...\n\n");
		
		Configuration config = new Configuration();
		SessionFactory sessionFactory = config.configure(new File("hibernate.cfg.xml")).buildSessionFactory();
		session = sessionFactory.openSession();
		
		// register a clean up hook
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				cleanUp();
			}
		});
		Runtime.getRuntime().addShutdownHook(t);
	}
	
	/**
	 * Get the global session for use.
	 * 
	 * @return
	 */
	public static Session getSession()
	{
		return session;
	}

	/**
	 * Close the session for clean-up.
	 * 
	 */
	private static void cleanUp()
	{
		if (session != null)
		{
			System.err.println("\nCleaning up ...\n\n");
			session.flush();
			session.close();
		}
	}

}
