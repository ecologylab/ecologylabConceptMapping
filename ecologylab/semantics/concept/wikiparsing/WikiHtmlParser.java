package ecologylab.semantics.concept.wikiparsing;

import ecologylab.semantics.generated.library.WikipediaPageType;

/**
 * Parse rendered Wiki HTML codes into pure text and wiki-links.
 * 
 * @author quyin
 * 
 */
public interface WikiHtmlParser
{

	/**
	 * Parse rendered wiki HTML codes.
	 * 
	 * @param wikiHtml
	 * @return a WikipediaPageForParsing object that can be used by the application. null if the
	 *         parsing failed.
	 */
	public WikipediaPageType parse(String wikiHtml);

}
