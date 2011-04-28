package wikxplorer.preparation;

import java.io.IOException;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.preparation.postparsing.WikiLink;
import ecologylab.semantics.concept.service.Configs;

public class LinkRelatednessPreparer
{
	
	public void prepare(int offset)
	{
		Session session = SessionManager.newSession();
		
		int count = 0;

		Criteria q = session.createCriteria(WikiLink.class);
		q.setCacheable(false);
		q.setFetchSize(100);
		q.setFirstResult(offset);
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		while (sr.next())
		{
			WikiLink link = (WikiLink) sr.get(0);
			
			count++;
			System.out.println("processing #" + count + "...");
			
			Session session2 = SessionManager.newSession();
			Transaction tx = session2.beginTransaction();
			SQLQuery q1 = session.createSQLQuery("SELECT calculate_relatedness(?, ?, ?);");
			q1.setCacheable(false);
			q1.setInteger(0, link.getFromId());
			q1.setInteger(1, link.getToId());
			q1.setInteger(2, Configs.getInt("db.total_concept_count"));
			q1.list();
			tx.commit();
			session2.close();
		}
		sr.close();

		session.close();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		int offset = 0;
		
		if (args.length != 1)
		{
			System.err.println("args: <offset> (default to 0)");
		}
		else
		{
			offset = Integer.valueOf(args[0]);
		}
		
		LinkRelatednessPreparer tlp = new LinkRelatednessPreparer();
		tlp.prepare(offset);
	}

}
