package ecologylab.semantics.concept.wikiparsing;

import info.bliki.wiki.model.WikiModel;

/**
 * Render MediaWiki markups into HTML, using bliki engine.
 * 
 * @author quyin
 * 
 */
public class WikiMarkupRenderer
{

	private WikiModel	wikiModel;

	public WikiMarkupRenderer()
	{
		wikiModel = new WikiModel("http://en.wikipedia.org/wiki/${image}", "http://en.wikipedia.org/wiki/${title}");
	}

	/**
	 * Render MediaWiki markups into HTML.
	 * 
	 * @param wikiMarkups
	 * @return
	 */
	public String render(String wikiMarkups)
	{
		String wikiHtml = wikiModel.render(wikiMarkups);
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
		sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head><body>\n");
		sb.append(wikiHtml);
		sb.append("\n</body></html>\n");
		return sb.toString();
	}

}
