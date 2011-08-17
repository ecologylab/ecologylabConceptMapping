package ecologylab.semantics.concept.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestJDBC
{
	public static final String	url		= "jdbc:postgresql://ecolab-chevron-1.cse.tamu.edu/wikiparsing3";

	public static final String	user	= "quyin";

	public static final String	pass	= "quyindbpwd";

	public static Connection connect() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
		return DriverManager.getConnection(url, user, pass);
	}

	public static void test() throws ClassNotFoundException, SQLException
	{
		Connection conn = connect();
		conn.setAutoCommit(false);

		PreparedStatement ps = conn.prepareStatement("SELECT * FROM wiki_links;");
		ps.setFetchSize(10);
		ResultSet rs = ps.executeQuery();
		while (rs.next())
		{
			String str = String.format("%s, %s, %s\n", rs.getInt(1), rs.getInt(2), rs.getString(3));
			System.out.println(str);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		test();
	}

}
