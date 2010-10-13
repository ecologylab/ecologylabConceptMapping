package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import ecologylab.semantics.concept.database.DatabaseAdapter;

public class FrequentSurfacesIdentifier implements PreparationConstants
{
	
	public FrequentSurfacesIdentifier(int threshold) throws SQLException
	{
		String sql1 =
				"DROP TABLE IF EXISTS freq_surfaces; ";

		String sql2 =
				"CREATE TABLE freq_surfaces (" +
						"  surface VARCHAR PRIMARY KEY," +
						"  count_of_references INTEGER NOT NULL" +
						") WITHOUT OIDS; ";

		String sql3 =
				"INSERT INTO freq_surfaces" +
						"  SELECT surface, count(surface) as count" +
						"  FROM wikilinks" +
						"  GROUP BY surface" +
						"  HAVING count(surface) > " + threshold +
						"  ORDER BY count DESC;";

		DatabaseAdapter.get().executeSql(sql1);
		DatabaseAdapter.get().executeSql(sql2);
		DatabaseAdapter.get().executeSql(sql3);
	}

	public void identify() throws IOException, SQLException
	{
		String sql = "SELECT surface, count_of_references AS count FROM freq_surfaces ORDER BY count DESC";
		int n = 0;
		BufferedWriter bw = new BufferedWriter(new FileWriter(freqSurfacesFilePath));
		ResultSet rs = DatabaseAdapter.get().executeQuerySql(sql);
		while (rs.next())
		{
			String surface = rs.getString("surface");
			int count = rs.getInt("count");
			bw.write(String.format("%s\t%d", surface, count));
			bw.newLine();
			n++;
		}
		rs.close();
		bw.close();

		System.out.println(n + " frequent surfaces recognized.");
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		System.err.println("args: <threshold>");
		
		int threshold = 5;
		
		if (args.length == 1)
		{
			threshold = Integer.parseInt(args[0]);
		}

		FrequentSurfacesIdentifier fsi = new FrequentSurfacesIdentifier(threshold);
		fsi.identify();
	}

}
