package ecologylab.semantics.concept.detect;

import java.util.HashMap;
import java.util.Map;

import ecologylab.semantics.concept.database.DatabaseUtils;

/**
 * this class represents a surface, i.e. anchor text for a concept.
 * <p />
 * one surface might relate to different concepts; however all the senses along with commonness
 * values will be cached. commonness is the probability of a particular sense given a surface.
 * <p />
 * keyphraseness, the prior probability of this surface labeled as a relevant concept, is also
 * cached.
 * 
 * @author quyin
 * 
 */
public class Surface
{

	private static Map<String, Surface>	pool	= new HashMap<String, Surface>();

	/**
	 * get a surface from the pool, given a word. if it is not yet in the pool, create a new one and
	 * put it in the pool.
	 * <p />
	 * using a pool will benefit performance because 1) no redundant surfaces are held in memory; 2)
	 * senses / keyphraseness are also cached, eliminating some database operations.
	 * 
	 * @param word
	 * @return
	 */
	public static Surface get(String word)
	{
		if (!pool.containsKey(word))
		{
			pool.put(word, new Surface(word));
		}
		return pool.get(word);
	}

	public final String					word;

	private Map<String, Double>	senses				= null;

	private double							keyphraseness	= -1;
	
	private Surface(String word)
	{
		this.word = word;
	}

	/**
	 * get senses represented by a map from a concept to a commonness value.
	 * 
	 * @return
	 */
	public Map<String, Double> getSenses()
	{
		if (senses == null)
		{
			senses = DatabaseUtils.get().querySenses(word);
		}
		return senses;
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
	public boolean isAmbiguous(TrieDict dict)
	{
		return dict.isAmbiguous(word);
	}

	/**
	 * keyphraseness of this surface.
	 * 
	 * @return
	 */
	public double getKeyphraseness()
	{
		if (keyphraseness < 0)
		{
			keyphraseness = DatabaseUtils.get().queryKeyphraseness(word);
		}
		return keyphraseness;
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
