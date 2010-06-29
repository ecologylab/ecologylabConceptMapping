package ecologylab.semantics.conceptmapping.linkdetection;

public class Gram
{
	
	String context;
	String text;
	int startPos;
	int length;
	
	public String toString()
	{
		return String.format("[from=%d, len=%d]%s", startPos, length, text);
	}
}
