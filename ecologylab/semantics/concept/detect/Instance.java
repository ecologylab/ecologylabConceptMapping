package ecologylab.semantics.concept.detect;

import ecologylab.semantics.concept.database.orm.WikiConcept;

/**
 * Instance augments Surface with extracted features & prediction results.
 * 
 * @author quyin
 * 
 */
public class Instance
{

	public Surface			surface;

	public WikiConcept	concept;

	public Instance(Surface surface, WikiConcept concept)
	{
		this.surface = surface;
		this.concept = concept;
	}

	// for disambiguation

	public double	commonness;

	public double	contextualRelatedness;

	public double	contextQuality;

	public double	disambiguationConfidence;

	// for detection

	public double	keyphraseness;

	public double	occurrence;							// number of occurrence

	public double	frequency;

	public double	detectionConfidence;

	@Override
	public String toString()
	{
		String s = String.format("instance[surface:%s, concept:%s, confidences:%f,%f]", surface,
				concept.getTitle(), disambiguationConfidence, detectionConfidence);
		return s;
	}

}