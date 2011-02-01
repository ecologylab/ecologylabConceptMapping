package ecologylab.semantics.concept.wikiparser;

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
