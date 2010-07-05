package ecologylab.semantics.conceptmapping.linkdetection;

public class LinkDetInstance
{
	public String		surface;

	public String		concept;

	public double		keyphraseness;

	public double		contextualRelatedness;

	public double		averageRelatedness;

	public double		dismabiguationConfidence;

	// the following features are in %
	public double		frequency;

	public double		firstOccur;

	public double		lastOccur;

	public double		occur;										// relative to firstOccur and lastOccur

	public double		location;								// relative to the whole article

	public double		spread;

	// public double generality;

	public boolean	isLinked; // the target
}
