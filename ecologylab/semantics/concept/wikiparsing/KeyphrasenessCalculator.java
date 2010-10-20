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
import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.detect.Doc;
import ecologylab.semantics.concept.detect.Surface;
import ecologylab.semantics.concept.detect.TrieDict;

public class KeyphrasenessCalculator extends Debug
{

	TrieDict					dictionary;

	PreparedStatement	pstInitSurface;

	PreparedStatement	pstWikiText;

	PreparedStatement	pstOutlinkSurfaces;

	PreparedStatement	pstTotalOccurrence;

	PreparedStatement	pstLabeledOccurrence;

	public KeyphrasenessCalculator(TrieDict dictionary) throws SQLException
	{
		this.dictionary = dictionary;
		init(dictionary);

		pstInitSurface = DatabaseFacade.get().getConnection()
				.prepareStatement("INSERT INTO surface_occurrences VALUES (?, 0, 0);");
		pstWikiText = DatabaseFacade.get().getConnection()
				.prepareStatement("SELECT text FROM wikitexts WHERE title=?;");
		pstOutlinkSurfaces = DatabaseFacade.get().getConnection()
				.prepareStatement("SELECT surface FROM wikilinks WHERE from_title=?;");
		pstTotalOccurrence = DatabaseFacade.get().getConnection()
				.prepareStatement("UPDATE surface_occurrences SET total = total + 1 WHERE surface = ?;");
		pstLabeledOccurrence = DatabaseFacade.get().getConnection()
				.prepareStatement("UPDATE surface_occurrences SET labeled = labeled + 1 WHERE surface = ?;");
	}

	private void init(TrieDict dictionary)
	{
		int i = 0;
		int interval = 1000;

		String[] words = dictionary.getAll();
		for (String word : words)
		{
			i++;
			if (i % interval == 0)
			{
				debug(i + " surfaces initiated.");
			}

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
		pstInitSurface.setString(1, word);
		return pstInitSurface.executeUpdate();
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

			i++;
			if (i % interval == 0)
			{
				debug(i + " primary concepts processed.");
			}

			try
			{
				// count all occurrences
				String text = getWikiText(concept);
				Doc doc = new Doc(concept.toString(), text, dictionary);
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
				warning("compute(): error processing " + concept + ": " + e.getMessage());
			}
		}

		// calculate keyphraseness based on surface occurrences
		try
		{
			int c = DatabaseFacade.get().executeUpdateSql(
							"INSERT INTO keyphraseness SELECT surface, labeled*1.0/total AS keyphraseness FROM surface_occurrences;"
					);
			debug(c + " surfaces calculated keyphraseness.");
		}
		catch (SQLException e)
		{
			warning("execution error when calculating keyphraseness: " + e.getMessage());
		}
		
		try
		{
			pstInitSurface.close();
			pstWikiText.close();
			pstOutlinkSurfaces.close();
			pstTotalOccurrence.close();
			pstLabeledOccurrence.close();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getWikiText(String concept) throws SQLException
	{
		String wikiText = "";
		
		pstWikiText.setString(1, concept);
		ResultSet rs = pstWikiText.executeQuery();
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
		
		pstOutlinkSurfaces.setString(1, concept);
		ResultSet rs = pstOutlinkSurfaces.executeQuery();
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
		pstTotalOccurrence.setString(1, surface);
		return pstTotalOccurrence.executeUpdate();
	}

	private int countLinkedSurfaceOccurrence(String surface) throws SQLException
	{
		pstLabeledOccurrence.setString(1, surface);
		return pstLabeledOccurrence.executeUpdate();
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IOException, SQLException
	{
		KeyphrasenessCalculator kc = new KeyphrasenessCalculator(TrieDict.load(new File("freq-surfaces.dict")));
		kc.compute(new File("data/primary-concepts.lst"));
		DatabaseFacade.get().close();
	}

}
