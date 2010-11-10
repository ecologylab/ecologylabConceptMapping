package ecologylab.semantics.concept.detect;

/**
 * Instance augments Surface with extracted features & prediction results.
 * 
 * @author quyin
 * 
 */
public class Instance
{

	public Surface	surface;

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

	public double		detectionConfidence;

	@Override
	public String toString()
	{
		String s = String.format("{surface:%s, concept:%s, confidences:%f,%f}", surface,
				disambiguatedConcept, disambiguationConfidence, detectionConfidence);
		return s;
	}

	/**
	 * extract features for disambiguation (including commonness, contextual relatedness and quality.
	 * 
	 * @param context
	 * @param surface
	 * @param sense
	 * @return
	 */
	public static Instance getForDisambiguation(Context context, Surface surface, Concept sense)
	{
		Instance instance = new Instance(surface);

		instance.commonness = surface.getCommonness(sense);

		instance.contextQuality = context.getQuality();

		instance.contextualRelatedness = context.getContextualRelatedness(sense);

		instance.disambiguatedConcept = sense;

		return instance;
	}

	public void recycle()
	{
		surface = null;
		disambiguatedConcept = null;
	}

}