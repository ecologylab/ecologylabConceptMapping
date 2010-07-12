package ecologylab.semantics.conceptmapping.conceptdetection;

import java.sql.SQLException;
import java.util.List;

import ecologylab.semantics.conceptmapping.database.DatabaseUtils;
import ecologylab.semantics.conceptmapping.text.Gram;
import ecologylab.semantics.conceptmapping.text.WikiAnchor;

public class DetectionFeatureExtractor
{

	protected DatabaseUtils										dbUtils	= new DatabaseUtils();

	protected DisambiguationFeatureExtractor	lfe			= new DisambiguationFeatureExtractor();

	public DetectionInstance extract(List<WikiAnchor> context, Gram gram) throws SQLException
	{
		List<String> concepts = dbUtils.querySenses(gram.text);

		DetectionInstance bestInstance = null;
		double bestConfidence = Double.NEGATIVE_INFINITY;
		for (String concept : concepts)
		{
			DetectionInstance instance = new DetectionInstance(gram.text, concept);
			DisambiguationInstance resInst = lfe.extract(context, gram.text, concept);
			instance.keyphraseness = dbUtils.queryKeyphraseness(gram.text);
			instance.averageRelatedness = lfe.getAverageRelatedness(concept, context);
			instance.contextualRelatedness = resInst.contextualRelatedness;
			// instance.dismabiguationConfidence = ;
			instance.occurrence = 0;
			instance.frequency = 0;

			if (instance.dismabiguationConfidence > bestConfidence)
			{
				bestConfidence = instance.dismabiguationConfidence;
				bestInstance = instance;
			}
		}

		return bestInstance;
	}
}
