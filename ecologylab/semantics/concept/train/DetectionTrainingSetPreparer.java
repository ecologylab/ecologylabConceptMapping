package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.ConceptTrainingConstants;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.generated.library.GeneratedMetadataTranslationScope;
import ecologylab.semantics.metametadata.MetaMetadataRepository;

public class DetectionTrainingSetPreparer extends TrainingSetPreparer
{

	public DetectionTrainingSetPreparer(Context presetContext)
	{
		super(presetContext);
		try
		{
			out = new BufferedWriter(new FileWriter(
					ConceptTrainingConstants.DETECT_TRAINING_SET_FILE_PATH, true));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void detectConcepts()
	{
		try
		{
			for (Instance inst : instances)
			{
				String surface = inst.anchor.getSurface();
				String concept = inst.anchor.getConcept();

				out.write(String.format("%d,%f,%f,%f,%f,%f,%f # %s -> %s\n", presetContext.getAnchors()
						.contains(surface) ? ConceptConstants.POS_CLASS_INT_LABEL
						: ConceptConstants.NEG_CLASS_INT_LABEL, inst.keyphraseness, inst.contextualRelatedness,
						inst.disambiguationConfidence, inst.occurrence, inst.frequency, surface, concept));
			}
			out.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException
	{
		TrainingSetPreparer.phase = DETECTION_PHASE;
		TrainingSetPreparer.registerSemanticActions();
		File outf = new File(ConceptTrainingConstants.DETECT_TRAINING_SET_FILE_PATH);
		if (outf.exists())
		{
			System.err
					.println("training set data file already exists! if you want to regenerate it please delete the old one first.");
			System.exit(-1);
		}
		
		MetaMetadataRepository repo = MetaMetadataRepository.load(new File(
				ConceptConstants.METAMETADATA_REPOSITORY_LOCATION));
		WikiInfoCollectorForTraining ic = new WikiInfoCollectorForTraining(repo,
				GeneratedMetadataTranslationScope.get());
		ParsedURL purl = ParsedURL
				.getAbsolute("http://achilles.cse.tamu.edu/wiki/articles/c/h/i/Chicago.html");
		ic.getContainerDownloadIfNeeded(null, purl, null, false, false, false);
	}

}
