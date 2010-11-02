package ecologylab.semantics.concept.detect;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ecologylab.semantics.concept.database.DatabaseFacade;

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

	public final String						word;

	private Set<Concept>					senses				= null;

	private Map<Concept, Double>	commonness		= null;

	private double								keyphraseness	= -1;

	private Surface(String word)
	{
		this.word = word;
	}

	/**
	 * get senses represented by a set of concepts.
	 * 
	 * @return
	 */
	public Set<Concept> getSenses()
	{
		if (senses == null)
		{
			try
			{
				senses = new HashSet<Concept>();
				commonness = new HashMap<Concept, Double>();

				Map<String, Double> commonness0 = DatabaseFacade.get().querySenses(word);
				for (String title : commonness0.keySet())
				{
					Concept concept = Concept.get(title);
					senses.add(concept);
					commonness.put(concept, commonness0.get(title));
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return senses;
	}

	/**
	 * get commonness value for a concept w.r.t this surface.
	 * 
	 * @param concept
	 * @return
	 */
	public double getCommonness(Concept concept)
	{
		if (commonness == null)
		{
			getSenses();
		}

		if (commonness.containsKey(concept))
			return commonness.get(concept);
		return 0;
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

	/**
	 * keyphraseness of this surface.
	 * 
	 * @return
	 */
	public double getKeyphraseness()
	{
		if (keyphraseness < 0)
		{
			try
			{
				keyphraseness = DatabaseFacade.get().queryKeyphraseness(word);
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
