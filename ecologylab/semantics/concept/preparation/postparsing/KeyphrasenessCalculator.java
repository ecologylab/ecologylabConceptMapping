package ecologylab.semantics.concept.preparation.postparsing;

import java.io.IOException;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.detect.SurfaceDictionary;

public class KeyphrasenessCalculator
{

	private int	total;

	private int	counter;

	private void calculateKeyphraseness(int offset, int number)
	{
		total = number;
		counter = 0;

		Session session = SessionManager.newSession();

		Criteria q = session.createCriteria(WikiConcept.class);
		q.setCacheMode(CacheMode.IGNORE);
		q.setFirstResult(offset);
		q.setMaxResults(number);

		ScrollableResults results = q.scroll(ScrollMode.FORWARD_ONLY);
		while (results.next())
		{
			final WikiConcept concept = (WikiConcept) results.get(0);
			System.out.println(counter + "/" + total + ": processing " + concept.getTitle());
			processConcept(concept);
			counter++;
			session.evict(concept);
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
		if (args.length != 2)
		{
			System.err.println("args: <offset> <number>");
			System.exit(-1);
		}

		int offset = Integer.parseInt(args[0]);
		int number = Integer.parseInt(args[1]);

		KeyphrasenessCalculator kc = new KeyphrasenessCalculator();
		kc.calculateKeyphraseness(offset, number);
	}

}
