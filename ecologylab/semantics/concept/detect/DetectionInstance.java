package ecologylab.semantics.concept.detect;

public class DetectionInstance
{
	public static final int	posClassIntLabel	= 1;

	public static final int	negClassIntLabel	= -1;

	public String						surface;

	public String						concept;

	public double						keyphraseness;

	public double						contextualRelatedness;

	public double						averageRelatedness;

	public double						dismabiguationConfidence;

	public double						occurrence;							// number of occurrence

	public double						frequency;

	// public double generality;

	public boolean					isLinked;								// target for the binary classification problem

	public double						positiveConfidence;

	public DetectionInstance(String surface, String concept)
	{
		this.surface = surface;
		this.concept = concept;
	}
}
