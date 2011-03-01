package ecologylab.semantics.concept.wikiparsing;

/**
 * Render MediaWiki markups into HTML.
 * 
 * @author quyin
 * 
 */
public interface WikiMarkupRenderer
{

	/**
	 * Render MediaWiki markups into HTML.
	 * 
	 * @param wikiMarkups
	 * @return
	 */
	String render(String wikiMarkups);

}
