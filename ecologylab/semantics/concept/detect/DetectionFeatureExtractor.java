package ecologylab.semantics.concept.detect;

import java.sql.SQLException;
import java.util.List;

import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.text.Gram;
import ecologylab.semantics.concept.text.WikiAnchor;

public class DetectionFeatureExtractor
{

	protected DatabaseUtils	dbUtils	= new DatabaseUtils();

	public DetectionInstance extract(int totalWordCount, Gram gram, List<WikiAnchor> context,
			Disambiguator disambiguator) throws SQLException
	{
		String concept = disambiguator.disambiguatedConcept;
		DetectionInstance instance = new DetectionInstance(gram.text, concept);

		instance.keyphraseness = dbUtils.queryKeyphraseness(gram.text);
		instance.contextualRelatedness = disambiguator.disambiguatedInstance.contextualRelatedness;
		instance.averageRelatedness = DisambiguationFeatureExtractor.getAverageRelatedness(dbUtils,
				concept, context);
		instance.dismabiguationConfidence = disambiguator.confidence;
		instance.occurrence = gram.count;
		instance.frequency = (double) gram.count / totalWordCount;

		return instance;
	}
}
