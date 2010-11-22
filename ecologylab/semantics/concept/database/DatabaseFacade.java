package ecologylab.semantics.concept.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import ecologylab.generic.Debug;

public class DatabaseFacade extends Debug
{

	private static DatabaseFacade	the	= null;

	public static DatabaseFacade get()
	{
		if (the == null)
		{
			synchronized (DatabaseFacade.class)
			{
				if (the == null)
					the = new DatabaseFacade();
			}
		}

		return the;
	}

	private Connection											conn;

	private int															totalConceptCount			= 3000000;

	private Object													lockTotalConceptCount	= new Object();

	private Map<String, PreparedStatement>	preparedStatements		= new HashMap<String, PreparedStatement>();

	private DatabaseFacade()
	{
		try
		{
			Class.forName("org.postgresql.Driver");

			// String url = "jdbc:postgresql://achilles.cse.tamu.edu/wikiparsing";
			String url = "jdbc:postgresql://achilles.cse.tamu.edu/wikiparsing2";
			String username = "quyin";
			String password = "quyindbpwd";
			conn = DriverManager.getConnection(url, username, password);
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

	public int getTotalConceptCount()
	{
		if (totalConceptCount < 0)
		{
			synchronized (lockTotalConceptCount)
			{
				if (totalConceptCount < 0)
				{
					try
					{
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery("SELECT count(*) FROM freq_concept;");
						if (rs.next())
							totalConceptCount = (int) rs.getLong(1);
					}
					catch (SQLException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return totalConceptCount;
	}

	public void executeSql(String sql) throws SQLException
	{
		Statement st = conn.createStatement();
		st.execute(sql);
		st.close();
	}

	public int executeUpdateSql(String sql) throws SQLException
	{
		Statement st = conn.createStatement();
		int rst = st.executeUpdate(sql);
		st.close();
		return rst;
	}

	/**
	 * BE CAREFUL! you need to close the statement and its result set (if any) manually after use.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Statement getStatement() throws SQLException
	{
		return conn.createStatement();
	}

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
	
	/**
	 * query for keyphraseness from the database.
	 * 
	 * @param surface
	 * @return the keyphraseness value, or 0 if surface not found
	 * @throws SQLException
	 */
	public double queryKeyphraseness(String surface)
	{
		double kp = 0;

		try
		{
			CallableStatement cst = conn.prepareCall("{ ? = call query_keyphraseness(?) }");
			cst.registerOutParameter(1, Types.DOUBLE);

			cst.setString(2, surface);
			cst.execute();
			kp = cst.getDouble(1);
			cst.close();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return kp;
	}

	/**
	 * query for all the senses of a given surface. note that this method queries commonness table, so
	 * it can't be used before commonness is computed.
	 * 
	 * @param surface
	 * @return a map from sense (concept) to commonness.
	 * @throws SQLException
	 */
	public Map<String, Double> querySenses(String surface)
	{
		Map<String, Double> rst = new HashMap<String, Double>();

		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT * FROM query_senses(?);");
			ResultSet rs = null;
			synchronized (pst)
			{
				pst.setString(1, surface);
				rs = pst.executeQuery();
			}
			if (rs != null)
			{
				while (rs.next())
				{
					rst.put(rs.getString("concept"), rs.getDouble("commonness"));
				}
				rs.close();
			}
			pst.close();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			System.err.println("surface=" + surface);
			e.printStackTrace();
		}

		return rst;
	}

	/**
	 * return how many concepts are linking to this one.
	 * 
	 * @param toConcept
	 * @return
	 */
	public int queryInlinkCount(String toConcept)
	{
		int inlinkCount = 0;

		try
		{
			CallableStatement cst = conn.prepareCall("{ ? = call query_inlink_count(?) }");
			cst.registerOutParameter(1, Types.INTEGER);
			cst.setString(2, toConcept);
			cst.execute();
			inlinkCount = cst.getInt(1);
			cst.close();
		}
		catch (SQLException e)
		{
			System.err.println("toConcept=" + toConcept);
			e.printStackTrace();
		}

		return inlinkCount;
	}

	public int queryCommonInlinkCount(String concept1, String concept2)
	{
		int commonInlinkCount = 0;

		try
		{
			CallableStatement cst = conn.prepareCall("{ ? = call query_common_inlink_count(?, ?) }");
			cst.registerOutParameter(1, Types.BIGINT);
			cst.setString(2, concept1);
			cst.setString(3, concept2);
			cst.execute();
			commonInlinkCount = (int) cst.getLong(1);
			cst.close();
		}
		catch (SQLException e)
		{
			System.err.println("concept1=" + concept1 + ", concept2=" + concept2);
			e.printStackTrace();
		}

		return commonInlinkCount;
	}

}
