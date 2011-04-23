package ecologylab.semantics.concept.detect;

import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;

/**
 * an instance is an occurrence of a surface in an article, which may be mapped to a concept for
 * disambiguation. it also contains features and classification results (confidence).
 * 
 * @author quyin
 * 
 */
public class Instance
{

	private WikiSurface	surface;

	private WikiConcept	concept;

	public Instance(WikiSurface surface)
	{
		this.surface = surface;
	}

	public WikiSurface getSurface()
	{
		return surface;
	}

	public WikiConcept getConcept()
	{
		return concept;
	}

	// features for disambiguation

	/* commonness omitted */

	public double	contextualRelatedness;

	public double	contextQuality;

	public double	disambiguationConfidence;

	// features for detection

	/* keyphraseness omitted */

	public double	occurrence;							// number of occurrence

	public double	frequency;

	public double	detectionConfidence;

	@Override
	public String toString()
	{
		String s = String.format("instance[surface:%s, concept:%s, confidences:%f,%f]",
				surface,
				concept.getTitle(),
				disambiguationConfidence,
				detectionConfidence);
		return s;
	}

}