package ecologylab.semantics.concept.detect;

import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;

/**
 * an instance is an occurrence of a surface in an article, which may be mapped to a concept for
 * disambiguation. it also contains features and classification results (confidences).
 * 
 * @author quyin
 * 
 */
public class Instance
{

	private Doc					doc;

	private WikiSurface	surface;

	private WikiConcept	concept;

	// features for disambiguation

	/* commonness omitted */

	private double			contextualRelatedness;

	private double			contextQuality;

	private double			disambiguationConfidence;

	// features for detection

	/* keyphraseness omitted */

	private double			occurrence;							// number of occurrence

	private double			frequency;

	private double			detectionConfidence;

	// internal use

	private String			toString;

	public Instance(Doc doc, WikiSurface surface)
	{
		this.doc = doc;
		this.surface = surface;
	}

	@Override
	public String toString()
	{
		if (toString == null)
		{
			toString = String.format("instance[surface:%s, concept:%s, confidences:%f,%f]",
					surface,
					concept.getTitle(),
					disambiguationConfidence,
					detectionConfidence);
		}
		return toString;
	}

	public Doc getDoc()
	{
		return doc;
	}

	public WikiSurface getSurface()
	{
		return surface;
	}

	public WikiConcept getConcept()
	{
		return concept;
	}

	void setConcept(WikiConcept concept)
	{
		this.concept = concept;
	}

	public double getContextualRelatedness()
	{
		return contextualRelatedness;
	}

	void setContextualRelatedness(double contextualRelatedness)
	{
		this.contextualRelatedness = contextualRelatedness;
	}

	public double getContextQuality()
	{
		return contextQuality;
	}

	void setContextQuality(double contextQuality)
	{
		this.contextQuality = contextQuality;
	}

	public double getDisambiguationConfidence()
	{
		return disambiguationConfidence;
	}

	void setDisambiguationConfidence(double disambiguationConfidence)
	{
		this.disambiguationConfidence = disambiguationConfidence;
	}

	public double getDetectionConfidence()
	{
		return detectionConfidence;
	}

	void setDetectionConfidence(double detectionConfidence)
	{
		this.detectionConfidence = detectionConfidence;
	}

	public double getOccurrence()
	{
		return occurrence;
	}

	void setOccurrence(double occurrence)
	{
		this.occurrence = occurrence;
	}

	public double getFrequency()
	{
		return frequency;
	}

	void setFrequency(double frequency)
	{
		this.frequency = frequency;
	}

}
