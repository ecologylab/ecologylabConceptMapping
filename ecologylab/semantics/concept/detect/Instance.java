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

	public static Instance get(Doc doc, Context context, Surface surface, Concept sense)
	{
		Instance instance = new Instance(surface);

		instance.commonness = surface.getCommonness(sense);
		instance.contextQuality = context.getQuality();
		instance.contextualRelatedness = context.getContextualRelatedness(sense);

		instance.keyphraseness = surface.getKeyphraseness();
		instance.occurrence = doc.getNumberOfOccurrences(surface);
		instance.frequency = ((double) instance.occurrence) / doc.getTotalWords();

		return instance;
	}

}