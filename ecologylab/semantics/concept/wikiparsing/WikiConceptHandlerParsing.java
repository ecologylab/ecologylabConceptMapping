package ecologylab.semantics.concept.wikiparsing;

import java.util.ArrayList;

import org.hibernate.Query;
import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiLink;
import ecologylab.semantics.concept.database.orm.WikiRedirect;
import ecologylab.semantics.concept.utils.TextNormalizer;
import ecologylab.semantics.generated.library.Anchor;
import ecologylab.semantics.generated.library.Paragraph;
import ecologylab.semantics.generated.library.WikipediaPageType;

/**
 * Handle wiki concepts parsed from wiki dump file. Convert them to final form (wiki markups to
 * readable texts), and store into the database.
 * 
 * Thread safe.
 * 
 * @author quyin
 * 
 */
public class WikiConceptHandlerParsing implements WikiConceptHandler
{

	private WikiMarkupRenderer		renderer;

	private WikiHtmlParser				htmlParser;

	private WikiHtmlPreprocessor	htmlPreprocessor;

	public WikiConceptHandlerParsing() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		this.renderer = new WikiMarkupRenderer();
		this.htmlPreprocessor = new WikiHtmlPreprocessor();
		this.htmlParser = new WikiHtmlMmdParser();
	}

	@Override
	public void handle(int id, String title, String markups)
	{
		if (title == null || title.isEmpty())
			return;
		if (markups == null || markups.isEmpty())
			return;

		Session session = SessionManager.newSession();
		Query query = session.createQuery("SELECT c FROM WikiConcept c WHERE c.title = ?");

		if (WikiRedirect.getRedirected(title, session) == null)
		{
			// render html
			String html = renderer.render(markups);
			String html1 = htmlPreprocessor.preprocess(html);

			// process texts and links
			StringBuilder sb = new StringBuilder();
			WikipediaPageType page = htmlParser.parse(html1);
			if (page != null)
			{
				session.beginTransaction();

				ArrayList<Paragraph> paragraphs = page.getParagraphs();
				if (paragraphs != null)
				{
					for (Paragraph para : paragraphs)
					{
						if (para != null)
						{
							String paraText = para.getParagraphText();
							if (paraText != null)
								sb.append(para.getParagraphText()).append(" ");

							ArrayList<Anchor> anchors = para.getAnchors();
							if (anchors != null)
							{
								for (Anchor anchor : anchors)
								{
									if (anchor != null)
									{
										String surface = anchor.getAnchorText();
										String target = anchor.getTargetTitle();
										int toId = getIdForTitle(target, session, query);
										if (toId >= 0)
										{
											WikiLink link = new WikiLink();
											link.setFromId(id);
											link.setToId(toId);
											String normSurface = TextNormalizer.normalize(surface);
											if (normSurface != null && !normSurface.isEmpty())
											{
												link.setSurface(normSurface);
												session.save(link);
											}
										}
									}
								}
							}
						}
					}
				}

				WikiConcept concept = (WikiConcept) session.get(WikiConcept.class, id);
				if (concept != null)
				{
					String normText = TextNormalizer.normalize(sb.toString());
					if (normText != null && !normText.isEmpty())
					{
						concept.setText(normText);
						session.update(concept);
					}
				}

				session.getTransaction().commit();
			}
		}

		session.close();
	}

	private int getIdForTitle(String target, Session openSession, Query query)
	{
		if (target == null || target.isEmpty())
			return -1;

		char c0 = target.charAt(0);
		if (Character.isLowerCase(c0))
			target = Character.toUpperCase(c0) + target.substring(1);
		String redirect = WikiRedirect.getRedirected(target, openSession);
		String trueTarget = redirect == null ? target : redirect;

		query.setString(0, trueTarget);
		WikiConcept c = (WikiConcept) query.uniqueResult();
		return c == null ? -1 : c.getId();
	}

	@Override
	public void finish()
	{
		// TODO Auto-generated method stub

	}

}
