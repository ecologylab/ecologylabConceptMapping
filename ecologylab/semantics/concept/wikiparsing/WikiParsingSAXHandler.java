package ecologylab.semantics.concept.wikiparsing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.utils.TextUtils;
import ecologylab.semantics.concept.utils.WikiUtils;

public class WikiParsingSAXHandler extends DefaultHandler
{

	private StringBuilder	currentText;

	private String				wikiTitle;

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

	private static synchronized void saveWikiLink(String from, String to, String surface)
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

			PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
					"INSERT INTO wikilinks VALUES (?, ?, ?);");
			try
			{
				ps.setString(1, from);
				ps.setString(2, trueTarget);
				ps.setString(3, normedSurface);
				ps.execute();
			}
			catch (SQLException e)
			{
				Debug.warning(WikiParsingSAXHandler.class, String.format(
						"error when saving wikilink: %s -> %s: %s, error message: %s", from, to, surface,
						e.getMessage()));
			}
		}
	}

	private static String initcap(String s)
	{
		return s == null ? null :
				s.length() == 0 ? "" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	private static synchronized void saveWikiText(String title, String text)
	{
		if (title == null || text == null)
			return;

		PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
				"INSERT INTO wikitexts VALUES (?,?);");
		try
		{
			ps.setString(1, title);
			ps.setString(2, text);
			ps.execute();
		}
		catch (SQLException e)
		{
			Debug.warning(
					WikiParsingSAXHandler.class,
					String.format("error when saving wikitext for %s, error message: %s", title,
							e.getMessage()));
		}
	}

	private static synchronized String getRedirectedOrTrueTitle(String target)
	{
		PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
				"SELECT to_title FROM redirects WHERE from_title=?;");
		PreparedStatement ps2 = DatabaseAdapter.get().getPreparedStatement(
				"SELECT title FROM dbp_titles WHERE title=?;");
		try
		{
			ps.setString(1, target);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				return rs.getString("to_title");
			}
			else
			{
				ps2.setString(1, target);
				rs = ps2.executeQuery();
				if (rs.next())
				{
					return target;
				}
			}
		}
		catch (SQLException e)
		{
			Debug.warning(
					WikiParsingSAXHandler.class,
					String.format("error when looking up redirects for %s, error message: %s", target,
							e.getMessage()));
		}
		
		return null;
	}

	private static void testSaveWikiLink(String from, String to, String surface)
	{
		String trueTarget = getRedirectedOrTrueTitle(to);
		String normedSurface = TextUtils.normalize(surface);

		System.out.format("LINK:\t%s\t(%s)\t%s\n", from, normedSurface, trueTarget);
	}

	private static void testSaveWikiText(String title, String text)
	{
		System.out.format("TEXT: %s\n%s\n\n", title, text);
	}

}
