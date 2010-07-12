package ecologylab.semantics.conceptmapping.conceptdetection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecologylab.semantics.conceptmapping.database.DatabaseUtils;
import ecologylab.semantics.conceptmapping.text.NGramGenerator;
import ecologylab.semantics.conceptmapping.text.WikiAnchor;

/**
 * Extract features for link detection. Including keyphraseness, contextual / average relatedness,
 * disambiguation confidence, frequency, first / last occurrence and spread in %.
 * <p>
 * The inputs are paragraphs of free text. The outputs are features. It first generates n-grams with
 * NGramGenerator, filters out non-surface n-grams, finds out non-ambiguous surfaces, carries out
 * link resolution and at last calculate features.
 * 
 * @author quyin
 * 
 */
public class Detector
{

	protected DatabaseUtils								dbUtils	= new DatabaseUtils();

	protected NGramGenerator							ngGen;

	protected List<String>								unambiSurfaces;

	protected List<String>								ambiSurfaces;

	protected List<WikiAnchor>						context;

	protected Map<String, Disambiguator>	disambiguators;

	public Detector(String text) throws SQLException
	{
		generateNGrams(text);
		findSurfaces();
		generateContext();
		disambiguate();
		detect();
	}

	/**
	 * Generate n-grams from the given text.
	 * 
	 * @param text
	 */
	protected void generateNGrams(String text)
	{
		ngGen = new NGramGenerator(text);
	}

	/**
	 * Filter out n-grams that are not surfaces (which will not link to Wikipedia articles).
	 */
	protected void findSurfaces()
	{
		unambiSurfaces = new ArrayList<String>();
		ambiSurfaces = new ArrayList<String>();

		for (String gram : ngGen.ngrams.keySet())
		{
			if (dbUtils.querySurfaces().contains(gram))
			{
				List<String> senses;
				try
				{
					senses = dbUtils.querySenses(gram);
					if (senses.size() == 1) // unambiguous surface
					{
						unambiSurfaces.add(gram);
					}
					else
					// ambiguous surface
					{
						ambiSurfaces.add(gram);
					}
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Find unambiguous surfaces and link them to Wikipedia articles. They will be used in link
	 * resolution.
	 */
	protected void generateContext()
	{
		context = new ArrayList<WikiAnchor>();

		for (String surface : unambiSurfaces)
		{
			try
			{
				String sense = dbUtils.querySenses(surface).get(0);
				WikiAnchor anchor = new WikiAnchor(ngGen.ngrams.get(surface), sense);
				context.add(anchor);
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Disambiguate. Output the best possible concept & confidence.
	 */
	protected void disambiguate()
	{
		disambiguators = new HashMap<String, Disambiguator>();
		// TODO
	}

	/**
	 * Determine output concepts.
	 */
	protected void detect()
	{
		// TODO Auto-generated method stub

	}

}
