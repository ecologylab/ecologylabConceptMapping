package ecologylab.semantics.concept.wikiparsing;

import java.util.List;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiLink;
import ecologylab.semantics.concept.service.Configs;

public class WikiConceptHandler
{

	private WikiMarkupRenderer		renderer;

	private WikiHtmlParser				htmlParser;

	private WikiHtmlPreprocessor	htmlPreprocessor;

	public WikiConceptHandler() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		this.renderer = (WikiMarkupRenderer) Configs.getObject("prep.wiki_markup_renderer");
		this.htmlPreprocessor = (WikiHtmlPreprocessor) Configs.getObject("prep.wiki_html_preprocessor");
		this.htmlParser = (WikiHtmlParser) Configs.getObject("prep.wiki_html_parser");
	}

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
		List<WikiLink> links = htmlParser.getLinks();

		SessionManager.getSession().beginTransaction();

		WikiConcept concept = new WikiConcept();
		concept.setId(id);
		concept.setTitle(title);
		concept.setText(text);
		SessionManager.getSession().save(concept);

		for (WikiLink link : links)
		{
			SessionManager.getSession().save(link);
		}

		SessionManager.getSession().getTransaction().commit();
	}

}
