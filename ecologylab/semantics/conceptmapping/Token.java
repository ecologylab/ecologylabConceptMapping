package ecologylab.semantics.conceptmapping;

/**
 * 
 * @author quyin
 * 
 */
public class Token
{
	public String	passage;

	public int		offsetBegin;

	public int		offsetEnd;

	public String	surface;

	public String	normForm;

	public String	posTag;

	public float	keyphraseness;
	
	public boolean chosenAsKeyphrase;

	public String	sense;

	@Override
	public String toString()
	{
		return surface + "|" + normForm +"|" + offsetBegin + "|" + offsetEnd + "|" + posTag;
	}
}
