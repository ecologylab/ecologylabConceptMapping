package wikxplorer.preparation;

import java.io.IOException;

import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.preparation.postparsing.WikiLink;

public class LinkRelatednessPreparer
{

	public static int	step	= 1000;

	public void prepare(int offset)
	{
		Session session = SessionManager.newSession();
		
		int count = 0;

		Criteria q = session.createCriteria(WikiLink.class);
		q.setFetchSize(100);
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		long t0 = System.currentTimeMillis();
		while (sr.next())
		{
			count++;
			System.out.println("processing #" + count + "...");
			if (count < offset)
				continue;
			
			WikiLink link = (WikiLink) sr.get(0);
			int id1 = link.getFromId();
			int id2 = link.getToId();
			
			Session session2 = SessionManager.newSession();
			Transaction tx = session2.beginTransaction();
			
			WikiConcept c1 = WikiConcept.getById(id1, session2);
			WikiConcept c2 = WikiConcept.getById(id2, session2);
			c1.getRelatedness(c2);
			
			tx.commit();
			session2.close();
			
			if (count % step == 0)
			{
				long t1 = System.currentTimeMillis();
				System.out.println(step + " relatedness costs " + (t1 -t0) + "ms.");
				t0 = t1;
			}
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
