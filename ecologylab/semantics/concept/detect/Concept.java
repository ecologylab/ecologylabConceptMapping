package ecologylab.semantics.concept.detect;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ecologylab.semantics.concept.database.DatabaseFacade;

/**
 * this class represents a concept. inlink concepts and relatedness information is cached.
 * 
 * @author quyin
 * 
 */
public class Concept implements Comparable<Concept>
{

	private static Map<String, Concept>	pool	= new HashMap<String, Concept>();

	/**
	 * get a surface from the pool, given a word. if it is not yet in the pool, create a new one and
	 * put it in the pool.
	 * <p />
	 * using a pool will benefit performance because in this way we can cache information between
	 * documents.
	 * 
	 * @param title
	 * @return
	 */
	public static Concept get(String title)
	{
		if (!pool.containsKey(title))
		{
			synchronized (pool)
			{
				if (!pool.containsKey(title))
				{
					pool.put(title, new Concept(title));

					if (pool.size() % 100 == 0)
					{
						System.out.println("concept pool size: " + pool.size());
					}
				}
			}
		}
		return pool.get(title);
	}
	
	public static void purgePool()
	{
		synchronized (pool)
		{
			pool.clear();
		}
	}

	public final String						title;

	private Map<Concept, Double>	relatedness			= new HashMap<Concept, Double>();

	private Concept(String title)
	{
		this.title = title;
	}

	private Set<String> inlinkConceptTitles = null;
	private Object lockInlinkConceptTitles = new Object();
	public Set<String> getInlinkConceptTitles()
	{
		if (inlinkConceptTitles == null)
		{
			synchronized (lockInlinkConceptTitles)
			{
				if (inlinkConceptTitles == null)
				{
//					System.out.println(":( no, have to retrieve inlink concept titles from database!");
					inlinkConceptTitles = DatabaseFacade.get().getInlinkConceptTitles(this.title);
				}
			}
		}
		else
		{
//			System.out.println(":) already have inlink concept titles!");
		}
		return inlinkConceptTitles;
	}
	
	/**
	 * query relatedness between this and another concept. the value will be cached in the "smaller"
	 * concept, determined by compareTo().
	 * 
	 * @param other
	 * @return relatedness value if different concepts, or 0 if identical concepts
	 */
	public double getRelatedness(Concept other)
	{
		if (equals(other))
		{
			return 0; // relatedness = 0 for identical concepts (predefined)
		}

		if (compareTo(other) > 0)
		{
			return other.getRelatedness(this);
		}

		if (!relatedness.containsKey(other))
		{
			synchronized (relatedness)
			{
				if (!relatedness.containsKey(other))
				{
					double relatednessValue = 0;
					int W = DatabaseFacade.get().getTotalConceptCount();
					
					Set<String> set1 = getInlinkConceptTitles();
					Set<String> set2 = other.getInlinkConceptTitles();

					int s1 = set1.size();
					int s2 = set2.size();
					if (s1 > 0 && s2 > 0)
					{
						int smin = ((s1 > s2) ? s2 : s1);
						int smax = ((s1 > s2) ? s1 : s2);
						int s = 0;
						for (String str : set1)
						{
							if (set2.contains(str))
								s++;
						}

						if (s > 0)
							relatednessValue = (Math.log(smax) - Math.log(s)) / (Math.log(W) - Math.log(smin));
					}
					else
					{
						System.err.println("zero length inlink count: " + (s1 == 0 ? this : "") + " "
								+ (s2 == 0 ? other : ""));
					}
					relatedness.put(other, relatednessValue);
				}
			}
		}
		return relatedness.get(other);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Concept)
		{
			Concept c = (Concept) obj;
			return title.equals(c.title);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return title.hashCode();
	}

	@Override
	public String toString()
	{
		return title;
	}

	@Override
	public int compareTo(Concept other)
	{
		return title.compareTo(other.title);
	}

}
