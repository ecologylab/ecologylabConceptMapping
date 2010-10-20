package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ecologylab.semantics.concept.database.DatabaseFacade;

public class FrequentSurfacesIdentifier implements PreparationConstants
{
	
	public FrequentSurfacesIdentifier(int threshold) throws SQLException
	{
		DatabaseFacade.get().executeSql("TRUNCATE freq_surfaces;");
		
		String sql =
				"INSERT INTO freq_surfaces" +
						"  SELECT surface, count(surface)" +
						"  FROM wikilinks" +
						"  GROUP BY surface" +
						"  HAVING count(surface) > " + threshold +
						"  ORDER BY count(surface) DESC;";

		DatabaseFacade.get().executeSql(sql);
	}

	public void identify() throws IOException, SQLException
	{
		String sql = "SELECT surface, count_of_references AS count FROM freq_surfaces ORDER BY count DESC";
		int n = 0;
		BufferedWriter bw = new BufferedWriter(new FileWriter(freqSurfacesFilePath));
		
		Statement st = DatabaseFacade.get().getConnection().createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next())
		{
			String surface = rs.getString("surface");
			int count_ref = rs.getInt("count");
			bw.write(String.format("%s\t%d", surface, count_ref));
			bw.newLine();
			
			n++;
			if (n % 1000 == 0)
				System.out.println(n + " surfaces processed ...");
		}
		rs.close();
		st.close();
		
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
		DatabaseFacade.get().close();
	}

}
