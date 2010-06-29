/**
 * 
 */
package ecologylab.semantics.conceptmapping.linkresolution;

public class DisambiguationInstance
{
	public final String surface;
	public final String concept;
	
	public DisambiguationInstance(String surface, String concept)
	{
		this.surface = surface;
		this.concept = concept;
	}
	
	public double commonness;
	public double contextualRelatedness;
	public double contextQuality;
	
	public String target; // for classification
	
	@Override
	public String toString()
	{
		String s = String.format("surface:%s, concept:%s, commonness:%f, relatedness:%f, quality:%f, target:%s",
				surface, concept, commonness, contextualRelatedness, contextQuality, target);
		return s;
	}
}