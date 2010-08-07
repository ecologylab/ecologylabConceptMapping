package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.ConceptTrainingConstants;
import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.text.ConceptAnchor;
import ecologylab.semantics.generated.library.GeneratedMetadataTranslationScope;
import ecologylab.semantics.metametadata.MetaMetadataRepository;

public class DisambiguationTrainingSetPreparer extends TrainingSetPreparer
{

	public DisambiguationTrainingSetPreparer(Context presetContext)
	{
		super(presetContext);
		try
		{
			out = new BufferedWriter(new FileWriter(
					ConceptTrainingConstants.DISAMBI_TRAINING_SET_FILE_PATH));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * overridden to generate a training set for disambiguation, and also prepare disambiguation
	 * features for use by concept detection feature extractor.
	 */
	@Override
	protected void disambiguateAndGenerateInstances()
	{
		try
		{
			for (ConceptAnchor anchor : context.getAnchors())
			{
				String surface = anchor.getSurface();
				Map<String, Double> concepts = DatabaseUtils.get().querySenses(surface);
				if (concepts.size() > 1)
				{
					for (String concept : concepts.keySet())
					{
						Instance inst = featureExtractor.extract(surface, concept, concepts.get(concept),
								nGramGenerator.totalWordCount, nGramGenerator.ngrams.get(surface).count);
						out.write(String.format("%d,%f,%f,%f # %s -> %s\n",
								concept.equals(anchor.getConcept()) ? ConceptConstants.POS_CLASS_INT_LABEL
										: ConceptConstants.NEG_CLASS_INT_LABEL, inst.commonness,
								inst.contextualRelatedness, inst.contextQuality, surface, concept));
					}
				}
			}
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * overridden to generate a training set for concept detection.
	 */
	@Override
	protected void detectConcepts()
	{
		// nothing to do
	}

	public static void main(String[] args) throws IOException
	{
		TrainingSetPreparer.phase = DISAMBIGUTION_PHASE;
		MetaMetadataRepository repo = MetaMetadataRepository.load(new File(
				ConceptConstants.METAMETADATA_REPOSITORY_LOCATION));
		WikiInfoCollectorForTraining ic = new WikiInfoCollectorForTraining(repo,
				GeneratedMetadataTranslationScope.get());
		ParsedURL purl = ParsedURL
				.getAbsolute("http://achilles.cse.tamu.edu/wiki/articles/c/h/i/Chicago.html");
		ic.getContainerDownloadIfNeeded(null, purl, null, false, false, false);
	}
}
