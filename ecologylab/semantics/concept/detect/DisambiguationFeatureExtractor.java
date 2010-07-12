package ecologylab.semantics.concept.detect;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.text.WikiAnchor;

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

	public static final double	w_kp		= 0.5;

	public static final double	w_ar		= 0.5;

	protected DatabaseUtils			dbUtils	= new DatabaseUtils();

	public DisambiguationInstance extract(List<WikiAnchor> context, String surface, String concept)
			throws SQLException
	{
		DisambiguationInstance instance = new DisambiguationInstance(surface, concept);

		List<Double> weights = new ArrayList<Double>();
		instance.contextQuality = 0;
		for (WikiAnchor anchor : context)
		{
			if (anchor.text.equals(surface))
				continue;

			double kp = dbUtils.queryKeyphraseness(anchor.text);
			double ar = getAverageRelatedness(dbUtils, anchor.concept, context);
			double w = w_kp * kp + w_ar * ar;
			weights.add(w);
			instance.contextQuality += w;
		}

		instance.contextualRelatedness = 0;
		for (int i = 0; i < context.size(); ++i)
		{
			WikiAnchor anchor = context.get(i);
			if (anchor.text.equals(surface))
				continue;

			instance.commonness = dbUtils.queryCommonness(surface, concept);
			double w = weights.get(i);
			instance.contextualRelatedness += w * dbUtils.queryRelatedness(concept, anchor.concept);
		}

		return instance;
	}

	public static double getAverageRelatedness(DatabaseUtils dbUtils, String concept,
			List<WikiAnchor> context) throws SQLException
	{
		double sumR = 0;
		for (WikiAnchor anchor : context)
		{
			double relatedness = dbUtils.queryRelatedness(anchor.concept, concept);
			sumR += relatedness;
		}
		return sumR / context.size();
	}

}
