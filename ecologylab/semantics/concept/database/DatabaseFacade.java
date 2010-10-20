package ecologylab.semantics.concept.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	public static final int	NUM_ALL_CONCEPTS	= 3056348;

	private Connection			conn;

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
	}
	
	public Connection getConnection()
	{
		return conn;
	}
	
	/**
	 * close the connection. <b>MUST CALL AFTER USE!</b>
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException
	{
		conn.close();
		the = null;
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
	
	public ResultSet executeQuerySql(String sql) throws SQLException
	{
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		st.close();
		return rs;
	}

	/**
	 * query for keyphraseness from the database.
	 * 
	 * @param surface
	 * @return the keyphraseness value, or 0 if surface not found
	 * @throws SQLException
	 */
	public double queryKeyphraseness(String surface) throws SQLException
	{
		double kp = 0;

		PreparedStatement st = conn
				.prepareStatement("SELECT keyphraseness FROM keyphraseness WHERE surface=?;");
		st.setString(1, surface.toLowerCase().replaceAll("[^a-z0-9]", " "));
		ResultSet rs = st.executeQuery();
		if (rs.next())
		{
			kp = rs.getDouble("keyphraseness");
		}
		else
		{
			warning("keyphraseness not found: " + surface);
		}

		rs.close();
		st.close();
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
	public double queryRelatedness(List<String> inlinkList1, List<String> inlinkList2)
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
	public Map<String, Double> querySenses(String surface) throws SQLException
	{
		Map<String, Double> rst = new HashMap<String, Double>();

		PreparedStatement st = conn
				.prepareStatement("SELECT concept, commonness FROM commonness WHERE surface=?;");
		st.setString(1, surface);
		ResultSet rs = st.executeQuery();
		while (rs.next())
		{
			rst.put(rs.getString("concept"), rs.getDouble("commonness"));
		}
		rs.close();
		st.close();

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
	public List<String> queryInlinkConceptsForConcept(String toConcept) throws SQLException
	{
		List<String> rst = new ArrayList<String>();

		PreparedStatement st = conn
				.prepareStatement("SELECT DISTINCT from_title FROM wikilinks WHERE to_title=?;");
		st.setString(1, toConcept);
		ResultSet rs = st.executeQuery();
		while (rs.next())
		{
			rst.add(rs.getString("from_title"));
		}
		rs.close();
		st.close();

		// using Java's sort() instead of SQL ORDER BY, since ORDER BY seems to treat upper / lower
		// cases differently from sort().
		Collections.sort(rst);
		return rst;
	}

}
