package ecologylab.semantics.concept.wikiparsing;

import java.util.List;

import ecologylab.semantics.concept.database.orm.WikiLink;

/**
 * Parse rendered Wiki HTML codes into pure text and wiki-links.
 * 
 * @author quyin
 * 
 */
public class WikiHtmlParser
{

	private boolean parsed = false;
	
	/**
	 * Parse given rendered wiki HTML codes.
	 * 
	 * @param wikiHtml
	 */
	public void parse(String wikiHtml)
	{
		// TODO
		parsed = true;
	}

	/**
	 * Get parsed wiki-links. Should be called after calling parse(), or null will be returned.
	 * 
	 * @return
	 */
	public List<WikiLink> getLinks()
	{
		if (!parsed)
			return null;
		
		// TODO
		return null;
	}

	/**
	 * Get parsed wiki text (pure text not HTML). Should be called after calling parse(), or null will
	 * be returned.
	 * 
	 * @return
	 */
	public String getText()
	{
		if (!parsed)
			return null;
		
		// TODO
		return null;
	}

}
