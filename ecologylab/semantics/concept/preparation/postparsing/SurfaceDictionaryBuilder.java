package ecologylab.semantics.concept.preparation.postparsing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.detect.SurfaceDictionary;
import ecologylab.semantics.concept.service.Configs;

/**
 * initialize or build the surface dictionary. filter out stop words and more.
 * 
 * @author quyin
 * 
 */
public class SurfaceDictionaryBuilder
{

	private static final int	LINKED_COUNT_THRESHOLD	= 5;

	public void buildDictionary() throws IOException
	{
		File dictFile = Configs.getFile(SurfaceDictionary.SURFACE_DICTIONARY_PATH);
		FileWriter out = new FileWriter(dictFile);

		Session session = SessionManager.newSession();

		SQLQuery q = session
				.createSQLQuery("SELECT surface, count(*) AS count FROM wiki_links GROUP BY surface ORDER BY surface;");
		q.addScalar("surface", StandardBasicTypes.STRING);
		q.addScalar("count", StandardBasicTypes.INTEGER);
		q.setFetchSize(100);
		ScrollableResults results = q.scroll(ScrollMode.FORWARD_ONLY);
		while (results.next())
		{
			String surface = results.getString(0);
			int count = results.getInteger(1);
			System.out.println("processing " + surface + "...");

			if (SurfaceFilter.containsLetter(surface) && !SurfaceFilter.filter(surface)
					&& count >= LINKED_COUNT_THRESHOLD)
			{
				Session session2 = SessionManager.newSession();
				WikiSurface ws = WikiSurface.get(surface, session2);
				if (ws != null)
				{
					int senseCount = ws.getConcepts().size();
					String line = String.format("%s|%d\n", surface, senseCount);
					try
					{
						System.out.print("writing " + line);
						out.write(line);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				session2.close();
			}
		}
		results.close();

		session.close();

		out.close();
	}

	public static void main(String[] args) throws IOException
	{
		SurfaceDictionaryBuilder builder = new SurfaceDictionaryBuilder();
		builder.buildDictionary();
	}

}
