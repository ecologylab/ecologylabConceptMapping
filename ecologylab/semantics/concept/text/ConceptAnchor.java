package ecologylab.semantics.concept.text;

import ecologylab.semantics.concept.utils.Pair;

public class ConceptAnchor
{
	private final Pair<String, String>	anchor;

	public ConceptAnchor(String surface, String concept)
	{
		anchor = new Pair<String, String>(surface, concept);
	}

	public String getSurface()
	{
		return anchor.getFirst();
	}

	public String getConcept()
	{
		return anchor.getSecond();
	}

	public int hashCode()
	{
		return anchor.hashCode();
	}

	public boolean equals(Object other)
	{
		if (other instanceof ConceptAnchor)
		{
			return anchor.equals(((ConceptAnchor) other).anchor);
		}
		return false;
	}

	public String toString()
	{
		return String.format("[%s -> %s]", getSurface(), getConcept());
	}
}
