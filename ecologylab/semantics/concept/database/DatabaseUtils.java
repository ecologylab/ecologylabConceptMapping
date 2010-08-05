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
import ecologylab.semantics.concept.utils.Pair;

public class DatabaseUtils
{

	public static final int										NUM_ALL_CONCEPTS	= 2283272;

	private DatabaseAdapter										da;

	private Set<String>												surfaces					= new HashSet<String>();

	private Set<String>												concepts					= new HashSet<String>();

	private Map<Pair<String, String>, Double>	cachedRelatedness	= new HashMap<Pair<String, String>, Double>();

	private HashMap<String, List<String>>	cachedFromConcepts = new HashMap<String, List<String>>();

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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}


	public double queryKeyphraseness(String surface) throws SQLException
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT keyphraseness FROM keyphraseness WHERE surface=?;");

		st.setString(1, surface);
		ResultSet rs = st.executeQuery();
		if (!rs.next())
			return 0;

		double keyphraseness = rs.getDouble("keyphraseness");

		if (rs.next())
		{
			String warning = String.format(
					"warning: duplicate keyphraseness entries for surface '%s' found.", surface);
			System.err.println(warning);
		}
		return keyphraseness;
	}

	public double queryRelatedness(String concept1, String concept2) throws SQLException
	{
		if (concept1.equals(concept2))
			return 0;

		Pair<String, String> conceptPair = new Pair<String, String>(concept1, concept2);

		if (!cachedRelatedness.containsKey(conceptPair))
		{
			List<String> list1 = queryFromConceptsForConcept(concept1);
			List<String> list2 = queryFromConceptsForConcept(concept2);
			int s1 = list1.size();
			int s2 = list2.size();
			List<String> commonSublist = CollectionUtils.commonSublist(list1, list2);
			int s = commonSublist.size();
			if (s <= 0)
			{
				return 1;
			}

			int smin = ((s1 > s2) ? s2 : s1);
			int smax = ((s1 > s2) ? s1 : s2);

			int W = NUM_ALL_CONCEPTS;
			double relatedness = (Math.log(smax) - Math.log(s)) / (Math.log(W) - Math.log(smin));
			cachedRelatedness.put(conceptPair, relatedness);
		}
		
		return cachedRelatedness.get(conceptPair);
	}

	public Map<String, Double> querySenses(String surface) throws SQLException
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT concept, commonness FROM commonness WHERE surface=?;");

		st.setString(1, surface);
		ResultSet rs = st.executeQuery();
		Map<String, Double> rst = new HashMap<String, Double>();
		while (rs.next())
		{
			rst.put(rs.getString("concept"), rs.getDouble("commonness"));
		}
		return rst;
	}

	public double queryCommonness(String surface, String concept) throws SQLException
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT commonness FROM commonness WHERE surface=? AND concept=?;");

		st.setString(1, surface);
		st.setString(2, concept);
		ResultSet rs = st.executeQuery();
		if (!rs.next())
			return 0;

		double commonness = rs.getDouble("commonness");

		if (rs.next())
		{
			String warning = String.format(
					"warning: duplicate commonness entries for surface '%s' and concept '%s' found.",
					surface, concept);
			System.err.println(warning);
		}
		return commonness;
	}
	/**
	 * return a ORDERED list of from_concept (source of a link) given a to_concept (destination of a link).
	 * 
	 * @param toConcept
	 * @return
	 * @throws SQLException
	 */
	public List<String> queryFromConceptsForConcept(String toConcept) throws SQLException
	{
		if (cachedFromConcepts.containsKey(toConcept))
			return cachedFromConcepts.get(toConcept);
		
		PreparedStatement st = da
				.getPreparedStatement("SELECT DISTINCT from_concept FROM inlinks WHERE to_concept=? ORDER BY from_concept;");

		st.setString(1, toConcept);
		ResultSet rs = st.executeQuery();
		List<String> rst = new ArrayList<String>();
		while (rs.next())
		{
			rst.add(rs.getString("from_concept"));
		}
		cachedFromConcepts.put(toConcept, rst);
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
