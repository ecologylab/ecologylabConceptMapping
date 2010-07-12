package ecologylab.semantics.conceptmapping.text;

public class Gram
{
	public String	text;

	public int		count;

	public Gram(String text)
	{
		this(text, 1);
	}

	public Gram(String text, int count)
	{
		this.text = text;
		this.count = count;
	}

	public Gram(Gram other)
	{
		this.text = other.text;
		this.count = other.count;
	}

	public String toString()
	{
		return String.format("[%s:%d]", text, count);
	}
}
