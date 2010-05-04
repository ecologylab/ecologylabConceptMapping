package ecologylab.semantics.conceptmapping.wikipedia.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ecologylab.semantics.conceptmapping.wikipedia.db.DatabaseAdapter;

public class DatabaseUtils
{
	public static List<String> getSurfaces(DatabaseAdapter adapter)
	{
		List<String> rst = new ArrayList<String>();
		try
		{
		ResultSet rs = adapter.executeQuerySql("SELECT surface FROM surfaces;");
		while (rs.next())
		{
			String surface = rs.getString("surface");
			rst.add(surface);
		}
		}
		catch (SQLException e)
		{
			System.err.println("cannot query the database: " + e.getMessage());
		}
		return rst;
	}
}
