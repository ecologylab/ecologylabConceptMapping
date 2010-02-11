package ecologylab.semantics.conceptmapping;

/**
 * 
 * @author quyin
 * 
 */
public class Token
{
	public Term term = new Term();
	public float keyphraseness;
	public String sense;
	
	@Override
	public String toString()
	{
		return term.surface + "|" + term.normForm;
	}
}
