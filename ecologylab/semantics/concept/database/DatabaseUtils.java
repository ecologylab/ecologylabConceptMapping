package ecologylab.semantics.concept.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ecologylab.semantics.concept.database.DatabaseAdapter;

public class DatabaseUtils
{
	
	private DatabaseAdapter	da;

	public DatabaseUtils()
	{
		this.da = DatabaseAdapter.get();
	}
	
	private List<String>	surfaces	= null;

	public List<String> querySurfaces()
	{
		if (surfaces != null)
			return surfaces;

		surfaces = new ArrayList<String>();
		try
		{
			ResultSet rs = da.executeQuerySql("SELECT surface FROM surfaces;");
			while (rs.next())
			{
				String surface = rs.getString("surface");
				surfaces.add(surface);
			}
		}
		catch (SQLException e)
		{
			System.err.println("cannot query the database: " + e.getMessage());
		}
		return surfaces;
	}

	private List<String>	concepts	= null;

	public List<String> queryConcepts()
	{
		if (concepts != null)
			return concepts;

		concepts = new ArrayList<String>();
		try
		{
			ResultSet rs = da.executeQuerySql("SELECT DISTINCT from_concept FROM inlinks;");
			while (rs.next())
			{
				String concept = rs.getString("from_concept");
				concepts.add(concept);
			}
		}
		catch (SQLException e)
		{
			System.err.println("cannot query the database: " + e.getMessage());
		}
		return concepts;
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

		Set<String> set1 = new HashSet<String>(queryFromConceptsForConcept(concept1));
		Set<String> set2 = new HashSet<String>(queryFromConceptsForConcept(concept2));
		int s1 = set1.size();
		int s2 = set2.size();
		set1.retainAll(set2);
		int s = set1.size();
		if (s <= 0)
		{
			return 1;
		}

		int smin = ((s1 > s2) ? s2 : s1);
		int smax = ((s1 > s2) ? s1 : s2);

		int W = queryConcepts().size();
		return (Math.log(smax) - Math.log(s)) / (Math.log(W) - Math.log(smin));
	}

	public List<String> querySenses(String surface) throws SQLException
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT concept FROM commonness WHERE surface=?");

		st.setString(1, surface);
		ResultSet rs = st.executeQuery();
		List<String> rst = new ArrayList<String>();
		while (rs.next())
		{
			rst.add(rs.getString("concept"));
		}
		return rst;
	}

	public List<String> queryFromConceptsForConcept(String toConcept) throws SQLException
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT DISTINCT from_concept FROM inlinks WHERE to_concept=?;");

		st.setString(1, toConcept);
		ResultSet rs = st.executeQuery();
		List<String> rst = new ArrayList<String>();
		while (rs.next())
		{
			rst.add(rs.getString("from_concept"));
		}
		return rst;
	}

	public List<String> queryFromConceptsForSurface(String surface) throws SQLException
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT DISTINCT from_concept FROM inlinks WHERE surface=?;");

		st.setString(1, surface);
		ResultSet rs = st.executeQuery();
		List<String> rst = new ArrayList<String>();
		while (rs.next())
		{
			rst.add(rs.getString("from_concept"));
		}
		return rst;
	}

}
