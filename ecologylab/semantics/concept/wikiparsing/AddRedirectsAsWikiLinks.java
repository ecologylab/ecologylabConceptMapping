package ecologylab.semantics.concept.wikiparsing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.utils.TextUtils;

/**
 * treat redirects as wikilinks, using normalized form of the redirected concept title as surface.
 * save them into wikilinks.
 * 
 * @author quyin
 * 
 */
public class AddRedirectsAsWikiLinks
{

	public static void main(String[] args)
	{
		try
		{
			ResultSet rs = DatabaseAdapter.get().executeQuerySql(
					"SELECT from_title, to_title FROM redirects;");
			PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
					"INSERT INTO wikilinks VALUES (?,?,?);");
			while (rs.next())
			{
				String from = rs.getString("from_title");
				String to = rs.getString("to_title");
				String surface = TextUtils.normalize(from);

				ps.setString(1, from);
				ps.setString(2, to);
				ps.setString(3, surface);
				ps.execute();
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
