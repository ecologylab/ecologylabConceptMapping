package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.detect.Doc;
import ecologylab.semantics.concept.detect.Surface;
import ecologylab.semantics.concept.detect.TrieDict;

public class KeyphrasenessCalculator
{

	private TrieDict	dictionary;

	public KeyphrasenessCalculator(TrieDict dictionary) throws SQLException
	{
		this.dictionary = dictionary;
		DatabaseFacade.get().executeUpdateSql("TRUNCATE surface_occurrences;");
		DatabaseFacade.get().executeUpdateSql(
				"INSERT INTO surface_occurrences SELECT surface, 0, 0 FROM freq_surfaces;");
		System.out.println("surface_occurrences initialized.");
	}

	public void compute(File primaryConcepts) throws IOException
	{
		int i = 0;
		int interval = 1000;

		BufferedReader brPrimaryConcepts = new BufferedReader(new FileReader(primaryConcepts));
		String line = null;
		while ((line = brPrimaryConcepts.readLine()) != null)
		{
			String concept = line.trim();

			try
			{
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
				System.out.println(i + " primary concepts processed.");
			}
			else
			{
				System.out.print(".");
			}
		}
		brPrimaryConcepts.close();

		// calculate keyphraseness based on surface occurrences
		try
		{
			int c = DatabaseFacade
					.get()
					.executeUpdateSql(
							"INSERT INTO keyphraseness SELECT surface, labeled*1.0/total AS keyphraseness FROM surface_occurrences;");
			System.out.println(c + " surfaces calculated keyphraseness.");
		}
		catch (SQLException e)
		{
			System.err.println("ERROR: calculating keyphraseness: " + e.getMessage());
		}
	}

	private String getWikiText(String concept) throws SQLException
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

	private Set<String> getLinkedSurfaces(String concept) throws SQLException
	{
		Set<String> rst = new HashSet<String>();

		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"SELECT DISTINCT surface FROM wikilinks WHERE from_title=?;");
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

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws IOException, SQLException
	{
		KeyphrasenessCalculator kc = new KeyphrasenessCalculator(TrieDict.load(new File(
				"data/freq-surfaces-with-concept-count.lst")));
		kc.compute(new File("data/primary-concepts.lst"));
	}

}
