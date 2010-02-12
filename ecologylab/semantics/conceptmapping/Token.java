package ecologylab.semantics.conceptmapping;

/**
 * 
 * @author quyin
 * 
 */
public class Token
{
	// basic information about the token
	public String	context;

	public int		offsetBegin;

	public int		offsetEnd;

	public String	surface;

	public String	normForm;

	public String	posTag;

	@Override
	public String toString()
	{
		return surface + "|" + normForm +"|" + offsetBegin + "|" + offsetEnd + "|" + posTag;
	}
}
