package ecologylab.semantics.concept.train;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.ConceptTrainingConstants;
import ecologylab.semantics.concept.detect.Concept;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.Doc;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.detect.Surface;
import ecologylab.semantics.concept.detect.TrieDict;
import ecologylab.semantics.generated.library.GeneratedMetadataTranslationScope;
import ecologylab.semantics.metametadata.MetaMetadataRepository;

public class DetectionTrainingSetPreparer extends TrainingSetPreparer
{

	public DetectionTrainingSetPreparer(TrieDict dict) throws IOException, SQLException
	{
		super(dict);
	}

	/**
	 * find all ambiguous surfaces. for each one, disambiguate it in the context consisting of
	 * unambiguous surfaces & linked concepts. linked ones are treated as positive samples, while
	 * unlinked ones negative. 
	 * 
	 * @param doc
	 * @param linkedConcepts
	 * @throws SQLException
	 */
	@Override
	protected void prepare(Doc doc, Map<Concept, Surface> linkedConcepts) throws SQLException
	{
	}

	/*
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

		BufferedReader in = new BufferedReader(new FileReader("data/trainset-url.lst"));
		String line = null;
		while ((line = in.readLine()) != null)
		{
			ParsedURL purl = ParsedURL.getAbsolute(line);
			ic.getContainerDownloadIfNeeded(null, purl, null, false, false, false);
		}
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ic.getDownloadMonitor().stop();
	}
	*/

}
