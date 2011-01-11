package ecologylab.semantics.concept.detect;

import java.util.Set;

import ecologylab.semantics.concept.database.CachedTable;
import ecologylab.semantics.concept.database.CachedTables;

/**
 * proxy class for concept
 * 
 * @author quyin
 * 
 */
public class Concept implements Comparable<Concept>
{

	public final String	title;

	public Concept(String title)
	{
		assert title != null && !title.isEmpty() : "invalid concept title.";
		this.title = title;
	}

	public Set<String> getInlinkConceptTitles()
	{
		CachedTable inlinksTable = CachedTables.getCachedTable(CachedTables.INLINKS_TABLE_NAME);
		return (Set<String>) inlinksTable.get(title);
	}

	/**
	 * query relatedness between this and another concept.
	 * <p />
	 * the value will be cached in the "smaller" concept, determined by compareTo().
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

		CachedTable relatednessTable = CachedTables.getCachedTable(CachedTables.RELATEDNESS_TABLE_NAME);
		return (Double) relatednessTable.get(title + "\t" + other.title);
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
