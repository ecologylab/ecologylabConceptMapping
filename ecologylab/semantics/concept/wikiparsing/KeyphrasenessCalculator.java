package ecologylab.semantics.concept.wikiparsing;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.detect.Doc;
import ecologylab.semantics.concept.detect.Surface;
import ecologylab.semantics.concept.detect.SurfaceDictionary;

public class KeyphrasenessCalculator
{

	private SurfaceDictionary	dictionary;

	public KeyphrasenessCalculator(SurfaceDictionary dictionary) throws SQLException
	{
		this.dictionary = dictionary;
	}

	public void compute(int offset, int limit) throws SQLException
	{
		int i = 0;
		int interval = 1000;
		long t0 = System.currentTimeMillis();

		Statement st = DatabaseFacade.get().getStatement();
		String sql= String.format("SELECT title FROM freq_concepts OFFSET %d LIMIT %d;", offset, limit);
		ResultSet rs = st.executeQuery(sql);
		while (rs.next())
		{
			String concept = "";

			try
			{
				concept = rs.getString("title");

				// count all occurrences
				String text = getWikiText(concept);
				Doc doc = new Doc(concept, text, dictionary);
				for (Surface surface : doc.getUnambiSurfaces())
				{
					countSurfaceOccurrence(surface.word);
				}
				for (Surface surface : doc.getAmbiSurfaces())
				{
					countSurfaceOccurrence(surface.word);
				}

				// count linked occurrences
				Set<String> linkedSurfaces = getLinkedSurfaces(concept);
				for (String surface : linkedSurfaces)
				{
					countLinkedSurfaceOccurrence(surface);
				}
			}
			catch (SQLException e)
			{
				System.err.println("ERROR: processing " + concept + ": " + e.getMessage());
			}

			i++;
			if (i % interval == 0)
			{
				long dt = System.currentTimeMillis() - t0;
				System.out.println(i + " primary concepts processed, " + dt + " ms.");
				t0 += dt;
			}
			else
			{
				System.out.print(".");
			}
		}
		rs.close();
		st.close();
	}

	protected static String getWikiText(String concept) throws SQLException
	{
		String wikiText = "";

		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"SELECT text FROM wikitexts WHERE title=?;");
		pst.setString(1, concept);
		ResultSet rs = pst.executeQuery();
		if (rs.next())
		{
			wikiText = rs.getString("text");
		}
		rs.close();

		return wikiText;
	}

	protected static Set<String> getLinkedSurfaces(String concept) throws SQLException
	{
		Set<String> rst = new HashSet<String>();

		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"SELECT surface FROM wikilinks WHERE from_title=?;");
		pst.setString(1, concept);
		ResultSet rs = pst.executeQuery();
		while (rs.next())
		{
			String surface = rs.getString("surface");
			rst.add(surface);
		}
		rs.close();

		return rst;
	}

	private int countSurfaceOccurrence(String surface) throws SQLException
	{
		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"UPDATE surface_occurrences SET total = total + 1 WHERE surface = ?;");
		pst.setString(1, surface);
		return pst.executeUpdate();
	}

	private int countLinkedSurfaceOccurrence(String surface) throws SQLException
	{
		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"UPDATE surface_occurrences SET labeled = labeled + 1 WHERE surface = ?;");
		pst.setString(1, surface);
		return pst.executeUpdate();
	}
	
	public static void main(String[] args) throws IOException, SQLException
	{
		if (args.length != 2)
		{
			System.err.println("args: <offset> <limit>");
			return;
		}
		
		int offset = Integer.parseInt(args[0]);
		int limit = Integer.parseInt(args[1]);
		
		KeyphrasenessCalculator kc = new KeyphrasenessCalculator(SurfaceDictionary.get(ConceptConstants.DICTIONARY_PATH));
		kc.compute(offset, limit);
	}

}
