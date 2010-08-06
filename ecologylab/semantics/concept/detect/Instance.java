/**
 * 
 */
package ecologylab.semantics.concept.detect;

import ecologylab.semantics.concept.text.ConceptAnchor;

public class Instance
{

	public final ConceptAnchor	anchor;

	public double								commonness;

	public double								contextualRelatedness;

	public double								contextQuality;

	public boolean							isThisSense;

	public double								disambiguationConfidence;

	public double								keyphraseness;

	public double								occurrence;							// number of occurrence

	public double								frequency;

	// public double generality;

	public boolean							isDetected;

	public double								conceptConfidence;

	public Instance(String surface, String concept)
	{
		anchor = new ConceptAnchor(surface, concept);
	}

	@Override
	public String toString()
	{
		String s = String.format("{surface:%s, concept:%s, features:%f,%f,%f,%f,%f,%f,%f,%f}",
				anchor.getSurface(), anchor.getConcept(), commonness, contextualRelatedness,
				contextQuality, disambiguationConfidence, keyphraseness, occurrence, frequency);
		return s;
	}

}