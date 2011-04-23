package ecologylab.semantics.concept.preparation.postparsing;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;

import ecologylab.semantics.concept.database.SessionPool;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.utils.CollectionUtils;

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

	private ExecutorService			pool;

	private int									counter											= 0;

	public CommonnessCalculator(int numThreads)
	{
		pool = Executors.newFixedThreadPool(numThreads);
	}

	public void calculateCommonness()
	{
		Session session = SessionPool.getSession();

		Criteria q = session.createCriteria(WikiSurface.class);
		q.add(Property.forName("linkedOccurrence").gt(LINKED_OCCURRENCE_THRESHOLD));
		q.addOrder(Order.desc("linkedOccurrence"));
		ScrollableResults results = q.scroll();
		while (results.next())
		{
			WikiSurface ws = (WikiSurface) results.get(0);
			final String surface = ws.getSurface();
			if (SurfaceFilter.containsLetter(surface) && !SurfaceFilter.filter(surface))
			{
				pool.submit(new Runnable()
				{
					@Override
					public void run()
					{
						processSurface(surface);
						counter++;
						System.out.println(counter + ": processed " + surface);
					}
				});
			}
		}
		results.close();

		try
		{
			pool.awaitTermination(7, TimeUnit.DAYS);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		session.close();

	}

	private void processSurface(String surface)
	{
		Session session = SessionPool.getSession();

		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();

		session.beginTransaction();
		Criteria q2 = session.createCriteria(WikiLink.class);
		q2.add(Property.forName("surface").eq(surface));

		ScrollableResults sr = q2.scroll();
		while (sr.next())
		{
			WikiLink link = (WikiLink) sr.get(0);
			int prevCount = 0;
			if (counts.containsKey(link.getToId()))
				prevCount = counts.get(link.getToId());
			counts.put(link.getToId(), prevCount + 1);
		}
		sr.close();

		int sum = CollectionUtils.sum(counts.values());
		for (int cid : counts.keySet())
		{
			double commonness = counts.get(cid) * 1.0 / sum;
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
	}

	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.err.println("args: <num-of-threads>");
			System.exit(-1);
		}
		int numThreads = Integer.parseInt(args[0]);
		CommonnessCalculator cc = new CommonnessCalculator(numThreads);
		cc.calculateCommonness();
	}

}
