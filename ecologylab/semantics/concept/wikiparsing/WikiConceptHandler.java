package ecologylab.semantics.concept.wikiparsing;

import java.util.List;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiLink;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.TextNormalizer;

/**
 * Handle wiki concepts parsed from wiki dump file. Convert them to final form (wiki markups to
 * readable texts), and store into the database.
 * 
 * @author quyin
 * 
 */
public class WikiConceptHandler
{

	private WikiMarkupRenderer		renderer;

	private WikiHtmlParser				htmlParser;

	private WikiHtmlPreprocessor	htmlPreprocessor;

	private TextNormalizer				textNormalizer;

	public WikiConceptHandler() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		this.renderer = (WikiMarkupRenderer) Configs.getObject("prep.wiki_markup_renderer", WikiMarkupRenderer.class);
		this.htmlPreprocessor = (WikiHtmlPreprocessor) Configs.getObject("prep.wiki_html_preprocessor", WikiHtmlPreprocessor.class);
		this.htmlParser = (WikiHtmlParser) Configs.getObject("prep.wiki_html_parser", WikiHtmlParser.class);
		this.textNormalizer = (TextNormalizer) Configs.getObject("prep.wiki_text_postprocessor", TextNormalizer.class);
	}

	/**
	 * Handle a parsed wiki concept. Convert the format and store into the database.
	 * 
	 * @param id
	 * @param title
	 * @param markups
	 */
	public void handle(int id, String title, String markups)
	{
		if (title == null || title.isEmpty())
			return;
		if (markups == null || markups.isEmpty())
			return;

		String html = renderer.render(markups);
		String html1 = htmlPreprocessor.preprocess(html);
		htmlParser.parse(html1);
		String text = htmlParser.getText();
		String text1 = textNormalizer.normalize(text);
		List<WikiLink> links = htmlParser.getLinks();

		SessionManager.getSession().beginTransaction();

		WikiConcept concept = new WikiConcept();
		concept.setId(id);
		concept.setTitle(title);
		concept.setText(text1);
		SessionManager.getSession().save(concept);

		for (WikiLink link : links)
		{
			String surface = link.getSurface();
			link.setSurface(textNormalizer.normalize(surface));
			SessionManager.getSession().save(link);
		}

		SessionManager.getSession().getTransaction().commit();
		
		SessionManager.getSession().evict(concept);
		for (WikiLink link : links)
		{
			SessionManager.getSession().evict(link);
		}
	}
}
