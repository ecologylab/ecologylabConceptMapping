package ecologylab.semantics.concept.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.serialization.SIMPLTranslationException;

/**
 * provide facade methods for upper layers to access data.
 * 
 * @author quyin
 * 
 */
public class DatabaseFacade extends Debug
{

	private static DatabaseFacade	the	= null;

	/**
	 * get the global singleton instance
	 * 
	 * @return
	 */
	public static DatabaseFacade get()
	{
		if (the == null)
		{
			synchronized (DatabaseFacade.class)
			{
				if (the == null)
				{
					try
					{
						the = new DatabaseFacade();
					}
					catch (SIMPLTranslationException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return the;
	}

	private Connection											conn;

	private Map<String, PreparedStatement>	preparedStatements	= new HashMap<String, PreparedStatement>();

	/**
	 * create database connection using prefs, and set up a clean-up hook
	 * 
	 * @throws SIMPLTranslationException
	 */
	private DatabaseFacade() throws SIMPLTranslationException
	{
		try
		{
			String driverClass = Configs.getString("db.driver_class");
			String url = Configs.getString("db.url");
			String user = Configs.getString("db.user");
			String password = Configs.getString("db.password");

			if (driverClass != null)
				Class.forName(driverClass);
			if (url != null && user != null && password != null)
				conn = DriverManager.getConnection(url, user, password);

			debug("database connected");
		}
		catch (ClassNotFoundException e)
		{
			error("database driver not found!");
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			error("database connection failed.");
			e.printStackTrace();
		}

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
	 * clean-up hook. close all open prepared statements. close the connection.
	 * 
	 */
	private void cleanUp()
	{
		// clean up prepared statements & connection
		if (preparedStatements != null)
		{
			debug("cleaning up prepared statements ...");
			for (String sql : preparedStatements.keySet())
			{
				try
				{
					PreparedStatement pst = preparedStatements.get(sql);
					if (!pst.isClosed())
						pst.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			preparedStatements = null;
			debug("done.");
		}

		if (conn != null)
		{
			try
			{
				debug("closing database connection ...");
				if (!conn.isClosed())
					conn.close();
				debug("done.");
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * create a statement. remember to close it after use.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Statement getStatement() throws SQLException
	{
		return conn.createStatement();
	}

	/**
	 * get a prepared statement from prepared statement pool, or create a new one if it is not there.
	 * each prepared statement will be synchronized (at each time at most one operation is using it).
	 * 
	 * @param sql
	 * @return
	 */
	public PreparedStatement getPreparedStatement(String sql)
	{
		if (!preparedStatements.containsKey(sql))
		{
			synchronized (preparedStatements)
			{
				if (!preparedStatements.containsKey(sql))
				{
					try
					{
						PreparedStatement pst = conn.prepareStatement(sql);
						preparedStatements.put(sql, pst);
					}
					catch (SQLException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				}
			}
		}
		return preparedStatements.get(sql);
	}

}
