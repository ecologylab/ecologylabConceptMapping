package ecologylab.semantics.concept.preparation.postparsing;

import java.io.IOException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.detect.SurfaceDictionary;

public class KeyphrasenessCalculator
{

	private int	target;

	private int	current;

	private void calculateKeyphraseness(int offset, int number)
	{
		target = offset + number;
		current = offset;

		Session session = SessionManager.newSession();

		Criteria q = session.createCriteria(WikiConcept.class);
		q.setFetchSize(100);
		q.setFirstResult(offset);
		if (number > 0)
			q.setMaxResults(number);

		ScrollableResults results = q.scroll(ScrollMode.FORWARD_ONLY);
		while (results.next())
		{
			WikiConcept concept = (WikiConcept) results.get(0);
			System.out.println(current + "/" + target + ": processing " + concept.getId());
			processConcept(concept);
			current++;
		}
		results.close();

		session.close();
	}

	private void processConcept(WikiConcept concept)
	{
		Session session = SessionManager.newSession();
		Transaction tx = session.beginTransaction();

		String text = concept.getText();
		List<String> surfaces = SurfaceDictionary.get().extractSurfaces(text);
		for (String surface : surfaces)
		{
			WikiSurface ws = WikiSurface.get(surface, session);
			if (ws != null)
			{
				ws.setTotalOccurrence(ws.getTotalOccurrence() + 1);
			}
		}

		tx.commit();
		session.close();
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println("args: <offset> <number>");
			System.exit(-1);
		}

		int offset = Integer.parseInt(args[0]);
		int number = Integer.parseInt(args[1]);

		KeyphrasenessCalculator kc = new KeyphrasenessCalculator();
		long t0 = System.currentTimeMillis();
		kc.calculateKeyphraseness(offset, number);
		long t1 = System.currentTimeMillis();
		System.out.println("time cost: " + (t1 - t0) + " ms.");
	}

}
