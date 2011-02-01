package ecologylab.semantics.concept.wikiparser;

import ecologylab.semantics.generated.library.Paragraph;
import ecologylab.semantics.generated.library.WikipediaPageType;

/**
 * This class encapsulate post-processing of a WikipediaPageType object, before information is
 * stored into the database.
 * 
 * @author quyin
 *
 */
public class PostProcessor
{

	public String processWikiText(WikipediaPageType wikiPage)
	{
		StringBuilder sb = new StringBuilder();
		for (Paragraph para : wikiPage.getParagraphs())
		{
			sb.append(para.getParagraphText());
			sb.append("\n");
		}
		String wikiText = sb.toString();
		
		
		
		return sb.toString();
	}
}
