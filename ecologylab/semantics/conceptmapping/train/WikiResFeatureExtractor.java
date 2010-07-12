package ecologylab.semantics.conceptmapping.train;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ecologylab.semantics.conceptmapping.linkresolution.Context;
import ecologylab.semantics.conceptmapping.train.WikiResFeatureExtractor.Context.Item;
import ecologylab.semantics.conceptmapping.conceptdetection.DisambiguationInstance;
import ecologylab.semantics.conceptmapping.database.DatabaseAdapter;
import ecologylab.semantics.conceptmapping.database.DatabaseUtils;

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
public class WikiResFeatureExtractor
{

	public static final double	w_kp	= 0.5;

	public static final double	w_ar	= 0.5;

	private DatabaseAdapter			dbAdapter;

	protected DatabaseUtils			dbUtils;

	public WikiResFeatureExtractor(Context context, String... ambiSurfaces)
	{
		dbAdapter = DatabaseAdapter.get(this.getClass().getName());
		dbUtils = DatabaseUtils.get(dbAdapter);

		extract(context, ambiSurfaces);
	}

	protected void extract(Context context, String... ambiSurfaces)
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

	protected List<Context> getContexts(String surface) throws SQLException
	{
		List<Context> contexts = new ArrayList<Context>();
		List<String> fromConcepts = dbUtils.queryFromConceptsForSurface(surface);
		for (String fc : fromConcepts)
		{
			Context C = new Context(fc);
			contexts.add(C);
		}
		return contexts;
	}

	protected List<DisambiguationInstance> processASurfaceOccurrence(String surface, Context C)
			throws SQLException
	{
		// debug
		System.out.format("processing surface '%s' in context '%s' of %d concepts...\n", surface,
				C.contextConcept, C.items.size());

		List<DisambiguationInstance> instances = new ArrayList<DisambiguationInstance>();

		List<Double> weights = new ArrayList<Double>();
		double contextQuality = 0;
		for (Context.Item it : C.items)
		{
			double kp = dbUtils.queryKeyphraseness(it.surface);
			double ar = getAverageRelatedness(it, C);
			double w = w_kp * kp + w_ar * ar;
			weights.add(w);
			contextQuality += w;
		}

		for (Context.Item item : C.items)
		{
			if (!item.surface.equals(surface))
				continue;

			for (String concept : dbUtils.querySenses(surface))
			{
				DisambiguationInstance instance = new DisambiguationInstance(surface, concept);
				instance.commonness = dbUtils.queryCommonness(surface, concept);
				instance.contextualRelatedness = 0;
				for (int i = 0; i < C.items.size(); ++i)
				{
					Context.Item it = C.items.get(i);
					double w = weights.get(i);
					instance.contextualRelatedness += w * dbUtils.queryRelatedness(concept, it.concept);
				}

				instance.contextQuality = contextQuality;

				instance.target = String.valueOf(item.concept.equals(concept));
				instances.add(instance);
			}
		}

		return instances;
	}

	private double getAverageRelatedness(Item it, Context C) throws SQLException
	{
		double sumR = 0;
		for (Context.Item item : C.items)
		{
			double relatedness = dbUtils.queryRelatedness(it.concept, item.concept);
			sumR += relatedness;
		}
		return sumR / (C.items.size() - 1);
	}

	protected void storeInstances(DisambiguationInstance... instances)
	{
		for (DisambiguationInstance inst : instances)
		{
			System.out.println(inst);
		}
	}

	public Context getContext(String concept) throws SQLException
	{
		Context = this.contextConcept = concept;

		PreparedStatement st = dbAdapter
				.getPreparedStatement("SELECT DISTINCT to_concept, surface FROM inlinks WHERE from_concept=?;");
		st.setString(1, concept);
		ResultSet rs = st.executeQuery();
		while (rs.next())
		{
			Item it = new Item();
			it.text = rs.getString("surface");
			it.concept = rs.getString("to_concept");
			items.add(it);
		}
	}

	public static void main(String[] args)
	{
		WikiResFeatureExtractor dfe = new WikiResFeatureExtractor("glass");
	}
}
