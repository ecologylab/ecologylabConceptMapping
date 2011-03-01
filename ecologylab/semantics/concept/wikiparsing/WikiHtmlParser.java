package ecologylab.semantics.concept.wikiparsing;

import java.util.List;

import ecologylab.semantics.concept.database.orm.WikiLink;

/**
 * Parse rendered Wiki HTML codes into pure text and wiki-links.
 * 
 * @author quyin
 * 
 */
public interface WikiHtmlParser
{

	/**
	 * Parse given rendered wiki HTML codes.
	 * 
	 * @param wikiHtml
	 */
	void parse(String wikiHtml);

	/**
	 * Get parsed wiki-links. Should be called after calling parse(), or null will be returned.
	 * 
	 * @return
	 */
	List<WikiLink> getLinks();

	/**
	 * Get parsed wiki text (pure text not HTML). Should be called after calling parse(), or null will
	 * be returned.
	 * 
	 * @return
	 */
	String getText();

}
