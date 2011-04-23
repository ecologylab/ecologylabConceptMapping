package ecologylab.semantics.concept.preparation.dbpedia;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionPool;
import ecologylab.semantics.concept.database.orm.DbpRecord;
import ecologylab.semantics.concept.database.orm.WikiRedirect;

/**
 * Import Dbpedia redirect records. It will use Dbpedia title records to store redirects with
 * Wikipedia article titles, i.e. each redirect record stored into the database describes a redirect
 * from Wikipedia Article 1 to Wikipedia Article 2.
 * 
 * @author quyin
 *
 */
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
		Session session = SessionPool.getSession();
		session.beginTransaction();
		
		DbpRecord drFrom = (DbpRecord) session.get(DbpRecord.class, from);
		DbpRecord drTo = (DbpRecord) session.get(DbpRecord.class, to);
		WikiRedirect wr = (WikiRedirect) session.get(WikiRedirect.class, drFrom.getWikiTitle());
		if (drFrom != null && drTo != null && wr == null)
		{
			wr = new WikiRedirect();
			wr.setFromTitle(drFrom.getWikiTitle());
			wr.setToTitle(drTo.getWikiTitle());
			session.save(wr);
		}
		
		session.getTransaction().commit();
	}
	
	public static void main(String[] args) throws IOException, SQLException
	{
		RedirectImporter ri = new RedirectImporter();
		ri.parse("D:/wikidata/dbpedia/redirects_en.nt");
	}

}
