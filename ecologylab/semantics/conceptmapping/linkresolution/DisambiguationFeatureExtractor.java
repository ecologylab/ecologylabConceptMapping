package ecologylab.semantics.conceptmapping.linkresolution;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ecologylab.semantics.conceptmapping.linkresolution.DisambiguationFeatureExtractor.Context.Item;
import ecologylab.semantics.conceptmapping.database.DatabaseAdapter;

/**
 * This class extracts features for sense disambiguation, for each pair of (surface, concept).
 * 
 * Features include commonness (the prior probability), relatedness to surrounding text, and quality
 * of context. Commonness is 1D: commonness(surface, concept); Relatedness to surrounding text is
 * 1D: weighted sum of relatedness(concept, surronding concepts); Quality is 1D: sum of all the
 * weights.
 * 
 * The output is a probability of this surface being related to this concept.
 * 
 * @author quyin
 * 
 */
public class DisambiguationFeatureExtractor
{
	// constant: # of concepts we have in total
	public static final int	W	= 2465197;

	public class Context
	{
		public class Item
		{
			public String	surface;

			public String	concept;
		}

		public final String	contextConcept;

		public List<Item>		items	= new ArrayList<Item>();

		public Context(String concept) throws SQLException
		{
			this.contextConcept = concept;
			ResultSet rs = queryContext(contextConcept);
			while (rs.next())
			{
				Item it = new Item();
				it.surface = rs.getString("surface");
				it.concept = rs.getString("to_concept");
				items.add(it);
			}
		}
	}

	public static final double	w_kp	= 0.5;

	public static final double	w_ar	= 0.5;

	protected DatabaseAdapter			da		= DatabaseAdapter.get(this.getClass().getName());
	
	public DatabaseAdapter getDatabaseAdapter()
	{
		return da;
	}

	public List<DisambiguationInstance> processASurfaceOccurrence(String surface, Context C) throws SQLException
	{
		// debug
		System.out.format("processing surface '%s' in context '%s' of %d concepts...\n", surface,
				C.contextConcept, C.items.size());

		List<DisambiguationInstance> instances = new ArrayList<DisambiguationInstance>();

		List<Double> weights = new ArrayList<Double>();
		double contextQuality = 0;
		for (Context.Item it : C.items)
		{
			double kp = queryKeyphraseness(it.surface);
			double ar = getAverageRelatedness(it, C);
			double w = w_kp * kp + w_ar * ar;
			weights.add(w);
			contextQuality += w;
		}

		for (Context.Item item : C.items)
		{
			if (!item.surface.equals(surface))
				continue;

			for (String concept : querySenses(surface))
			{
				DisambiguationInstance instance = new DisambiguationInstance(surface, concept);
				instance.commonness = queryCommonness(surface, concept);
				instance.contextualRelatedness = 0;
				for (int i = 0; i < C.items.size(); ++i)
				{
					Context.Item it = C.items.get(i);
					double w = weights.get(i);
					instance.contextualRelatedness += w * queryRelatedness(concept, it.concept);
				}

				instance.contextQuality = contextQuality;

				instance.target = String.valueOf(item.concept.equals(concept));
				instances.add(instance);
			}
		}

		return instances;
	}

	protected double getAverageRelatedness(Item it, Context C) throws SQLException
	{
		double sumR = 0;
		for (Context.Item item : C.items)
		{
			double relatedness = queryRelatedness(it.concept, item.concept);
			sumR += relatedness;
		}
		return sumR / (C.items.size() - 1);
	}

	public void extract(String... ambiSurfaces)
	{
		for (String s : ambiSurfaces)
		{
			try
			{
				List<Context> contexts = getContexts(s);
				for (Context C : contexts)
				{
					try
					{
						List<DisambiguationInstance> insts = processASurfaceOccurrence(s, C);
						if (insts != null)
							storeInstances(insts.toArray(new DisambiguationInstance[0]));
					}
					catch (SQLException e)
					{
						System.err.println("cannot process surface " + s + " in context " + C + ": "
								+ e.getMessage());
					}
				}
			}
			catch (SQLException e)
			{
				System.err.println("cannot get contexts for surface " + s + ": " + e.getMessage());
			}
		}
	}

	protected void storeInstances(DisambiguationInstance... instances)
	{
		for (DisambiguationInstance inst : instances)
		{
			System.out.println(inst);
		}
	}

	protected List<Context> getContexts(String surface) throws SQLException
	{
		List<Context> contexts = new ArrayList<Context>();
		List<String> fromConcepts = queryFromConceptsForSurface(surface);
		for (String fc : fromConcepts)
		{
			Context C = new Context(fc);
			contexts.add(C);
		}
		return contexts;
	}

	protected List<String> querySenses(String surface) throws SQLException
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

	protected List<String> queryFromConceptsForSurface(String surface) throws SQLException
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

	protected ResultSet queryContext(String fromConcept) throws SQLException
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT DISTINCT to_concept, surface FROM inlinks WHERE from_concept=?;");

		st.setString(1, fromConcept);
		return st.executeQuery();
	}

	protected double queryCommonness(String surface, String concept) throws SQLException
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

	protected double queryKeyphraseness(String surface) throws SQLException
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

	protected double queryRelatedness(String concept1, String concept2) throws SQLException
	{
		if (concept1.equals(concept2))
			return 0;

		Set<String> set1 = queryFromConceptsForConcept(concept1);
		Set<String> set2 = queryFromConceptsForConcept(concept2);
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

		return (Math.log(smax) - Math.log(s)) / (Math.log(W) - Math.log(smin));
	}

	protected Set<String> queryFromConceptsForConcept(String toConcept) throws SQLException
	{
		PreparedStatement st = da
				.getPreparedStatement("SELECT DISTINCT from_concept FROM inlinks WHERE to_concept=?;");

		st.setString(1, toConcept);
		ResultSet rs = st.executeQuery();
		Set<String> rst = new HashSet<String>();
		while (rs.next())
		{
			rst.add(rs.getString("from_concept"));
		}
		return rst;
	}

	public static void main(String[] args)
	{
		DisambiguationFeatureExtractor dfe = new DisambiguationFeatureExtractor();
		dfe.extract("glass");
	}
}
