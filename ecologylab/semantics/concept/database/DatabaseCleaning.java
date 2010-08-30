package ecologylab.semantics.concept.database;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import ecologylab.semantics.concept.utils.CollectionUtils;
import ecologylab.semantics.model.text.TermDictionary;

public class DatabaseCleaning
{
	static String[] obviousStopWords;
	
	static String[] moreStopWords;
	
	DatabaseAdapter	da	= DatabaseAdapter.get();
	
	static
	{
		obviousStopWords = TermDictionary.mostObviousStopWordStrings;
		moreStopWords = TermDictionary.moreStopWordStrings;
		Arrays.sort(obviousStopWords);
		Arrays.sort(moreStopWords);
	}

	boolean isStopWord(String word)
	{
		word = word.toLowerCase();
		if (CollectionUtils.binarySearch(word, obviousStopWords))
			return true;
		if (CollectionUtils.binarySearch(word, moreStopWords))
			return true;
		return false;
	}
	
	boolean containsLetter(String word)
	{
		for (int i = 0; i < word.length(); ++i)
		{
			char c = word.charAt(i);
			if (Character.isLetter(c))
				return true;
		}
		return false;
	}
	
	public void generateSurfaceList(int minOccur, String outFilepath) throws FileNotFoundException,
			SQLException
	{
		PrintWriter out = new PrintWriter(outFilepath);

		PreparedStatement ps = da
				.getPreparedStatement("SELECT surface FROM surface_count WHERE count >= ? ORDER BY count DESC;");
		ps.setInt(1, minOccur);

		int n = 0;
		ResultSet rs = ps.executeQuery();
		while (rs.next())
		{
			String surface = rs.getString("surface");
			if (surface != null && !surface.isEmpty() && !isStopWord(surface) && containsLetter(surface))
			{
				out.format("%s\n", surface);
				n++;
			}
		}

		System.out.println(n + " surfaces saved.");
		out.close();
	}

	public void generateConceptList(int minInlink, String outFilepath) throws FileNotFoundException,
			SQLException
	{
		PrintWriter out = new PrintWriter(outFilepath);

		PreparedStatement ps = da
				.getPreparedStatement("SELECT concept FROM concept_count WHERE count >= ? ORDER BY count DESC;");
		ps.setInt(1, minInlink);

		int n = 0;
		ResultSet rs = ps.executeQuery();
		while (rs.next())
		{
			String concept = rs.getString("concept");
			if (concept != null && !concept.isEmpty())
			{
				out.println(concept);
				n++;
			}
		}

		System.out.println(n + " concepts saved.");
		out.close();
	}

	public static void main(String[] args) throws FileNotFoundException, SQLException
	{
		DatabaseCleaning dc = new DatabaseCleaning();
		dc.generateSurfaceList(6, "data/freq-surfaces.dat");
	}

}
