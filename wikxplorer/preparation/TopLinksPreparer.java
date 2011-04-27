package wikxplorer.preparation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.preparation.postparsing.WikiLink;
import ecologylab.semantics.concept.service.Configs;

/**
 * Generate a SQL script to prepare top related in and out links for most referred concepts.
 * 
 * @author quyin
 * 
 */
public class TopLinksPreparer
{
	
	public void prepare(int numOfPreparedConcepts, Writer out)
	{
		int totalNumOfConcepts = Configs.getInt("db.total_concept_count");
		
		Session session1 = SessionManager.newSession();

		// find top linked concepts
		Criteria q = session1.createCriteria(WikiLink.class);
		q.setCacheable(false);
		q.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("toId"), "id")
				.add(Projections.count("toId"), "count")
				);
		q.addOrder(Order.desc("count"));
		q.setCacheMode(CacheMode.IGNORE);
		q.setFetchSize(100);
		q.setMaxResults(numOfPreparedConcepts);
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		while (sr.next())
		{
			int id = sr.getInteger(0);
			
			String sql1 = String.format("SELECT calculate_top_inlinks(%d, %d);\n", id, totalNumOfConcepts);
			String sql2 = String.format("SELECT calculate_top_outlinks(%d, %d);\n", id, totalNumOfConcepts);
			try
			{
				out.write(sql1);
				out.write(sql2);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sr.close();

		session1.close();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			System.err.println("args: <sql-script-path>");
			System.exit(-1);
		}
		
		String path = args[0];
		FileWriter fw = new FileWriter(path);
		TopLinksPreparer tlp = new TopLinksPreparer();
		tlp.prepare(10000, fw);
		fw.close();
	}

}
