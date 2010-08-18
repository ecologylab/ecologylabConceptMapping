package ecologylab.semantics.concept.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class DatabaseUtils extends Debug
{

	public static final int	NUM_ALL_CONCEPTS	= 2283272;

	private DatabaseAdapter	da;

	private Set<String>			surfaces					= new HashSet<String>();
	
	public boolean hasSurface(String surface)
	{
		if (surfaces.contains(surface))
			return true;
		return false;
	}

	public double queryKeyphraseness(String surface)
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT keyphraseness FROM keyphraseness WHERE surface=?;");

		try
		{
			st.setString(1, surface.toLowerCase().replaceAll("[^a-z0-9]", " "));
			ResultSet rs = st.executeQuery();
			if (rs.next())
			{
				return rs.getDouble("keyphraseness");
			}
			else
			{
				warning("keyphraseness not found: " + surface);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return 0;
	}

	/* smaller relatedness indicates more relation between concepts */
	public double queryRelatedness(List<String> inlinkList1, List<String> inlinkList2)
	{
		if (inlinkList1.equals(inlinkList2))
			return 0;

		int s1 = inlinkList1.size();
		int s2 = inlinkList2.size();
		List<String> commonSublist = CollectionUtils.commonSublist(inlinkList1, inlinkList2);
		int s = commonSublist.size();
		if (s <= 0)
			return 0; // or Math.log will fail

		int smin = ((s1 > s2) ? s2 : s1);
		int smax = ((s1 > s2) ? s1 : s2);

		int W = NUM_ALL_CONCEPTS;
		return (Math.log(smax) - Math.log(s)) / (Math.log(W) - Math.log(smin));
	}

	/**
	 * 
	 * @param surface
	 * @return a map from sense (concept) to commonness.
	 */
	public Map<String, Double> querySenses(String surface)
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT concept, commonness FROM commonness WHERE surface=?;");
		Map<String, Double> rst = new HashMap<String, Double>();
		try
		{
			st.setString(1, surface);
			ResultSet rs = st.executeQuery();
			while (rs.next())
			{
				rst.put(rs.getString("concept"), rs.getDouble("commonness"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return rst;
	}

	/**
	 * return a ASCENDINGLY ORDERED list of from_concept (source of a link) given a to_concept
	 * (destination of a link).
	 * 
	 * @param toConcept
	 * @return
	 */
	public List<String> queryFromConceptsForConcept(String toConcept)
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT DISTINCT from_concept FROM inlinks WHERE to_concept=? ORDER BY from_concept ASC;");
		List<String> rst = new ArrayList<String>();
		try
		{
			st.setString(1, toConcept);
			ResultSet rs = st.executeQuery();
			while (rs.next())
			{
				rst.add(rs.getString("from_concept"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return rst;
	}

	// singleton

	private DatabaseUtils()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(DatabaseConstants.freqSurfaceDataFile));
			String surface = null;
			while ((surface = br.readLine()) != null)
			{
				surfaces.add(surface);
			}
			br.close();
			debug("frequent surfaces loaded.");
			
			da = DatabaseAdapter.get();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static DatabaseUtils	utils	= null;

	public static DatabaseUtils get()
	{
		if (utils == null)
		{
			utils = new DatabaseUtils();
		}
		return utils;
	}
	
	public static void main(String[] args)
	{
		DatabaseUtils.get();
	}
}
