package ecologylab.semantics.concept.test;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.detect.SurfaceDictionary;
import ecologylab.semantics.concept.wikiparsing.KeyphrasenessCalculator;

public class TestKeyphraseness extends KeyphrasenessCalculator
{

	public TestKeyphraseness(SurfaceDictionary dictionary) throws SQLException
	{
		super(dictionary);
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException, IOException
	{
		SurfaceDictionary dict = SurfaceDictionary.load(new File(ConceptConstants.DICTIONARY_PATH));
		
		String testSurface = "united states 2000 census";
		System.out.println(dict.hasSurface(testSurface));
		Set<String> fromTitles = getFromTitles(testSurface);
		
		int total = 0;
		int labeled = 0;
		
		for (String fromTitle : fromTitles)
		{
			String text = getWikiText(fromTitle);
			Set<String> surfaces = dict.extractSurfaces(text);
			Set<String> linkedSurfaces = getLinkedSurfaces(fromTitle);
			
			if (surfaces.contains(testSurface))
			{
				total++;
			}
			if (linkedSurfaces.contains(testSurface))
			{
				labeled++;
			}
			
			if (!surfaces.contains(testSurface) && linkedSurfaces.contains(testSurface))
			{
				boolean contain = text.contains(testSurface);
				if (contain)
				{
					int i = text.indexOf(testSurface);
					int p = i - 10;
					if (p < 0)
						p = 0;
					int q = i + testSurface.length() + 10;
					if (q > text.length())
						q = text.length();
					System.out.format("...%s...\n", text.substring(p, q));
				}
				System.out.format("\t%s\n", fromTitle);
			}
		}
		
		System.out.format("%d / %d = %f\n", labeled, total, labeled * 1.0 / total);
	}

	private static Set<String> getFromTitles(String testSurface) throws SQLException
	{
		Set<String> fromTitles = new HashSet<String>();
		String sql = "SELECT from_title FROM wikilinks WHERE surface=?;";
		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(sql);
		pst.setString(1, testSurface);
		ResultSet rs = pst.executeQuery();
		while (rs.next())
		{
			String fromTitle = rs.getString("from_title");
			fromTitles.add(fromTitle);
		}
		rs.close();
		return fromTitles;
	}

}
