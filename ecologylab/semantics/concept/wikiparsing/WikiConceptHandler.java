package ecologylab.semantics.concept.wikiparsing;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiLink;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.TextNormalizer;
import ecologylab.semantics.generated.library.Anchor;
import ecologylab.semantics.generated.library.Paragraph;
import ecologylab.semantics.generated.library.WikipediaPageType;

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
		this.renderer = (WikiMarkupRenderer) Configs.getObject("prep.wiki_markup_renderer",
				WikiMarkupRenderer.class);
		this.htmlPreprocessor = (WikiHtmlPreprocessor) Configs.getObject("prep.wiki_html_preprocessor",
				WikiHtmlPreprocessor.class);
		this.htmlParser = (WikiHtmlParser) Configs.getObject("prep.wiki_html_parser",
				WikiHtmlParser.class);
		this.textNormalizer = (TextNormalizer) Configs.getObject("prep.wiki_text_postprocessor",
				TextNormalizer.class);
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
		WikipediaPageType page = htmlParser.parse(html1);

		Session session = SessionManager.getSession();
		session.beginTransaction();

		StringBuilder sb = new StringBuilder();
		List<WikiLink> links = new ArrayList<WikiLink>();

		for (Paragraph para : page.getParagraphs())
		{
			sb.append(para.getParagraphText()).append(" ");

			for (Anchor anchor : para.getAnchors())
			{
				String surface = anchor.getAnchorText();
				String target = anchor.getTargetTitle();
				int toId = getIdForTitle(target, session);

				WikiLink link = new WikiLink();
				link.setFromId(id);
				link.setToId(toId);
				link.setSurface(textNormalizer.normalize(surface));
				session.save(link);
				links.add(link);
			}
		}

		WikiConcept concept = new WikiConcept();
		concept.setId(id);
		concept.setTitle(title);
		concept.setText(textNormalizer.normalize(sb.toString()));
		session.save(concept);

		session.getTransaction().commit();

		session.evict(concept);
		for (WikiLink link : links)
		{
			session.evict(link);
		}
	}

	private int getIdForTitle(String target, Session session)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
