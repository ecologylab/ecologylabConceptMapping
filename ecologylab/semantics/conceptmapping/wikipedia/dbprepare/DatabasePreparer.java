package ecologylab.semantics.conceptmapping.wikipedia.dbprepare;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ecologylab.semantics.conceptmapping.wikipedia.db.DatabaseAdapter;
import ecologylab.semantics.conceptmapping.wikipedia.dbprepare.InlinkN3Parser.Inlink;

public class DatabasePreparer
{
	private DatabaseAdapter			da						= DatabaseAdapter.get("database-preparer");

	private String							inlinkFilepath;

	private String							keyphrasenessFilepath;

	private CommonnessExtractor	commonnessExtractor;

	private InlinkN3Parser			inlinkParser	= new InlinkN3Parser();

	public void prepare() throws IOException, SQLException
	{
		createTables();
		// convertInlink(inlinkFilepath);
		generateSurfaceTable();
		// convertKeyphraseness(keyphrasenessFilepath);
		calculateCommonness();
		createIndexes();
	}
	
	public void createTables() throws SQLException
	{
		// da.executeSql("CREATE TABLE inlinks (i INTEGER NOT NULL DEFAULT 0, to_concept VARCHAR NOT NULL, from_concept VARCHAR NOT NULL, surface VARCHAR NOT NULL);");
		// da.executeSql("CREATE TABLE keyphraseness (surface VARCHAR NOT NULL, keyphraseness DOUBLE NOT NULL DEFAULT 0);");
		da.executeSql("CREATE TABLE commonness (surface VARCHAR NOT NULL, concept VARCHAR NOT NULL, commonness DOUBLE NOT NULL DEFAULT 0);");
	}

	public void convertInlink(String inN3Filepath) throws IOException
	{
		PreparedStatement inlinkUpdateStatement = da
				.getPreparedStatement("INSERT INTO inlinks VALUES (?, ?, ?, ?)");

		BufferedReader br = new BufferedReader(new FileReader(inN3Filepath));
		int i = 0;
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.isEmpty())
				continue;
			i++;
			Inlink il = inlinkParser.parse(line);
			try
			{
				inlinkUpdateStatement.setInt(1, i);
				inlinkUpdateStatement.setString(2, il.toConcept);
				inlinkUpdateStatement.setString(3, il.fromConcept);
				inlinkUpdateStatement.setString(4, il.surface);
				inlinkUpdateStatement.executeUpdate();
			}
			catch (SQLException e)
			{
				System.err.println("insertion failed.");
				e.printStackTrace();
			}
		}
		br.close();
	}

	public void generateSurfaceTable() throws SQLException
	{
		String sql = "CREATE TABLE surfaces (surface, occurrence) AS "
				+ "SELECT surface, count(*) FROM inlinks GROUP BY surface ORDER BY count DESC;";
		da.executeSql(sql);
	}

	public void calculateCommonness() throws SQLException
	{
		ResultSet rs = da.executeQuerySql("SELECT surface FROM surfaces;");
		while (rs.next())
		{
			String surface = rs.getString("surface");
			commonnessExtractor.extract(surface);
		}
	}

	public void convertKeyphraseness(String commonnessFilepath) throws IOException
	{
		PreparedStatement keyphrasenessUpdateStatement = da
				.getPreparedStatement("INSERT INTO keyphraseness VALUES (?, ?)");
		BufferedReader br = new BufferedReader(new FileReader(commonnessFilepath));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.isEmpty())
				continue;

			int k = line.indexOf(' ');
			if (k < 0)
			{
				System.err.println("line parsing failed: " + line);
			}

			String keyphrasenessString = line.substring(0, k);
			String phrase = line.substring(k + 1);
			try
			{
				keyphrasenessUpdateStatement.setString(1, phrase);
				keyphrasenessUpdateStatement.setDouble(2, Double.valueOf(keyphrasenessString));
				keyphrasenessUpdateStatement.executeUpdate();
			}
			catch (SQLException e)
			{
				System.err.println("insertion failed: " + line);
				e.printStackTrace();
			}
		}
		br.close();
	}

	public void createIndexes() throws SQLException
	{
		// da.executeSql("CREATE INDEX inlinks_toconcept_index ON inlinks (to_concept);");
		// da.executeSql("CREATE INDEX inlinks_fromconcept_index ON inlinks (from_concept);");
		// da.executeSql("CREATE INDEX inlinks_surface_index ON inlinks (surface);");
		
		// da.executeSql("CREATE INDEX keyphraseness_surface_index ON keyphraseness (surface);");

		da.executeSql("CREATE INDEX commonness_surface_index ON commonness (surface);");
		da.executeSql("CREATE INDEX commonness_concept_index ON commonness (concept);");

		da.executeSql("CREATE INDEX surfaces_surface_index ON surfaces (surface);");
		da.executeSql("CREATE INDEX surfaces_occurrence_index ON surfaces (occurrence);");
	}

	public DatabasePreparer(String inlinkFilepath, String keyphrasenessFilepath,
			String unambiSurfacesFilepath, String ambiSurfacesFilepath)
	{
		this.inlinkFilepath = inlinkFilepath;
		this.keyphrasenessFilepath = keyphrasenessFilepath;
		this.commonnessExtractor = new CommonnessExtractor(da, unambiSurfacesFilepath,
				ambiSurfacesFilepath);
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		DatabasePreparer dc = new DatabasePreparer("C:/run/sorted/sorted-inlinks.n3",
				"S:/quyin/rada's data and processed/keyPhrasenessScore.2007", "unambi-surfaces.lst",
				"ambi-surfaces.lst");
		dc.prepare();
	}

}
