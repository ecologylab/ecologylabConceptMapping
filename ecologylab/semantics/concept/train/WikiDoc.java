package ecologylab.semantics.concept.train;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.detect.Concept;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.Doc;
import ecologylab.semantics.concept.detect.Surface;
import ecologylab.semantics.concept.detect.SurfaceDictionary;

public class WikiDoc extends Doc
{

	public static WikiDoc get(String title, SurfaceDictionary dict) throws SQLException
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

		PreparedStatement pstWikiText = DatabaseFacade.get().getPreparedStatement(
				"SELECT text FROM wikitexts WHERE title=?;");
		synchronized (pstWikiText)
		{
			pstWikiText.setString(1, title);
			ResultSet rs = pstWikiText.executeQuery();
			if (rs.next())
			{
				wikiText = rs.getString("text");
			}
			rs.close();
		}

		return wikiText;
	}

	private Context								context;

	private Map<Concept, Surface>	linkedConcepts;

	private Map<Surface, Concept>	linkedSurfaces;

	public WikiDoc(String title, String text, SurfaceDictionary dictionary) throws SQLException
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
				Set<Concept> senses = surface.getSenses();
				if (senses.size() > 0)
				{
					Concept concept = (Concept) senses.toArray()[0];
					context.addConcept(concept, surface);
				}
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

		PreparedStatement pstLinkedConcepts = DatabaseFacade.get().getPreparedStatement(
				"SELECT to_title, surface FROM wikilinks WHERE from_title=?;");
		synchronized (pstLinkedConcepts)
		{
			pstLinkedConcepts.setString(1, getTitle());
			ResultSet rs = pstLinkedConcepts.executeQuery();
			while (rs.next())
			{
				String toTitle = rs.getString("to_title");
				String surface = rs.getString("surface");
				Concept c = new Concept(toTitle);
				Surface s = new Surface(surface);

				linkedConcepts.put(c, s);
				linkedSurfaces.put(s, c);
			}
			rs.close();
		}
	}
	
	public void recycle()
	{
		super.recycle();
		if (context != null)
			context.recycle();
		if (linkedConcepts != null)
			linkedConcepts.clear();
		if (linkedSurfaces != null)
			linkedSurfaces.clear();
	}

}
