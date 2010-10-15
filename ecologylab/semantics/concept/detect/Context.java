package ecologylab.semantics.concept.detect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.utils.CollectionUtils;

/**
 * context is an abstraction of a set of concepts and their relations. surfaces could only be
 * disambiguated given a context.
 * 
 * @author quyin
 * 
 */
public class Context extends Debug
{

	private Set<Concept>					concepts						= new HashSet<Concept>();

	private Map<Concept, Double>	weights							= new HashMap<Concept, Double>();

	private double								quality							= 0;

	/**
	 * cached average relatedness can be reused during updating weights.
	 */
	private Map<Concept, Double>	averageRelatedness	= new HashMap<Concept, Double>();

	/**
	 * return all concepts in this context. DO NOT modify the returned set.
	 * 
	 * @return
	 */
	public Set<Concept> getConcepts()
	{
		return concepts;
	}

	/**
	 * the size of the context, i.e. how many concepts are in the context.
	 * 
	 * @return
	 */
	public int size()
	{
		return concepts.size();
	}

	/**
	 * add a concept to the context. it will cause weights and quality in this context to be
	 * re-calculated.
	 * 
	 * @param concept
	 */
	public void addConcept(Concept concept)
	{
		int n = concepts.size();
		concepts.add(concept);

		// update average relatedness and weights
		double avgRel = 0;
		for (Concept c : averageRelatedness.keySet())
		{
			double rel = c.getRelatedness(concept);
			avgRel += rel;

			double newAvgRelForC = (n * averageRelatedness.get(c) + rel) / (n + 1);
			averageRelatedness.put(c, newAvgRelForC);
			weights.put(c, getWeight(c, newAvgRelForC));
		}

		avgRel /= n + 1;
		averageRelatedness.put(concept, avgRel);
		weights.put(concept, getWeight(concept, avgRel));

		quality = CollectionUtils.sum(weights.values());
	}

	private double getWeight(Concept concept, double averageRelatedness)
	{
		return concept.surface.getKeyphraseness() * ConceptConstants.WEIGHT_KEYPHRASENESS
				+ averageRelatedness * ConceptConstants.WEIGHT_MUTUAL_RELATEDNESS;
	}

	/**
	 * get the weight of a concept in a context. if it is not in the context, 0 will be returned.
	 * 
	 * @param concept
	 * @return
	 */
	public double getWeight(Concept concept)
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
	public double getContextualRelatedness(Concept concept)
	{
		double sum = 0;
		double sumWeights = 0;
		
		for (Concept c : getConcepts())
		{
			double w = getWeight(c);
			double rel = concept.getRelatedness(c);
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

}
