package ecologylab.semantics.concept.preparation.postparsing;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.detect.SurfaceDictionary;

public class KeyphrasenessCalculator
{

	private ExecutorService	pool;

	private int							total;

	private int							counter;

	public KeyphrasenessCalculator(int numThreads)
	{
		pool = Executors.newFixedThreadPool(numThreads);
	}

	private void calculateKeyphraseness(int offset, int number)
	{
		total = number;
		counter = 0;

		Session session = SessionManager.newSession();
		Criteria q = session.createCriteria(WikiConcept.class);
		q.setFirstResult(offset);
		q.setMaxResults(number);

		ScrollableResults results = q.scroll();
		while (results.next())
		{
			final WikiConcept concept = (WikiConcept) results.get(0);
			pool.submit(new Runnable()
			{
				@Override
				public void run()
				{
					processConcept(concept);
					counter++;
					System.out.println(counter + "/" + total + ": processed " + concept.getTitle());
				}
			});
		}
		results.close();

		session.close();
	}

	private void processConcept(WikiConcept concept)
	{
		Session session = SessionManager.newSession();

		session.beginTransaction();

		String text = concept.getText();
		List<String> surfaces = SurfaceDictionary.get().extractSurfaces(text);
		for (String surface : surfaces)
		{
			WikiSurface ws = WikiSurface.get(surface, session);
			if (ws != null)
			{
				ws.setTotalOccurrence(ws.getTotalOccurrence() + 1);
				session.update(ws);
			}
		}

		session.getTransaction().commit();
		session.close();
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 3)
		{
			System.err.println("args: <offset> <number> <num-of-threads>");
			System.exit(-1);
		}

		int offset = Integer.parseInt(args[0]);
		int number = Integer.parseInt(args[1]);
		int nT = Integer.parseInt(args[2]);

		KeyphrasenessCalculator kc = new KeyphrasenessCalculator(nT);
		kc.calculateKeyphraseness(offset, number);
	}

}
