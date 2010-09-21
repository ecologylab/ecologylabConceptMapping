package ecologylab.semantics.concept.wikiparsing;

public class FrequentConceptsIdentifier
{
	
	private static final String sql = "SELECT to_title, count(*) as count FROM wikilinks GROUP BY to_title HAVING count(*) > 5 ORDER BY count;";

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
