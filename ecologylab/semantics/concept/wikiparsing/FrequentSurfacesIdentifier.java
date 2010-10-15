package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.detect.Surface;

public class FrequentSurfacesIdentifier implements PreparationConstants
{
	
	public FrequentSurfacesIdentifier(int threshold) throws SQLException
	{
		String sql =
				"INSERT INTO freq_surfaces" +
						"  SELECT surface, count(surface)" +
						"  FROM wikilinks" +
						"  GROUP BY surface" +
						"  HAVING count(surface) > " + threshold +
						"  ORDER BY count(surface) DESC;";

		DatabaseAdapter.get().executeSql(sql);
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
			Surface s = Surface.get(surface);
			bw.write(String.format("%s\t%d", surface, s.getSenses().size()));
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
