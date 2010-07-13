/**
 * 
 */
package ecologylab.semantics.concept.detect;

public class DisambiguationInstance
{

	public static final int	posClassIntLabel	= 1;

	public static final int	negClassIntLabel	= -1;

	public final String			surface;

	public final String			concept;

	public double						commonness;

	public double						contextualRelatedness;

	public double						contextQuality;

	public boolean					isThisSense;						// target of the binary classification problem

	public double						positiveConfidence;

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