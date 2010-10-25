package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.utils.CollectionUtils;

/**
 * This class is used to extract commonness given the sorted surface file.
 * 
 * @author quyin
 * 
 */
public class CommonnessCalculator implements PreparationConstants
{

	private PreparedStatement	stInsertCommonness;

	private PreparedStatement	stConceptCount;

	public CommonnessCalculator() throws SQLException
	{
		stInsertCommonness = DatabaseFacade.get().getPreparedStatement("INSERT INTO commonness VALUES (?, ?, ?)");
		stConceptCount = DatabaseFacade.get().getPreparedStatement("SELECT to_title, count(to_title) AS count FROM wikilinks, dbp_titles WHERE surface=? AND to_title=title GROUP BY to_title ORDER BY count DESC;");

		DatabaseFacade.get().executeSql("TRUNCATE commonness;");
	}

	public void computeAll() throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(freqSurfacesWithConceptCountFilePath));
		BufferedReader br = new BufferedReader(new FileReader(freqSurfacesFilePath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String surface = line.trim().split("\t")[0];

			try
			{
				// compute commonness and store
				Map<String, Integer> cc = getConceptCountForSurface(surface);
				int n = (int) CollectionUtils.sum(cc.values());
				for (String concept : cc.keySet())
				{
					double commonness = cc.get(concept) / (double) n;
					storeCommonness(surface, concept, commonness);
				}

				bw.write(surface + "\t" + cc.size());
				bw.newLine();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		br.close();
		bw.close();
		
		try
		{
			stInsertCommonness.close();
			stConceptCount.close();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void storeCommonness(String surface, String concept, double commonness)
			throws SQLException
	{
		stInsertCommonness.setString(1, surface);
		stInsertCommonness.setString(2, concept);
		stInsertCommonness.setDouble(3, commonness);
		stInsertCommonness.executeUpdate();
	}

	private Map<String, Integer> getConceptCountForSurface(String surface) throws SQLException
	{
		Map<String, Integer> cc = new HashMap<String, Integer>();

		stConceptCount.setString(1, surface);
		ResultSet rs = (ResultSet) stConceptCount.executeQuery();
		while (rs.next())
		{
			String concept = rs.getString("to_title");
			int count = rs.getInt("count");
			cc.put(concept, count);
		}
		rs.close();

		return cc;
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		CommonnessCalculator cc = new CommonnessCalculator();
		cc.computeAll();
	}

}
