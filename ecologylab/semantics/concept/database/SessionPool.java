package ecologylab.semantics.concept.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.service.Configs;

/**
 * Manage Hibernate sessions.
 * 
 * @author quyin
 * 
 */
public class SessionPool extends Debug
{

	private static SessionFactory	factory;

	private static List<Session>	allSessions;

	private static Queue<Session>	pool;

	static
	{
		System.err.println("\nSetting up Hibernate SessionFactory ...\n\n");
		Configuration config = new Configuration();
		factory = config.configure("hibernate.cfg.xml").buildSessionFactory();

		int n = Configs.getInt("session_pool_size");
		if (n <= 0)
			n = 1;
		pool = new ConcurrentLinkedQueue<Session>();
		allSessions = new ArrayList<Session>();
		for (int i = 0; i < n; ++i)
		{
			Session session = factory.openSession();
			pool.add(session);
			allSessions.add(session);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				for (Session session : allSessions)
				{
					session.close();
				}
			}
		}));
	}

	public static Session getSession()
	{
		pool.poll
		synchronized (poolLock)
		{
			int k = -1;
			while (true)
			{
				for (int i = 0; i < states.length; ++i)
				{
					if (states[i] == 0)
					{
						k = i;
						break;
					}
				}

				if (k >= 0)
					break;

				try
				{
					poolLock.wait();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			states[k] = 1;
			return pool[k];
		}
	}

	public static void releaseSession(Session session)
	{

	}

}
