package ecologylab.semantics.concept.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class DatabaseUtils
{

	public static final int	NUM_ALL_CONCEPTS	= 2283272;

	private DatabaseAdapter	da;

	private Set<String>			surfaces					= new HashSet<String>();

	private Set<String>			concepts					= new HashSet<String>();

	public boolean hasSurface(String surface)
	{
		// if it is in the cache, we know that it is a surface
		if (surfaces.contains(surface))
			return true;

		// if not in the cache, query the database
		PreparedStatement st = da.getPreparedStatement("SELECT surface FROM surfaces WHERE surface=?");
		try
		{
			st.setString(1, surface);
			ResultSet rs = st.executeQuery();
			if (rs.next())
			{
				String result = rs.getString("surface");
				surfaces.add(result);
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public boolean hasConcept(String concept)
	{
		if (concepts.contains(concept))
			return true;

		PreparedStatement st = da
				.getPreparedStatement("SELECT concept FROM inlinks WHERE from_concept=?");
		try
		{
			st.setString(1, concept);
			ResultSet rs = st.executeQuery();
			if (rs.next())
			{
				String result = rs.getString("from_concept");
				concepts.add(result);
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public double queryKeyphraseness(String surface)
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT keyphraseness FROM keyphraseness WHERE surface=?;");

		try
		{
			st.setString(1, surface);
			ResultSet rs = st.executeQuery();
			rs.next();
			return rs.getDouble("keyphraseness");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return 0;
	}

	/* smaller relatedness indicates more relation between concepts */
	public double queryRelatedness(String concept1, String concept2)
	{
		if (concept1.equals(concept2))
			return 0;

		List<String> list1 = queryFromConceptsForConcept(concept1);
		List<String> list2 = queryFromConceptsForConcept(concept2);
		int s1 = list1.size();
		int s2 = list2.size();
		List<String> commonSublist = CollectionUtils.commonSublist(list1, list2);
		int s = commonSublist.size();
		if (s <= 0)
			return 1; // or Math.log will fail

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
		this.da = DatabaseAdapter.get();
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
}
