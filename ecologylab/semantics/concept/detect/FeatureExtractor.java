package ecologylab.semantics.concept.detect;

import java.util.List;

import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.text.ConceptAnchor;

/**
 * This class extracts features for sense disambiguation, for a context. it is more efficient to
 * extract features for a context than for a (surface, concept) pair.
 * <p />
 * Features include commonness (the prior probability), relatedness to surrounding text, and quality
 * of context. Commonness is 1D: commonness(surface, concept); Relatedness to surrounding text is
 * 1D: weighted sum of relatedness(concept, surronding concepts); Quality is 1D: sum of all the
 * weights.
 * 
 * @author quyin
 */
public class FeatureExtractor
{

	private Context	context;

	public FeatureExtractor(Context context)
	{
		this.context = context;
	}

	public Instance extract(String surface, String concept, double commonness, int totalWordCount,
			int occurrence)
	{
		Instance instance = new Instance(surface, concept);

		instance.commonness = commonness;
		instance.contextQuality = context.getQuality();
		instance.contextualRelatedness = 0;
		
		List<String> inlinks = DatabaseUtils.get().queryFromConceptsForConcept(concept);
		double sumWeights = 0;
		for (ConceptAnchor anchor : context.getAnchors())
		{
			double w = context.getWeight(anchor);
			double rel = DatabaseUtils.get().queryRelatedness(inlinks, context.getConceptInlinks(anchor.getConcept()));
			instance.contextualRelatedness += w * rel;
			sumWeights += w;
		}
		instance.contextualRelatedness /= sumWeights;

		instance.keyphraseness = DatabaseUtils.get().queryKeyphraseness(surface);
		instance.occurrence = occurrence;
		instance.frequency = ((double) occurrence) / totalWordCount;

		return instance;
	}

}
