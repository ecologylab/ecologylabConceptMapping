package ecologylab.semantics.concept.detect;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.learning.svm.SVMGaussianNormalization;
import ecologylab.semantics.concept.learning.svm.SVMPredicter;
import ecologylab.semantics.concept.text.NGramGenerator;

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
public class Detector extends Debug
{

	protected NGramGenerator										nGramGenerator;

	protected Context														context;

	protected FeatureExtractor									featureExtractor;

	protected Map<String, Map<String, Double>>	surfacesAndSenses;

	protected Set<Instance>											instances;

	public Map<String, String>									detectedConcepts;

	/**
	 * the entry method for concept detection.
	 * 
	 * @param text
	 * @throws IOException 
	 */
	public void detect(String text) throws IOException
	{
		generateNGrams(text);
		findSurfacesAndGenerateContext();
		context.init();
		featureExtractor = new FeatureExtractor(context);
		disambiguateAndGenerateInstances();
		detectConcepts();
	}

	/**
	 * Generate n-grams from the given text.
	 * 
	 * @param text
	 */
	protected void generateNGrams(String text)
	{
		nGramGenerator = new NGramGenerator(text);
	}

	/**
	 * Filter out n-grams that are not surfaces (which will not link to Wikipedia articles).
	 */
	protected void findSurfacesAndGenerateContext()
	{
		context = new Context();
		surfacesAndSenses = new HashMap<String, Map<String, Double>>();

		for (String gram : nGramGenerator.ngrams.keySet())
		{
			if (DatabaseUtils.get().hasSurface(gram))
			{
				Map<String, Double> senses = DatabaseUtils.get().querySenses(gram);
				if (senses.size() <= 0)
				{
					// weird things happening
					continue;
				}
				else if (senses.size() == 1)
				{
					// unambiguous surface
					String sense = (String) senses.keySet().toArray()[0];
					context.add(gram, sense);
				}
				surfacesAndSenses.put(gram, senses); // cache commonness data for use
			}
		}
		
		debug(surfacesAndSenses.size() + " surface(s) found.");
	}

	/**
	 * Disambiguate. Output the best possible concept & confidence.
	 * @throws IOException 
	 */
	protected void disambiguateAndGenerateInstances() throws IOException
	{
		debug("disambiguating surfaces ...");
		
		instances = new HashSet<Instance>();
		SVMGaussianNormalization norm = new SVMGaussianNormalization(ConceptConstants.DISAMBI_PARAM_FILE_PATH);
		SVMPredicter pred = new SVMPredicter(ConceptConstants.DISAMBI_MODEL_FILE_PATH);
		
		for (String surface : surfacesAndSenses.keySet())
		{
			Map<String, Double> senses = surfacesAndSenses.get(surface);
			Instance bestInst = null;
			for (String concept : senses.keySet())
			{
				Instance inst = featureExtractor.extract(surface, concept, senses.get(concept),
						nGramGenerator.totalWordCount, nGramGenerator.ngrams.get(surface).count);

				if (senses.size() > 1)
				{
					// ambi surface
					svm_node[] svmInst = constructSVMInstance(inst.commonness, inst.contextualRelatedness,
							inst.contextQuality);
					norm.normalize(svmInst);
					Map<Integer, Double> buf = new HashMap<Integer, Double>();
					pred.predict(svmInst, buf);
					inst.disambiguationConfidence = buf.get(ConceptConstants.POS_CLASS_INT_LABEL);

					if (bestInst == null || inst.disambiguationConfidence > bestInst.disambiguationConfidence)
					{
						bestInst = inst;
					}
				}
				else
				{
					// unambi surface
					bestInst = inst;
				}
			}
			
			debug("disambiguated: " + surface + " -> " + bestInst.anchor.getConcept());
			instances.add(bestInst);
		}

		// maybe sort by confidence and iteratively refine the disambiguation results
	}

	/**
	 * Determine output concepts.
	 * @throws IOException 
	 */
	protected void detectConcepts() throws IOException
	{
		debug("detecting concepts ...");
		
		detectedConcepts = new HashMap<String, String>();
		SVMGaussianNormalization norm = new SVMGaussianNormalization(ConceptConstants.DETECT_PARAM_FILE_PATH);
		SVMPredicter pred = new SVMPredicter(ConceptConstants.DETECT_MODEL_FILE_PATH);

		for (Instance inst : instances)
		{
			svm_node[] svmInst = constructSVMInstance(inst.keyphraseness, inst.contextualRelatedness,
					inst.disambiguationConfidence, inst.occurrence, inst.frequency);
			norm.normalize(svmInst);
			Map<Integer, Double> results = new HashMap<Integer, Double>();
			pred.predict(svmInst, results);
			inst.conceptConfidence = results.get(ConceptConstants.POS_CLASS_INT_LABEL);

			if (inst.conceptConfidence > ConceptConstants.DETECT_THRESHOLD)
				detectedConcepts.put(inst.anchor.getSurface(), inst.anchor.getConcept());
		}
	}

	public static svm_node[] constructSVMInstance(double... features)
	{
		svm_node[] instance = new svm_node[features.length];
		for (int i = 0; i < instance.length; ++i)
		{
			instance[i] = new svm_node();
			instance[i].index = i + 1;
			instance[i].value = features[i];
		}
		return instance;
	}

}
