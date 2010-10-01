package ecologylab.semantics.concept.wikiparsing.dbpedia;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecologylab.semantics.concept.database.DatabaseAdapter;

public class RedirectImporter extends AbstractImporter
{
	public final static Pattern	redirectPattern	= Pattern.compile("<([^>]+)> <[^>]+> <([^>]+)> .");

	@Override
	public void parseLine(String line)
	{
		Matcher m = redirectPattern.matcher(line);
		if (m.matches())
		{
			String subjectPart = m.group(1);
			String objectPart = m.group(2);

			String from = subjectPart.substring(DbpediaParserUtils.dbpediaResourcePrefix.length());
			String to = objectPart.substring(DbpediaParserUtils.dbpediaResourcePrefix.length());

			if (from != null && to != null)
			{
				try
				{
					addRedirect(from, to);
				}
				catch (SQLException e)
				{
					error(e.getMessage() + " when processing line: " + line);
				}
			}
		}
	}

	private void addRedirect(String from, String to) throws SQLException
	{
		PreparedStatement st = DatabaseAdapter.get().getPreparedStatement(
				"INSERT INTO dbp_redirects VALUES (?, ?);");
		st.setString(1, from);
		st.setString(2, to);
		st.execute();
	}

	public static void main(String[] args) throws IOException
	{
		RedirectImporter ri = new RedirectImporter();
		ri.parse("C:/wikidata/redirects_en.nt");
	}

}
