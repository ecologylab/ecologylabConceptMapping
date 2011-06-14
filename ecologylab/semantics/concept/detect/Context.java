package ecologylab.semantics.concept.detect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.Constants;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.learning.svm.Normalizer;
import ecologylab.semantics.concept.learning.svm.NormalizerFactory;
import ecologylab.semantics.concept.learning.svm.PredicterFactory;
import ecologylab.semantics.concept.learning.svm.SVMPredictor;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.CollectionUtils;
import ecologylab.semantics.concept.utils.CollectionUtils.ValueSelector;

/**
 * a disambiguation context.
 * 
 * @author quyin
 */
public class Context extends Debug
{
	private static class ContextualInstance
	{
		public Instance	instance;

		public double		averageRelatedness	= 0;

		public double		weight							= 0;

		public ContextualInstance(Instance instance)
		{
			this.instance = instance;
		}
	}

	private static final double																			WEIGHT_KEYPHRASENESS;

	private static final double																			WEIGHT_MUTUAL_RELATEDNESS;

	private static final ValueSelector<ContextualInstance, Double>	WEIGHT_SELECTOR;

	static
	{
		WEIGHT_KEYPHRASENESS = Configs.getDouble("feature_extraction.weight_keyphraseness");
		WEIGHT_MUTUAL_RELATEDNESS = Configs.getDouble("feature_extraction.weight_mutual_relatedness");
		WEIGHT_SELECTOR = new ValueSelector<ContextualInstance, Double>()
		{
			@Override
			public Double getValue(ContextualInstance obj)
			{
				return obj.weight;
			}
		};
	}

	private Doc																											doc;

	private List<ContextualInstance>																contextualInstances	= new ArrayList<ContextualInstance>();

	private double																									quality							= 0;

	public Context(Doc doc)
	{
		this.doc = doc;
	}

	public Doc getDoc()
	{
		return doc;
	}

	public int size()
	{
		return contextualInstances.size();
	}

	@Override
	public String toString()
	{
		return "Context[" + doc.getTitle() + ", " + size() + " concepts]";
	}

	/**
	 * 
	 * add a instance (surface-concept pair) to the context. it will cause weights and quality in this
	 * context to be re-calculated.
	 * 
	 * @param instance
	 * @param session
	 */
	public void add(Instance instance, Session session)
	{
		ContextualInstance newCi = new ContextualInstance(instance);

		int n = contextualInstances.size();

		// update average relatedness and weights
		for (ContextualInstance ci : contextualInstances)
		{
			double rel = newCi.instance.getWikiConcept().getRelatedness(ci.instance.getWikiConcept(), session);
			newCi.averageRelatedness += rel;
			ci.averageRelatedness = (n * ci.averageRelatedness + rel) / (n + 1);
			ci.weight = getWeight(ci);
		}
		newCi.averageRelatedness /= n + 1;
		newCi.weight = getWeight(newCi);

		quality = CollectionUtils.sum(contextualInstances, WEIGHT_SELECTOR);
		// System.out.println(this + ": quality = " + quality);

		contextualInstances.add(newCi);
	}

	private double getWeight(ContextualInstance ci)
	{
		return ci.instance.getWikiSurface().getKeyphraseness() * WEIGHT_KEYPHRASENESS
				+ ci.averageRelatedness * WEIGHT_MUTUAL_RELATEDNESS;
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
	 * @param session
	 * @return the contextual relatedness if possible, or 0 if not possible (e.g. empty context).
	 */
	public double getContextualRelatedness(WikiConcept concept, Session session)
	{
		double sum = 0;
		double sumWeights = 0;

		for (ContextualInstance ci : contextualInstances)
		{
			double rel = concept.getRelatedness(ci.instance.getWikiConcept(), session);
			sum += ci.weight * rel;
			sumWeights += ci.weight;
		}

		if (sumWeights > Double.MIN_VALUE)
			return sum /= sumWeights;
		else
			return 0;
	}

	/**
	 * disambiguate an instance using this context. resulting concept and confidence will be set.
	 * 
	 * @param instance
	 * @param session
	 */
	public void disambiguate(Instance instance, Session session)
	{
		WikiSurface surface = instance.getWikiSurface();
		Map<WikiConcept, Double> senses = surface.getConcepts();

		WikiConcept bestConcept = null;
		double bestConfid = 0;

		Normalizer normalizer = NormalizerFactory.get(Configs.getFile("disambiguation.normalization"));
		SVMPredictor predicter = PredicterFactory.get(Configs.getFile("disambiguation.model"), normalizer);

		double[] kvalueBuffer = null;
		for (WikiConcept sense : senses.keySet())
		{
			instance.setWikiConcept(sense);
			instance.setContextualRelatedness(getContextualRelatedness(instance.getWikiConcept(), session));
			instance.setContextQuality(getQuality());

			svm_node[] svmInst = instance.toSvmInstanceForDisambiguation();

			Map<Integer, Double> buf = new HashMap<Integer, Double>();
			if (kvalueBuffer == null)
				kvalueBuffer = new double[predicter.getNumOfSVs()];
			predicter.predict(svmInst, buf, kvalueBuffer);
			instance.setDisambiguationConfidence(buf.get(Constants.POS_CLASS_INT_LABEL));

			if (bestConcept == null || instance.getDisambiguationConfidence() > bestConfid)
			{
				bestConcept = instance.getWikiConcept();
				bestConfid = instance.getDisambiguationConfidence();
			}
		}

		instance.setWikiConcept(bestConcept);
		instance.setDisambiguationConfidence(bestConfid);
	}

}
