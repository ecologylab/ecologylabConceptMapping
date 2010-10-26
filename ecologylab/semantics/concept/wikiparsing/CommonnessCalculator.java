package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.utils.CollectionUtils;
import ecologylab.semantics.concept.utils.TextUtils;

/**
 * This class is used to extract commonness given the sorted surface file.
 * 
 * @author quyin
 * 
 */
public class CommonnessCalculator implements PreparationConstants
{
	
	private List<String> primaryConcepts;

	public CommonnessCalculator() throws SQLException, IOException
	{
		primaryConcepts = TextUtils.loadTxtAsSortedList(new File(primaryConceptsFilePath));
		DatabaseFacade.get().executeSql("TRUNCATE commonness;");
	}

	public void computeAll() throws IOException, SQLException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(freqSurfacesWithConceptCountFilePath));
		
		int i = 0; // counter
		
		Statement st = DatabaseFacade.get().getStatement();
		ResultSet rs = st.executeQuery("SELECT surface FROM freq_surfaces LIMIT 10;");
		while (rs.next())
		{
			try
			{
				// compute commonness and store
				String surface = rs.getString("surface");
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
			
			i++;
			System.out.print(".");
			if (i % 1000 == 0)
			{
				System.out.println(i + " surfaces processed.");
			}
		}
		rs.close();
		st.close();
		
		bw.close();
	}

	private void storeCommonness(String surface, String concept, double commonness)
			throws SQLException
	{
		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement("INSERT INTO commonness VALUES (?, ?, ?)");
		pst.setString(1, surface);
		pst.setString(2, concept);
		pst.setDouble(3, commonness);
		pst.executeUpdate();
	}

	private Map<String, Integer> getConceptCountForSurface(String surface) throws SQLException
	{
		Map<String, Integer> cc = new HashMap<String, Integer>();

		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"SELECT to_title, count(to_title) AS count FROM wikilinks WHERE surface=? GROUP BY to_title ORDER BY count DESC;");
		pst.setString(1, surface);
		ResultSet rs = (ResultSet) pst.executeQuery();
		while (rs.next())
		{
			String concept = rs.getString("to_title");
			int count = rs.getInt("count");
			
			if (isPrimaryConcept(concept))
				cc.put(concept, count);
		}
		rs.close();

		return cc;
	}

	private boolean isPrimaryConcept(String concept)
	{
		return Collections.binarySearch(primaryConcepts, concept) >= 0;
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		CommonnessCalculator cc = new CommonnessCalculator();
		cc.computeAll();
	}

}
