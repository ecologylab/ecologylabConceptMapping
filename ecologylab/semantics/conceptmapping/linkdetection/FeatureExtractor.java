package ecologylab.semantics.conceptmapping.linkdetection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ecologylab.semantics.conceptmapping.database.DatabaseAdapter;
import ecologylab.semantics.conceptmapping.database.DatabaseUtils;

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
public class FeatureExtractor
{
	DatabaseAdapter								da;

	DatabaseUtils									utils;

	NGramGenerator								ngGen;

	protected String							context;

	protected List<Gram>					ngrams;

	protected List<Gram>					surfaces;

	protected List<WikiAnchor>		anchors;

	public List<LinkDetInstance>	instances;

	public FeatureExtractor(String text)
	{
		da = DatabaseAdapter.get(this.getClass().getName());
		utils = DatabaseUtils.get(da);
		ngGen = new NGramGenerator(text);
		context = ngGen.context;
		ngrams = ngGen.ngrams;

		filterNGrams();
		addAnchors();
		disambiguation();
		extractFeatures();
		extractLocationFeatures();
	}

	/**
	 * Filter out n-grams that are not surfaces (which will not link to Wikipedia articles).
	 */
	protected void filterNGrams()
	{
		surfaces = new ArrayList<Gram>();

		for (Gram gram : ngrams)
		{
			String phrase = gram.text;
			if (DatabaseUtils.get(da).querySurfaces().contains(phrase))
			{
				surfaces.add(gram);
			}
		}
	}

	/**
	 * Find unambiguous surfaces and link them to Wikipedia articles. They will be used in link
	 * resolution.
	 */
	protected void addAnchors()
	{
		anchors = new ArrayList<WikiAnchor>();
		
		for (Gram gram : surfaces)
		{
			try
			{
				List<String> senses = utils.querySenses(gram.text.replaceAll("\\s+", " "));
				if (senses.size() == 1) // unambiguous surface
				{
					String sense = senses.get(0);
					WikiAnchor anchor = new WikiAnchor(gram);
					anchor.title = sense;
					anchors.add(anchor);
				}
			}
			catch (SQLException e)
			{
				System.err.println("can't find senses from surface " + gram.text);
				e.printStackTrace();
			}
		}

	}

	protected void disambiguation()
	{
		// TODO Auto-generated method stub

	}

	protected void extractFeatures()
	{
		// TODO Auto-generated method stub

	}

	protected void extractLocationFeatures()
	{
		// TODO Auto-generated method stub

	}

}
