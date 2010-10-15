package ecologylab.semantics.concept.detect;


/**
 * Instance augments Surface with extracted features & prediction results.
 * 
 * @author quyin
 *
 */
public class Instance
{

	public final Surface	surface;

	public Instance(Surface surface)
	{
		this.surface = surface;
	}

	// for disambiguation
	
	public double		commonness;

	public double		contextualRelatedness;

	public double		contextQuality;

	public Concept	disambiguatedConcept;

	public double		disambiguationConfidence;

	// for detection

	public double		keyphraseness;

	public double		occurrence;							// number of occurrence

	public double		frequency;

	public boolean	isDetected;

	public double		detectionConfidence;

	@Override
	public String toString()
	{
		String s = String.format("{surface:%s, concept:%s, confidences:%f,%f}", surface,
				disambiguatedConcept, disambiguationConfidence, detectionConfidence);
		return s;
	}

}