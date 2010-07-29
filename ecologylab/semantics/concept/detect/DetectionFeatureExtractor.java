package ecologylab.semantics.concept.detect;

import java.sql.SQLException;

import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.text.Context;
import ecologylab.semantics.concept.text.Gram;

public class DetectionFeatureExtractor
{

	public DetectionInstance extract(int totalWordCount, Gram gram, Context context,
			Disambiguator disambiguator) throws SQLException
	{
		String concept = disambiguator.disambiguatedConcept;
		DetectionInstance instance = new DetectionInstance(gram.text, concept);

		instance.keyphraseness = DatabaseUtils.get().queryKeyphraseness(gram.text);
		instance.contextualRelatedness = disambiguator.disambiguatedInstance.contextualRelatedness;
		instance.averageRelatedness = DisambiguationFeatureExtractor.getAverageRelatedness(concept, context);
		instance.disambiguationConfidence = disambiguator.confidence;
		instance.occurrence = gram.count;
		instance.frequency = (double) gram.count / totalWordCount;

		return instance;
	}
}
