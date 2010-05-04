package ecologylab.semantics.conceptmapping.wikipedia.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DatabaseAdapter
{

	private static Map<String, DatabaseAdapter>	dbs	= new HashMap<String, DatabaseAdapter>();

	public static DatabaseAdapter get(String id)
	{
		if (!dbs.containsKey(id))
		{
			try
			{
				dbs.put(id, new DatabaseAdapter());
			}
			catch (ClassNotFoundException e)
			{
				System.err.println("driver loading failed.");
				return null;
			}
			catch (SQLException e)
			{
				System.err.println("connection failed.");
				return null;
			}
		}

		return dbs.get(id);
	}

	private Connection	db;

	private DatabaseAdapter() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://achilles.cse.tamu.edu/wikiparsing";
		String username = "quyin";
		String password = "quyindbpwd";

		db = DriverManager.getConnection(url, username, password);
	}

	private Map<String, PreparedStatement>	prepSts	= new HashMap<String, PreparedStatement>();

	public PreparedStatement getPreparedStatement(String sql)
	{
		if (!prepSts.containsKey(sql))
		{
			try
			{
				prepSts.put(sql, db.prepareStatement(sql));
			}
			catch (SQLException e)
			{
				System.err.println("cannot prepare statement: " + sql);
				return null;
			}
		}
		return prepSts.get(sql);
	}

	public boolean executeSql(String sql) throws SQLException
	{
		Statement st = db.createStatement();
		boolean rst = st.execute(sql);
		st.close();
		return rst;
	}

	public int executeUpdateSql(String sql) throws SQLException
	{
		Statement st = db.createStatement();
		int rst = st.executeUpdate(sql);
		st.close();
		return rst;
	}

	public ResultSet executeQuerySql(String sql) throws SQLException
	{
		Statement st = db.createStatement();
		ResultSet rs = st.executeQuery(sql);
		st.close();
		return rs;
	}

}
