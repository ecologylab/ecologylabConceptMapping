package ecologylab.semantics.concept.detect;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import libsvm.svm_node;

import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.learning.svm.SVMPredicter;
import ecologylab.semantics.concept.text.NGramGenerator;
import ecologylab.semantics.concept.text.WikiAnchor;

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

	public static final String						parameterFilePath			= null;

	public static final String						modelFilePath					= null;

	public static final Double						threshold							= 0.5;

	protected DatabaseUtils								dbUtils								= new DatabaseUtils();

	protected NGramGenerator							ngGen;

	protected Set<String>									surfaces;

	protected Set<String>									unambiSurfaces;

	protected Map<String, WikiAnchor>			context;

	protected Map<String, Disambiguator>	disambiguators;

	public Map<String, String>						detectedConcepts;

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
		unambiSurfaces = new HashSet<String>();

		for (String gram : ngGen.ngrams.keySet())
		{
			if (dbUtils.querySurfaces().contains(gram))
			{
				surfaces.add(gram);

				try
				{
					List<String> senses = dbUtils.querySenses(gram);
					if (senses.size() == 1) // unambiguous surface
					{
						unambiSurfaces.add(gram);
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
		context = new HashMap<String, WikiAnchor>();

		for (String surface : unambiSurfaces)
		{
			try
			{
				String sense = dbUtils.querySenses(surface).get(0);
				if (!context.containsKey(sense))
				{
					WikiAnchor anchor = new WikiAnchor(surface, sense);
					context.put(sense, anchor);
				}
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
		for (String surface : surfaces)
		{
			try
			{
				Disambiguator disambi = new Disambiguator(context, surface);
				disambiguators.put(surface, disambi);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// maybe sort by confidence and iteratively improve the disambiguation results
	}

	/**
	 * Determine output concepts.
	 */
	protected void detect()
	{
		detectedConcepts = new HashMap<String, String>();
		DetectionFeatureExtractor dfe = new DetectionFeatureExtractor();

		for (String surface : surfaces)
		{
			try
			{
				Disambiguator disambiguator = disambiguators.get(surface);
				DetectionInstance inst = dfe.extract(ngGen.totalWordCount, ngGen.ngrams.get(surface),
						context, disambiguator);
				svm_node[] instance = constructSVMInstance(inst);
				SVMPredicter pred;
				pred = new SVMPredicter(parameterFilePath, modelFilePath);
				Map<Integer, Double> results = new HashMap<Integer, Double>();
				int p = pred.predict(instance, results);
				double confid = results.get(DetectionInstance.posClassIntLabel);
				inst.isLinked = (p == DetectionInstance.posClassIntLabel);
				inst.positiveConfidence = confid;
				if (confid > threshold)
					detectedConcepts.put(surface, disambiguator.disambiguatedConcept);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected svm_node[] constructSVMInstance(DetectionInstance inst)
	{
		svm_node[] instance = new svm_node[6];
		for (int i = 0; i < instance.length; ++i)
		{
			instance[i].index = i + 1;
		}

		instance[0].value = inst.keyphraseness;
		instance[1].value = inst.contextualRelatedness;
		instance[2].value = inst.averageRelatedness;
		instance[3].value = inst.dismabiguationConfidence;
		instance[4].value = inst.occurrence;
		instance[5].value = inst.frequency;

		return instance;
	}
}
