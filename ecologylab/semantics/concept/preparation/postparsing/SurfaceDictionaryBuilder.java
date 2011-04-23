package ecologylab.semantics.concept.preparation.postparsing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import ecologylab.semantics.concept.database.SessionPool;
import ecologylab.semantics.concept.service.Configs;

/**
 * initialize or build the surface dictionary. filter out stop words and more.
 * 
 * @author quyin
 * 
 */
public class SurfaceDictionaryBuilder
{

	public void buildDictionary() throws IOException
	{
		File dictFile = Configs.getFile("surface_dictionary_path");
		FileWriter out = new FileWriter(dictFile);

		Session session = SessionPool.get().getSession();

		Criteria q = session.createCriteria(Commonness.class);
		q.setProjection(Projections.projectionList()
				.add(Projections.rowCount())
				.add(Projections.groupProperty("surface")));
		ScrollableResults results = q.scroll();
		while (results.next())
		{
			String surface = results.getString(0);
			int count = results.getInteger(1);
			String line = String.format("%s|%d\n", surface, count);
			try
			{
				out.write(line);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		results.close();

		SessionPool.get().releaseSession(session);

		out.close();
	}

	public static void main(String[] args) throws IOException
	{
		SurfaceDictionaryBuilder builder = new SurfaceDictionaryBuilder();
		builder.buildDictionary();
		SessionPool.get().closeAllSessions();
	}

}
