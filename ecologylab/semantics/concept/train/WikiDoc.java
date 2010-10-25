package ecologylab.semantics.concept.train;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.detect.Concept;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.Doc;
import ecologylab.semantics.concept.detect.Surface;
import ecologylab.semantics.concept.detect.TrieDict;

public class WikiDoc extends Doc
{

	private static PreparedStatement	pstLinkedConcepts;

	private static PreparedStatement	pstWikiText;

	static
	{
		pstLinkedConcepts = DatabaseFacade.get().getPreparedStatement(
				"SELECT text FROM wikitexts WHERE title=?;");
		pstWikiText = DatabaseFacade.get().getPreparedStatement(
				"SELECT to_title, surface FROM wikilinks WHERE from_title=?;");
	}

	public static WikiDoc get(String title, TrieDict dict) throws SQLException
	{
		String text = getWikiText(title);
		if (text != null && text.length() > 0)
		{
			return new WikiDoc(title, text, dict);
		}
		return null;
	}

	private synchronized static String getWikiText(String title) throws SQLException
	{
		String wikiText = "";

		pstWikiText.setString(1, title);
		ResultSet rs = pstWikiText.executeQuery();
		if (rs.next())
		{
			wikiText = rs.getString("text");
		}
		rs.close();

		return wikiText;
	}

	private Context								context;

	private Map<Concept, Surface>	linkedConcepts;

	private Map<Surface, Concept>	linkedSurfaces;

	public WikiDoc(String title, String text, TrieDict dictionary) throws SQLException
	{
		super(title, text, dictionary);
	}

	public Context getContext()
	{
		if (context == null)
		{
			context = new Context();

			for (Concept concept : getLinkedConcepts().keySet())
			{
				context.addConcept(concept, getLinkedConcepts().get(concept));
			}

			for (Surface surface : getUnambiSurfaces())
			{
				Concept concept = (Concept) surface.getSenses().toArray()[0];
				context.addConcept(concept, surface);
			}
		}
		return context;
	}

	public Map<Concept, Surface> getLinkedConcepts()
	{
		if (linkedConcepts == null)
		{
			try
			{
				getLinks();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return linkedConcepts;
	}

	public Map<Surface, Concept> getLinkedSurfaces()
	{
		if (linkedSurfaces == null)
		{
			try
			{
				getLinks();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return linkedSurfaces;
	}

	private void getLinks() throws SQLException
	{
		linkedConcepts = new HashMap<Concept, Surface>();
		linkedSurfaces = new HashMap<Surface, Concept>();

		synchronized (pstLinkedConcepts)
		{
			pstLinkedConcepts.setString(1, getTitle());
			ResultSet rs = pstLinkedConcepts.executeQuery();
			while (rs.next())
			{
				String toTitle = rs.getString("to_title");
				String surface = rs.getString("surface");
				Concept c = Concept.get(toTitle);
				Surface s = Surface.get(surface);

				linkedConcepts.put(c, s);
				linkedSurfaces.put(s, c);
			}
			rs.close();
		}
	}

}
