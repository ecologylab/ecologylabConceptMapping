package ecologylab.semantics.concept.preparation.postparsing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import ecologylab.semantics.concept.database.SessionManager;

/**
 * calculate commonness using wiki_links and wiki_surfaces.
 * 
 * @author quyin
 * 
 */
public class CommonnessCalculator
{

	public static final int			LINKED_OCCURRENCE_THRESHOLD	= 5;

	public static final double	COMMONNESS_THRESHOLD				= 0.001;

	private int									counter											= 1;

	public void calculateCommonness()
	{
		Session session = SessionManager.newSession();

		Queue<WikiLink> queue = new LinkedList<WikiLink>();
		String lastSurface = null;

		Criteria q = session.createCriteria(WikiLink.class);
		q.setCacheMode(CacheMode.IGNORE);
		q.addOrder(Order.asc("surface"));
		ScrollableResults results = q.scroll(ScrollMode.FORWARD_ONLY);
		while (results.next())
		{
			WikiLink link = (WikiLink) results.get(0);
			queue.add(link);
			if (lastSurface != null && !lastSurface.equals(link.getSurface()))
			{
				processQueue(queue, lastSurface);
				counter++;
			}
			lastSurface = link.getSurface();
			session.evict(link);
		}
		results.close();

		processQueue(queue, lastSurface);

		session.close();
	}

	private void processQueue(Queue<WikiLink> queue, String surface)
	{
		if (SurfaceFilter.containsLetter(surface) && !SurfaceFilter.filter(surface))
		{
			System.out.println(counter + ": processing " + surface);

			int totalCount = 0;
			Map<Integer, Integer> counts = new HashMap<Integer, Integer>();

			while (queue.size() > 0 && queue.peek().getSurface().equals(surface))
			{
				WikiLink link = queue.poll();
				int prevCount = 0;
				if (counts.containsKey(link.getToId()))
					prevCount = counts.get(link.getToId());
				counts.put(link.getToId(), prevCount + 1);
				totalCount++;
			}

			if (totalCount > LINKED_OCCURRENCE_THRESHOLD)
			{
				Session session = SessionManager.newSession();
				session.beginTransaction();

				for (int cid : counts.keySet())
				{
					double commonness = counts.get(cid) * 1.0 / totalCount;
					if (commonness > COMMONNESS_THRESHOLD)
					{
						Commonness comm = new Commonness();
						comm.setSurface(surface);
						comm.setConceptId(cid);
						comm.setCommonness(commonness);
						session.save(comm);
					}
				}

				session.getTransaction().commit();
				session.close();
			}
		}
	}

	public static void main(String[] args)
	{
		CommonnessCalculator cc = new CommonnessCalculator();
		cc.calculateCommonness();
	}

}
