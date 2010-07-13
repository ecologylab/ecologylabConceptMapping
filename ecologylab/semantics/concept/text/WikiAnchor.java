package ecologylab.semantics.concept.text;

public class WikiAnchor
{
	public String	surface;

	public String	concept;	// the concept

	public WikiAnchor(String surface, String concept)
	{
		this.surface = surface;
		this.concept = concept;
	}

	public String toString()
	{
		return String.format("[%s -> %s]", surface, concept);
	}
}
