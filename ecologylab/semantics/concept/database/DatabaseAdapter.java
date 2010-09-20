package ecologylab.semantics.concept.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import ecologylab.generic.Debug;

public class DatabaseAdapter extends Debug
{

	private static DatabaseAdapter	da	= null;

	public static DatabaseAdapter get()
	{
		if (da == null)
		{
			da = new DatabaseAdapter();
		}

		return da;
	}

	private Connection	conn;

	private DatabaseAdapter()
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

	private Map<String, PreparedStatement>	prepSts	= new HashMap<String, PreparedStatement>();

	public PreparedStatement getPreparedStatement(String sql)
	{
		if (!prepSts.containsKey(sql))
		{
			try
			{
				prepSts.put(sql, conn.prepareStatement(sql));
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
		Statement st = conn.createStatement();
		boolean rst = st.execute(sql);
		st.close();
		return rst;
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
		// st.close(); // will cause a 'This ResultSet is closed' exception.
		return rs;
	}

	public static void main(String[] args)
	{
		DatabaseAdapter da = DatabaseAdapter.get();
	}
}
