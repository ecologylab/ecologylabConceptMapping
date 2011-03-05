package ecologylab.semantics.concept.wikiparsing;

import org.hibernate.Query;
import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiLink;
import ecologylab.semantics.concept.database.orm.WikiRedirect;
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
public class WikiConceptHandlerParsing implements WikiConceptHandler
{

	private WikiMarkupRenderer		renderer;

	private WikiHtmlParser				htmlParser;

	private WikiHtmlPreprocessor	htmlPreprocessor;

	private TextNormalizer				textNormalizer;

	public WikiConceptHandlerParsing() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		this.renderer = (WikiMarkupRenderer) Configs.getObject("prep.wiki_markup_renderer",
				WikiMarkupRenderer.class);
		this.htmlPreprocessor = (WikiHtmlPreprocessor) Configs.getObject("prep.wiki_html_preprocessor",
				WikiHtmlPreprocessor.class);
		this.htmlParser = (WikiHtmlParser) Configs.getObject("prep.wiki_html_parser",
				WikiHtmlMmdParser.class);
		this.textNormalizer = (TextNormalizer) Configs.getObject("prep.wiki_text_postprocessor",
				TextNormalizer.class);
	}

	@Override
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
		Query query = session.createQuery("SELECT c FROM wiki_concepts c WHERE c.title = ?");
		session.beginTransaction();

		if (WikiRedirect.getRedirected(title, session) != null)
			return;

		StringBuilder sb = new StringBuilder();

		for (Paragraph para : page.getParagraphs())
		{
			sb.append(para.getParagraphText()).append(" ");

			for (Anchor anchor : para.getAnchors())
			{
				String surface = anchor.getAnchorText();
				String target = anchor.getTargetTitle();
				int toId = getIdForTitle(target, session, query);
				if (toId >= 0)
				{
					WikiLink link = new WikiLink();
					link.setFromId(id);
					link.setToId(toId);
					link.setSurface(textNormalizer.normalize(surface));
					session.save(link);
				}
			}
		}

		WikiConcept concept = (WikiConcept) session.get(WikiConcept.class, id);
		concept.setText(textNormalizer.normalize(sb.toString()));
		session.update(concept);

		session.getTransaction().commit();
	}

	private int getIdForTitle(String target, Session openSession, Query query)
	{
		String redirect = WikiRedirect.getRedirected(target, openSession);
		String trueTarget = redirect == null ? target : redirect;

		query.setString(1, trueTarget);
		WikiConcept c = (WikiConcept) query.uniqueResult();
		return c == null ? -1 : c.getId();
	}

}
