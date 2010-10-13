package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.database.DatabaseAdapter;

/**
 * Used to sort primary concepts stored in database into a .lst file. This file is used in tasks
 * including wiki-parsing, etc. The reason not to use PostgreSQL's ORDER BY is that it does not
 * confirm with Java's sorting for Strings, which causes problems to binary search.
 * 
 * @author quyin
 * 
 */
public class PrimaryConceptsIdentifier extends Debug implements PreparationConstants
{

	public PrimaryConceptsIdentifier() throws SQLException
	{
		String sql1 =
				"DELETE FROM dbp_primary_concepts;";
		String sql2 =
				"INSERT INTO dbp_primary_concepts" +
						"  (SELECT name FROM dbp_titles" +
						"  EXCEPT" +
						"  SELECT name FROM dbp_redirects);";
		DatabaseAdapter.get().executeSql(sql1);
		DatabaseAdapter.get().executeSql(sql2);
	}

	public void identify() throws SQLException, IOException
	{
		String sql = "SELECT title FROM dbp_primary_concepts, dbp_titles WHERE dbp_primary_concepts.name = dbp_titles.name;";
		List<String> primaryConceptList = new ArrayList<String>();

		ResultSet rs = DatabaseAdapter.get().executeQuerySql(sql);
		while (rs.next())
		{
			String title = rs.getString("title");
			primaryConceptList.add(title);
		}
		rs.close();

		Collections.sort(primaryConceptList);

		BufferedWriter bw = new BufferedWriter(new FileWriter(primaryConceptsFilePath));
		for (String concept : primaryConceptList)
		{
			bw.write(concept);
			bw.newLine();
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		PrimaryConceptsIdentifier pci = new PrimaryConceptsIdentifier();
		pci.identify();
	}

}