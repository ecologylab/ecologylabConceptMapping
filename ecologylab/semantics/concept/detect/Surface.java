package ecologylab.semantics.concept.detect;

import java.util.Map;
import java.util.Set;

import ecologylab.semantics.concept.database.CachedTable;
import ecologylab.semantics.concept.database.CachedTables;

/**
 * proxy class for a surface, i.e. anchor text for a concept. one surface may relate to more than
 * one concepts.
 * <p />
 * senses + commonness and keyphraseness is cached.
 * 
 * @author quyin
 */
public class Surface
{

	public final String						word;
	
	public Surface(String word)
	{
		assert word != null && !word.isEmpty() : "invalid word for surface."; 
		this.word = word;
	}

	public Set<Concept> getSenses()
	{
		CachedTable sensesTable = CachedTables.getCachedTable(CachedTables.SENSES_TABLE_NAME);
		Map<Concept, Double> senses = (Map<Concept, Double>) sensesTable.get(word);
		return senses.keySet();
	}

	public double getCommonness(Concept concept)
	{
		CachedTable sensesTable = CachedTables.getCachedTable(CachedTables.SENSES_TABLE_NAME);
		Map<Concept, Double> senses = (Map<Concept, Double>) sensesTable.get(word);
		return senses.containsKey(concept) ? senses.get(concept) : 0;
	}

	/**
	 * determine if this surface is ambiguous by looking up its senses.
	 * 
	 * @return
	 */
	public boolean isAmbiguous()
	{
		return getSenses().size() > 1;
	}

	/**
	 * determine if this surface is ambiguous by looking up a given dictionary.
	 * 
	 * @param dict
	 * @return
	 */
	public boolean isAmbiguous(SurfaceDictionary dict)
	{
		return dict.isAmbiguous(word);
	}

	public double getKeyphraseness()
	{
		CachedTable keyphrasenessTable = CachedTables.getCachedTable(CachedTables.KEYPHRASENESS_TABLE_NAME);
		return (Double) keyphrasenessTable.get(word);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Surface)
		{
			Surface s = (Surface) obj;
			return word.equals(s.word);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return word.hashCode();
	}

	@Override
	public String toString()
	{
		return word;
	}

}
