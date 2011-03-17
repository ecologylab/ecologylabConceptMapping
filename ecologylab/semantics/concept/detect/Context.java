package ecologylab.semantics.concept.detect;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.database.orm.Relatedness;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.learning.svm.NormalizerFactory;
import ecologylab.semantics.concept.learning.svm.PredicterFactory;
import ecologylab.semantics.concept.learning.svm.LearningUtils;
import ecologylab.semantics.concept.learning.svm.GaussianNormalizer;
import ecologylab.semantics.concept.learning.svm.SVMPredicter;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.CollectionUtils;

class Context extends Debug
{

	private Doc												doc;

	private Set<WikiConcept>					concepts						= new HashSet<WikiConcept>();

	private Map<WikiConcept, Surface>	surfaces						= new HashMap<WikiConcept, Surface>();

	private Map<WikiConcept, Double>	weights							= new HashMap<WikiConcept, Double>();

	private double										quality							= 0;

	private Map<WikiConcept, Double>	averageRelatedness	= new HashMap<WikiConcept, Double>();

	public Context(Doc doc)
	{
		this.doc = doc;
	}

	public int size()
	{
		return concepts.size();
	}

	/**
	 * add a concept to the context. it will cause weights and quality in this context to be
	 * re-calculated.
	 * 
	 * @param surface
	 * @param concept
	 */
	public void add(Surface surface, WikiConcept concept)
	{
		int n = weights.size();
		concepts.add(concept);
		surfaces.put(concept, surface);

		// update average relatedness and weights
		double avgRel = 0;
		for (WikiConcept c : concepts)
		{
			if (c.equals(concept))
				continue;

			double rel = Relatedness.get(concept.getId(), c.getId(), doc.getSession());
			avgRel += rel;

			double newAvgRelForC = (n * averageRelatedness.get(c) + rel) / (n + 1);
			averageRelatedness.put(c, newAvgRelForC);
			weights.put(c, getWeight(c, newAvgRelForC));
		}

		avgRel /= n + 1;
		averageRelatedness.put(concept, avgRel);
		weights.put(concept, getWeight(concept, avgRel));

		quality = CollectionUtils.sum(weights.values());
		// System.out.println(this + ": quality = " + quality);
	}

	private double getWeight(WikiConcept concept, double averageRelatedness)
	{
		double wk = Configs.getDouble("feature_extraction.weight_keyphraseness");
		double wm = Configs.getDouble("feature_extraction.weight_mutual_relatedness");
		return surfaces.get(concept).getKeyphraseness() * wk + averageRelatedness * wm;
	}

	/**
	 * get the weight of a concept in a context. if it is not in the context, 0 will be returned.
	 * 
	 * @param concept
	 * @return
	 */
	public double getWeight(WikiConcept concept)
	{
		if (weights.containsKey(concept))
			return weights.get(concept);
		return 0;
	}

	/**
	 * get the overall quality of this context, considering number & weights of all included concepts.
	 * 
	 * @return
	 */
	public double getQuality()
	{
		return quality;
	}

	/**
	 * calculate and return the contextual relatedness of a given concept in this context.
	 * 
	 * @param concept
	 * @return the contextual relatedness if possible, or 0 if not possible (e.g. empty context).
	 */
	public double getContextualRelatedness(WikiConcept concept)
	{
		double sum = 0;
		double sumWeights = 0;

		for (WikiConcept c : concepts)
		{
			double w = getWeight(c);
			double rel = Relatedness.get(concept.getId(), c.getId(), doc.getSession());
			sum += w * rel;
			sumWeights += w;
		}

		if (sumWeights > Double.MIN_VALUE)
			return sum /= sumWeights;
		else
			return 0;
	}

	@Override
	public String toString()
	{
		return "Context[" + size() + " concepts]";
	}

	/**
	 * disambiguate a surface using this context.
	 * 
	 * @param surface
	 * @return the instance containing both features and disambiguation results.
	 * @throws IOException
	 */
	public Instance disambiguate(Surface surface) throws IOException
	{
		Instance bestInst = null;

		double[] kvalueBuffer = null;
		for (WikiConcept sense : surface.getSenses())
		{
			Instance inst = Instance.getForDisambiguation(this, surface, sense);
			svm_node[] svmInst = LearningUtils.constructSVMInstanceForDisambiguation(inst);

			// System.out.format("before normalization: %.4f, %.4f, %.4f\n", svmInst[0].value,
			// svmInst[1].value, svmInst[2].value);
			GaussianNormalizer norm = NormalizerFactory
					.get(ConceptConstants.DISAMBI_PARAM_FILE_PATH);
			norm.normalize(svmInst);
			// System.out.format("after normalization: %.4f, %.4f, %.4f\n", svmInst[0].value,
			// svmInst[1].value, svmInst[2].value);

			Map<Integer, Double> buf = new HashMap<Integer, Double>();
			SVMPredicter pred = PredicterFactory.get(ConceptConstants.DISAMBI_MODEL_FILE_PATH);
			if (kvalueBuffer == null)
				kvalueBuffer = new double[pred.getNumOfSVs()];
			pred.predict(svmInst, buf, kvalueBuffer);
			inst.disambiguationConfidence = buf.get(ConceptConstants.POS_CLASS_INT_LABEL);

			if (bestInst == null || inst.disambiguationConfidence > bestInst.disambiguationConfidence)
			{
				bestInst = inst;
			}
		}

		return bestInst;
	}

}
