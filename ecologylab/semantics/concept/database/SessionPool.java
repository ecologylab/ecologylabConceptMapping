package ecologylab.semantics.concept.database;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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

	public static int						MIN_POOL_SIZE	= 4;

	private static SessionPool	the						= null;

	public static SessionPool get()
	{
		if (the == null)
		{
			synchronized (SessionPool.class)
			{
				if (the == null)
				{
					int n = Configs.getInt("session_pool_size");
					if (n <= MIN_POOL_SIZE)
						n = MIN_POOL_SIZE;
					the = new SessionPool(n);
				}
			}
		}
		return the;
	}

	private SessionFactory	factory;

	private Queue<Session>	pool;

	private Set<Session>		loaned;

	private Object					lock	= new Object();

	private SessionPool(int poolSize)
	{
		System.err.println("\nSetting up Hibernate SessionFactory ...\n\n");
		Configuration config = new Configuration();
		factory = config.configure("hibernate.cfg.xml").buildSessionFactory();

		pool = new LinkedList<Session>();
		loaned = new HashSet<Session>();
		for (int i = 0; i < poolSize; ++i)
		{
			Session session = createSession();
			pool.add(session);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				closeAllSessions();
			}
		}));
	}

	/**
	 * abstraction of creating a new session.
	 * 
	 * @return
	 */
	protected Session createSession()
	{
		return factory.openSession();
	}

	/**
	 * obtain a new session from the pool. if the pool is empty, the thread blocks.
	 * 
	 * @return
	 */
	public Session getSession()
	{
		synchronized (lock)
		{
			while (pool.size() <= 0)
			{
				try
				{
					lock.wait();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Session session = pool.poll();
			if (!session.isOpen())
				session = createSession();
			loaned.add(session);
			return session;
		}
	}

	/**
	 * release a used session so that it can be reused by other threads.
	 * 
	 * @param session
	 */
	public void releaseSession(Session session)
	{
		synchronized (lock)
		{
			if (loaned.contains(session))
			{
				loaned.remove(session);
				pool.offer(session);
				lock.notifyAll();
			}
		}
	}

	/**
	 * close all sessions. call at the end of the program.
	 */
	public void closeAllSessions()
	{
		for (Session session : pool)
		{
			if (session != null && session.isOpen())
				session.close();
		}
		for (Session session : loaned)
		{
			if (session != null && session.isOpen())
				session.close();
		}
	}

}
