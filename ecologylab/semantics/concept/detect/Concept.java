package ecologylab.semantics.concept.detect;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public synchronized static Concept get(String title)
	{
		if (!pool.containsKey(title))
		{
			pool.put(title, new Concept(title));
		}
		return pool.get(title);
	}

	public synchronized static void recycleAll()
	{
		pool.clear();
	}

	public final String						title;

	private List<String>					inlinkConcepts	= null;

	private Map<Concept, Double>	relatedness			= new HashMap<Concept, Double>();

	private Concept(String title)
	{
		this.title = title;
	}

	/**
	 * get inlink concepts as a list. result will be cached.
	 * 
	 * @return
	 */
	public synchronized List<String> getInlinkConcepts()
	{
		if (inlinkConcepts == null)
		{
			inlinkConcepts = DatabaseFacade.get().queryInlinkConceptsForConcept(title);
		}
		return inlinkConcepts;
	}

	/**
	 * query relatedness between this and another concept. the value will be cached in the "smaller"
	 * concept, determined by compareTo().
	 * 
	 * @param other
	 * @return relatedness value if different concepts, or 0 if identical concepts
	 */
	public synchronized double getRelatedness(Concept other)
	{
		if (equals(other))
		{
			return 0; // relatedness = 0 for identical concepts (predefined)
		}

		if (compareTo(other) > 0)
		{
			return other.getRelatedness(this);
		}

		if (!relatedness.containsKey(other.title))
		{
			double relatednessValue = DatabaseFacade.get().queryRelatedness(getInlinkConcepts(),
					other.getInlinkConcepts());
			relatedness.put(other, relatednessValue);
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

	public synchronized void recycle()
	{
		if (pool != null)
		{
			synchronized (pool)
			{
				if (pool.containsKey(title))
					pool.remove(title);
				if (inlinkConcepts != null)
					inlinkConcepts.clear();
				if (relatedness != null)
					relatedness.clear();
			}
		}
	}

}
