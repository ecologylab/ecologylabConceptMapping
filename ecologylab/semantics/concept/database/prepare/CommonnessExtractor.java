package ecologylab.semantics.concept.database.prepare;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.utils.CollectionUtils;
import ecologylab.semantics.concept.wikipedia.StringPool;

/**
 * This class is used to extract commonness given the sorted surface file.
 * 
 * @author quyin
 * 
 */
public class CommonnessExtractor
{

	private DatabaseAdapter	da;

	private String					unambiSurfaceFilePath;

	private String					ambiSurfaceFilePath;

	public CommonnessExtractor(DatabaseAdapter databaseAdapter, String unambiSurfaceFilePath,
			String ambiSurfaceFilePath)
	{
		if (databaseAdapter != null)
			this.da = databaseAdapter;
		else
			this.da = DatabaseAdapter.get();

		this.unambiSurfaceFilePath = unambiSurfaceFilePath;
		this.ambiSurfaceFilePath = ambiSurfaceFilePath;
	}

	public void extract(String surface) throws SQLException
	{
		Map<String, Integer> cc = getConceptCountForSurface(surface);
		int n = (int) CollectionUtils.sum(cc.values());
		for (String concept : cc.keySet())
		{
			double commonness = cc.get(concept) / (double) n;
			insertCommonness(surface, concept, commonness);
		}

		if (cc.size() == 1)
			StringPool.get(unambiSurfaceFilePath).addLine(surface);
		if (cc.size() > 1)
			StringPool.get(ambiSurfaceFilePath).addLine(surface);
	}

	private void insertCommonness(String surface, String concept, double commonness)
			throws SQLException
	{
		PreparedStatement pst = da.getPreparedStatement("INSERT INTO commonness VALUES (?, ?, ?)");
		pst.setString(1, surface);
		pst.setString(2, concept);
		pst.setDouble(3, commonness);
		pst.executeUpdate();
	}

	private Map<String, Integer> getConceptCountForSurface(String surface) throws SQLException
	{
		Map<String, Integer> cc = new HashMap<String, Integer>();

		PreparedStatement pst = da
				.getPreparedStatement("SELECT to_concept, count(*) FROM inlinks WHERE surface=? GROUP BY to_concept ORDER BY to_concept;");
		pst.setString(1, surface);
		ResultSet rs = (ResultSet) pst.executeQuery();
		while (rs.next())
		{
			String concept = rs.getString("to_concept");
			int count = rs.getInt("count");
			cc.put(concept, count);
		}
		return cc;
	}

}
