package ecologylab.semantics.concept.wikiparsing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.utils.TextUtils;
import ecologylab.semantics.concept.utils.WikiUtils;

public class WikiParsingSAXHandler extends DefaultHandler
{

	private StringBuilder	currentText;

	private String				wikiTitle;
	
	private PreparedStatement pstSaveWikiLink;
	private PreparedStatement pstSaveWikiText;
	private PreparedStatement pstGetRedirected;
	private PreparedStatement pstGetTrueTitle;
	
	public WikiParsingSAXHandler() throws SQLException
	{
		pstSaveWikiLink = DatabaseFacade.get().getPreparedStatement("INSERT INTO wikilinks VALUES (?, ?, ?);");
		pstSaveWikiText = DatabaseFacade.get().getPreparedStatement("INSERT INTO wikitexts VALUES (?,?);");
		pstGetRedirected = DatabaseFacade.get().getPreparedStatement("SELECT to_title FROM redirects WHERE from_title=?;");
		pstGetTrueTitle = DatabaseFacade.get().getPreparedStatement("SELECT title FROM dbp_titles WHERE title=?;");
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		currentText.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equals("title"))
		{
			wikiTitle = currentText.toString();
		}
		else if (qName.equals("text"))
		{
			handleWikiText(wikiTitle, currentText.toString());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException
	{
		currentText = new StringBuilder();
	}
	
	@Override
	public void endDocument()
	{
		
	}

	protected void handleWikiText(final String title, String wikiText)
	{
		if (title == null || title.isEmpty())
			return;
		if (wikiText == null || wikiText.isEmpty())
			return;

		WikiUtils wu = new WikiUtils()
		{
			@Override
			public void newWikiLink(String literal, String dest, String surface)
			{
				saveWikiLink(title, dest, surface);
			}
		};
		String ft = wu.filter(wikiText);
		String nt = TextUtils.normalize(ft);
		saveWikiText(title, nt);

		tick(title);
	}

	/**
	 * for counter.
	 */
	protected void tick(String title)
	{

	}

	private synchronized void saveWikiLink(String from, String to, String surface)
	{
		synchronized (getClass())
		{
			if (from != null && to != null && surface != null)
			{
				String trueTarget = getRedirectedOrTrueTitle(initcap(to));
				if (trueTarget == null)
				{
					// not a primary or redirected title, abandon this link since we don't know where it links to.
					return;
				}
				String normedSurface = TextUtils.normalize(surface);
	
				try
				{
					pstSaveWikiLink.setString(1, from);
					pstSaveWikiLink.setString(2, trueTarget);
					pstSaveWikiLink.setString(3, normedSurface);
					pstSaveWikiLink.execute();
				}
				catch (SQLException e)
				{
					Debug.warning(WikiParsingSAXHandler.class, String.format(
							"error when saving wikilink: %s -> %s: %s, error message: %s", from, to, surface,
							e.getMessage()));
				}
			}
		}
	}

	private static String initcap(String s)
	{
		return s == null ? null :
				s.length() == 0 ? "" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	private synchronized void saveWikiText(String title, String text)
	{
		synchronized (getClass())
		{
			if (title == null || text == null)
				return;
	
			try
			{
				pstSaveWikiText.setString(1, title);
				pstSaveWikiText.setString(2, text);
				pstSaveWikiText.execute();
			}
			catch (SQLException e)
			{
				Debug.warning(
						WikiParsingSAXHandler.class,
						String.format("error when saving wikitext for %s, error message: %s", title,
								e.getMessage()));
			}
		}
	}

	private synchronized String getRedirectedOrTrueTitle(String target)
	{
		synchronized (getClass())
		{
			String trueTarget = null;
			
			try
			{
				pstGetRedirected.setString(1, target);
				ResultSet rs = pstGetRedirected.executeQuery();
				if (rs.next())
				{
					trueTarget = rs.getString("to_title");
				}
				else
				{
					pstGetTrueTitle.setString(1, target);
					ResultSet rs1 = pstGetTrueTitle.executeQuery();
					if (rs1.next())
					{
						trueTarget = target;
					}
					rs1.close();
				}
				rs.close();
			}
			catch (SQLException e)
			{
				Debug.warning(
						WikiParsingSAXHandler.class,
						String.format("error when looking up redirects for %s, error message: %s", target,
								e.getMessage()));
			}
			
			return trueTarget;
		}
	}

}
