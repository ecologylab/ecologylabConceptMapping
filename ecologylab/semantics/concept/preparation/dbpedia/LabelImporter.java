package ecologylab.semantics.concept.preparation.dbpedia;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.Session;
import org.junit.Test;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.DbpRecord;

/**
 * Import Dbpedia labels into the database. One record corresponds a Dbpedia label with a Wikipedia
 * article title.
 * 
 * @author quyin
 * 
 */
public class LabelImporter extends AbstractImporter
{

	public final static Pattern	labelPattern	= Pattern.compile("<([^>]+)> <[^>]+> \"(.*?)\"@en .");

	@Override
	public void parseLine(String line)
	{
		Matcher m = labelPattern.matcher(line);
		if (m.matches())
		{
			String subjectPart = m.group(1);
			String objectPart = m.group(2);

			String dbpName = subjectPart.substring(DbpediaParserUtils.dbpediaResourcePrefix.length());
			String wikiTitle = StringEscapeUtils.unescapeJava(objectPart);

			if (dbpName != null && wikiTitle != null)
			{
				try
				{
					addTitle(dbpName, wikiTitle);
				}
				catch (SQLException e)
				{
					error(e.getMessage() + " when processing line: " + line);
				}
			}
		}
	}

	private void addTitle(String dbpName, String wikiTitle) throws SQLException
	{
		Session session = SessionManager.newSession();
		session.beginTransaction();

		DbpRecord dr = (DbpRecord) session.get(DbpRecord.class, dbpName);
		if (dr == null)
		{
			dr = new DbpRecord();
			dr.setDbpTitle(dbpName);
			dr.setWikiTitle(wikiTitle);
			session.save(dr);
		}

		session.getTransaction().commit();
		session.close();
	}

	@Test
	public void test()
	{
		String[] tests =
		{
				"<http://dbpedia.org/resource/Category:World_War_II> <http://www.w3.org/2004/02/skos/core#prefLabel> \"World War II\"@en .",
				"<http://dbpedia.org/resource/AtlasShrugged> <http://www.w3.org/2000/01/rdf-schema#label> \"\\\"AtlasShrugged\\\"\"@en .",
				"<http://dbpedia.org/resource/AtlasShrugged> <http://www.w3.org/2000/01/rdf-schema#label> \"\\\"@AtlasShrugged\\\"\"@en .",
				"<http://dbpedia.org/resource/AtlasShrugged> <http://www.w3.org/2000/01/rdf-schema#label> \"\\\"Atlas@enShrugged\\\"\"@en .",
				"<http://dbpedia.org/resource/AtlasShrugged> <http://www.w3.org/2000/01/rdf-schema#label> \"Atlas\"@en .Shrugged\"@en .",
		};

		parse(Arrays.asList(tests));
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		LabelImporter li = new LabelImporter();
		li.parse("D:/wikidata/dbpedia/labels_en.nt");
	}

}
