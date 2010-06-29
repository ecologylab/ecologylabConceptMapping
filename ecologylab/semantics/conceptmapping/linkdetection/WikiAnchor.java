package ecologylab.semantics.conceptmapping.linkdetection;

public class WikiAnchor extends Gram
{
	String	title;

	String	href;

	public String toString()
	{
		return String.format("[title=%s]%s", title, super.toString());
	}
}
