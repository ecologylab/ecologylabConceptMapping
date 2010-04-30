package ecologylab.semantics.conceptmapping.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestDatabase
{

	/**
	 * @param args
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args)
	{
		try
		{
			Class.forName("org.postgresql.Driver");

			String url = "jdbc:postgresql://achilles.cse.tamu.edu/wikiparsing";
			String username = "quyin";
			String password = "quyindbpwd";

			Connection db = DriverManager.getConnection(url, username, password);
			PreparedStatement st = db.prepareStatement("INSERT INTO inlinks VALUES (?, ?, ?, ?)");
			
			st.setString(1, "to_this");
			st.setString(2, "from_that");
			st.setInt(3, 0);
			st.setString(4, "surface");
			
			int n = st.executeUpdate();
			System.out.println("" + n + " row(s) inserted.");
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("database driver not found.");
		}
		catch (SQLException e)
		{
			System.err.println("database connection failed.");
		}
	}

}
