package ecologylab.semantics.concept.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class DatabaseFacade extends Debug
{

	private static DatabaseFacade	the	= null;

	public static synchronized DatabaseFacade get()
	{
		if (the == null)
		{
			the = new DatabaseFacade();
		}

		return the;
	}

	public static final int									NUM_ALL_CONCEPTS		= 3056348;

	private Connection											conn;

	private Map<String, PreparedStatement>	preparedStatements	= new HashMap<String, PreparedStatement>();

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

	public synchronized void executeSql(String sql) throws SQLException
	{
		Statement st = conn.createStatement();
		st.execute(sql);
		st.close();
	}

	public synchronized int executeUpdateSql(String sql) throws SQLException
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
	public synchronized Statement getStatement() throws SQLException
	{
		return conn.createStatement();
	}

	public synchronized PreparedStatement getPreparedStatement(String sql)
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
		return preparedStatements.get(sql);
	}

	/**
	 * query for keyphraseness from the database.
	 * 
	 * @param surface
	 * @return the keyphraseness value, or 0 if surface not found
	 * @throws SQLException
	 */
	public synchronized double queryKeyphraseness(String surface)
	{
		double kp = 0;

		try
		{
			CallableStatement cst = conn.prepareCall("{ ? = call query_keyphraseness(?) }");
			cst.registerOutParameter(1, Types.DOUBLE);

			cst.setString(2, surface);
			cst.execute();
			kp = cst.getDouble(1);
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return kp;
	}

	/**
	 * query for relatedness, given two inlink concept list. it accepts two inlink concept list so
	 * that user can cache them for fast computation. note that smaller relatedness value indicates
	 * more relation between concepts.
	 * 
	 * @param inlinkList1
	 * @param inlinkList2
	 * @return
	 */
	public synchronized double queryRelatedness(List<String> inlinkList1, List<String> inlinkList2)
	{
		if (inlinkList1.equals(inlinkList2))
			return 0;

		int s1 = inlinkList1.size();
		int s2 = inlinkList2.size();
		List<String> commonSublist = CollectionUtils.commonSublist(inlinkList1, inlinkList2);
		int s = commonSublist.size();
		if (s <= 0)
			return 0; // or Math.log will fail

		int smin = ((s1 > s2) ? s2 : s1);
		int smax = ((s1 > s2) ? s1 : s2);

		int W = NUM_ALL_CONCEPTS;
		return (Math.log(smax) - Math.log(s)) / (Math.log(W) - Math.log(smin));
	}

	/**
	 * query for all the senses of a given surface. note that this method queries commonness table, so
	 * it can't be used before commonness is computed.
	 * 
	 * @param surface
	 * @return a map from sense (concept) to commonness.
	 * @throws SQLException
	 */
	public synchronized Map<String, Double> querySenses(String surface)
	{
		Map<String, Double> rst = new HashMap<String, Double>();

		try
		{
			PreparedStatement pst = getPreparedStatement("SELECT * FROM query_senses(?);");
			pst.setString(1, surface);
			ResultSet rs = pst.executeQuery();
			while (rs.next())
			{
				rst.put(rs.getString("concept"), rs.getDouble("commonness"));
			}
			rs.close();
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
	 * return a ASCENDINGLY ORDERED list of from_concept (source of a link) given a to_concept
	 * (destination of a link).
	 * 
	 * @param toConcept
	 * @return
	 * @throws SQLException
	 */
	public synchronized List<String> queryInlinkConceptsForConcept(String toConcept)
	{
		List<String> rst = new ArrayList<String>();

		try
		{
			PreparedStatement pst = getPreparedStatement("SELECT * FROM query_inlink_concepts(?);");
			pst.setString(1, toConcept);
			ResultSet rs = pst.executeQuery();
			while (rs.next())
			{
				rst.add(rs.getString("from_title"));
			}
			rs.close();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			System.err.println("toConcept=" + toConcept);
			e.printStackTrace();
		}

		// using Java's sort() instead of SQL ORDER BY, since ORDER BY seems to treat upper / lower
		// cases differently from sort().
		Collections.sort(rst);
		return rst;
	}

}
