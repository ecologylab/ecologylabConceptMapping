package ecologylab.semantics.concept.preparation.parsing;

import java.util.ArrayList;

import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.preparation.postparsing.WikiLink;
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

		WikiConcept concept = WikiConcept.getById(id, session);
		if (concept != null)
		{
			// render html
			String html = renderer.render(markups);
			String html1 = htmlPreprocessor.preprocess(html);

			// process texts and links
			WikipediaPageType page = htmlParser.parse(html1);
			StringBuilder sb = new StringBuilder();
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
										String normSurface = TextNormalizer.normalize(surface);
										String target = anchor.getTargetTitle();
										// WikiConcept.getByTitle() will handle redirects
										WikiConcept targetConcept = WikiConcept.getByTitle(target, session);

										if (normSurface != null && !normSurface.isEmpty() && targetConcept != null
												&& concept.getId() != targetConcept.getId())
										{
											WikiLink link = new WikiLink();
											link.setFromId(concept.getId());
											link.setToId(targetConcept.getId());
											link.setSurface(normSurface);
											session.save(link);
										}
									}
								}
							}
						}
					}
				}

				String normText = TextNormalizer.normalize(sb.toString());
				if (normText != null && !normText.isEmpty())
				{
					concept.setText(normText);
				}
				session.update(concept);

				session.getTransaction().commit();
			}
		}

		session.close();
	}

	@Override
	public void finish()
	{

	}

}
