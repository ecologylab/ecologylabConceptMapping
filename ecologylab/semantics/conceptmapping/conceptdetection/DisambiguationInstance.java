/**
 * 
 */
package ecologylab.semantics.conceptmapping.conceptdetection;

public class DisambiguationInstance
{
	public final String	surface;

	public final String	concept;

	public double				commonness;

	public double				contextualRelatedness;

	public double				contextQuality;

	public boolean			isThisSense;						// binary classification problem

	public double				positiveConfidence;		// probability

	public DisambiguationInstance(String surface, String concept)
	{
		this.surface = surface;
		this.concept = concept;
	}

	@Override
	public String toString()
	{
		String s = String.format(
				"surface:%s, concept:%s, commonness:%f, relatedness:%f, quality:%f, target:%s", surface,
				concept, commonness, contextualRelatedness, contextQuality, isThisSense);
		return s;
	}
}