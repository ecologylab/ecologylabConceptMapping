package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.utils.CollectionUtils;
import ecologylab.semantics.concept.utils.StopWordsUtils;

public class FrequentConceptsIdentifier
{
	private static final int		threshold							= 5;

	private static final String	freqConceptsFilePath	= "data/freq-concepts.lst";

	private static final String	freqSurfacesFilePath	= "data/freq-surfaces.lst";

	private static final String	sql										= "SELECT to_title, count(*) as count FROM wikilinks GROUP BY to_title HAVING count(*) > "
																												+ threshold + " ORDER BY count;";

	public void identify() throws IOException, SQLException
	{
		List<String> concepts = new ArrayList<String>();
		BufferedWriter bw = new BufferedWriter(new FileWriter(freqConceptsFilePath));
		ResultSet rs = DatabaseAdapter.get().executeQuerySql(sql);
		while (rs.next())
		{
			String title = rs.getString("to_title");
			int count = rs.getInt("count");
			bw.write(String.format("%s\t%d", title, count));
			bw.newLine();
			concepts.add(title);
		}
		rs.close();
		bw.close();

		System.out.println(concepts.size() + " frequent concepts detected.");

		List<String> surfaces = new LinkedList<String>();
		for (String concept : concepts)
		{
			surfaces.addAll(DatabaseUtils.get().querySurfaces(concept));
		}
		Collections.sort(surfaces);
		CollectionUtils.unique(surfaces);

		BufferedWriter bw2 = new BufferedWriter(new FileWriter(freqSurfacesFilePath));
		for (String surface : surfaces)
		{
			if (StopWordsUtils.containsLetter(surface) && !StopWordsUtils.isStopWord(surface)) 
			{
				bw2.write(surface);
				bw2.newLine();
			}
		}
		bw2.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
