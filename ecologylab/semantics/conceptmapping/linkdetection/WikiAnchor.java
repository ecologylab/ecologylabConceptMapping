package ecologylab.semantics.conceptmapping.linkdetection;

public class WikiAnchor extends Gram
{
	public String	title;

	public String	href; // not used

	public WikiAnchor()
	{
		
	}
	
	public WikiAnchor(Gram gram)
	{
		this.context = gram.context;
		this.text = gram.text;
		this.startPos = gram.startPos;
		this.length = gram.length;
	}

	public String toString()
	{
		return String.format("[title=%s]%s", title, super.toString());
	}
}
