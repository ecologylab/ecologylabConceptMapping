package ecologylab.semantics.concept.wikiparsing;

public interface WikiConceptHandler
{

	/**
	 * Handle a parsed wiki concept. Convert the format and store into the database.
	 * 
	 * @param id
	 * @param title
	 * @param markups
	 */
	void handle(int id, String title, String markups);

}