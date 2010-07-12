package ecologylab.semantics.conceptmapping.conceptdetection;

public class DetectionInstance
{
	public String		surface;

	public String		concept;

	public double		keyphraseness;

	public double		contextualRelatedness;

	public double		averageRelatedness;

	public double		dismabiguationConfidence;

	public double		occurrence;							// number of occurrence

	public double		frequency;

	// public double generality;

	public boolean	isLinked;								// the target
	
	public DetectionInstance(String surface, String concept)
	{
		this.surface = surface;
		this.concept = concept;
	}
}
