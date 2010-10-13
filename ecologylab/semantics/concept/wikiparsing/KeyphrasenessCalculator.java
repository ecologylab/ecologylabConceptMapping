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

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.text.SurfaceExtractor;
import ecologylab.semantics.concept.text.TrieDict;

public class KeyphrasenessCalculator extends Debug
{

	SurfaceExtractor	surfaceExtractor;

	public KeyphrasenessCalculator(TrieDict dictionary)
	{
		surfaceExtractor = new SurfaceExtractor(dictionary);
		init(dictionary);
	}

	private void init(TrieDict dictionary)
	{
		String[] words = dictionary.getAll();
		for (String word : words)
		{
			try
			{
				int c = initSurface(word);
				assert c == 1 : "insertion failed: initSurface() returning " + c;
			}
			catch (SQLException e)
			{
				warning("init(): error processing " + word + ": " + e.getMessage());
			}
		}
	}

	private int initSurface(String word) throws SQLException
	{
		PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
				"INSERT INTO surface_occurrences VALUES (?, 0, 0);");
		ps.setString(1, word);
		return ps.executeUpdate();
	}

	public void compute(File primaryConcepts) throws IOException
	{
		BufferedReader brPrimaryConcepts = new BufferedReader(new FileReader(primaryConcepts));
		String line = null;
		while ((line = brPrimaryConcepts.readLine()) != null)
		{
			String concept = line.trim();

			try
			{
				// count all occurrences
				String text = getWikiText(concept);
				Set<String> allSurfaces = surfaceExtractor.extract(text);
				for (String surface : allSurfaces)
				{
					countSurfaceOccurrence(surface);
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
				warning("compute(): error processing " + concept + ": " + e.getMessage());
			}
		}

		// calculate keyphraseness based on surface occurrences
		try
		{
			int c = DatabaseAdapter
					.get()
					.executeUpdateSql(
							"INSERT INTO keyphraseness SELECT surface, labeled*1.0/total AS keyphraseness FROM surface_occurrences;");
			debug(c + " surfaces calculated keyphraseness.");
		}
		catch (SQLException e)
		{
			warning("execution error when calculating keyphraseness: " + e.getMessage());
		}
	}

	private String getWikiText(String concept) throws SQLException
	{
		PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
				"SELECT text FROM wikitexts WHERE title=?;");
		ps.setString(1, concept);
		ResultSet rs = ps.executeQuery();
		if (rs.next())
		{
			return rs.getString("text");
		}
		else
		{
			return "";
		}
	}

	private Set<String> getLinkedSurfaces(String concept) throws SQLException
	{
		Set<String> rst = new HashSet<String>();
		PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
				"SELECT surface FROM wikilinks WHERE from_title=?;");
		ps.setString(1, concept);
		ResultSet rs = ps.executeQuery();
		while (rs.next())
		{
			String surface = rs.getString("surface");
			rst.add(surface);
		}
		return rst;
	}

	private int countSurfaceOccurrence(String surface) throws SQLException
	{
		PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
				"UPDATE surface_occurrences SET total = total + 1 WHERE surface = ?;");
		ps.setString(1, surface);
		return ps.executeUpdate();
	}

	private int countLinkedSurfaceOccurrence(String surface) throws SQLException
	{
		PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
				"UPDATE surface_occurrences SET labeled = labeled + 1 WHERE surface = ?;");
		ps.setString(1, surface);
		return ps.executeUpdate();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		KeyphrasenessCalculator kc = new KeyphrasenessCalculator(TrieDict.load(new File("freq-surfaces.dict")));
		kc.compute(new File("data/primary-concepts.lst"));
	}

}
