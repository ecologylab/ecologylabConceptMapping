package ecologylab.semantics.conceptmapping.text;

public class WikiAnchor extends Gram
{
	public String	concept; // the concept

	public WikiAnchor(Gram gram)
	{
		super(gram);
	}

	public WikiAnchor(Gram gram, String concept)
	{
		this(gram);
		this.concept = concept;
	}

	public String toString()
	{
		return String.format("[%s => %s]", text, concept);
	}
}
