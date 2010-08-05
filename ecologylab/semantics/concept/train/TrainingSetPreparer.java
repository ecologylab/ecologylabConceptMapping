package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.detect.DetectionFeatureExtractor;
import ecologylab.semantics.concept.detect.DetectionInstance;
import ecologylab.semantics.concept.detect.Detector;
import ecologylab.semantics.concept.detect.DisambiguationFeatureExtractor;
import ecologylab.semantics.concept.detect.DisambiguationInstance;
import ecologylab.semantics.concept.text.Context;
import ecologylab.semantics.concept.text.WikiAnchor;
import ecologylab.semantics.generated.library.GeneratedMetadataTranslationScope;
import ecologylab.semantics.metametadata.MetaMetadataRepository;

public class TrainingSetPreparer extends Detector
{
	
	private Context presetContext;

	public TrainingSetPreparer(Context presetContext) throws SQLException
	{
		this.presetContext = presetContext;
	}
	
	/**
	 * overridden to enlarge the context with the preset context.
	 */
	@Override
	protected void generateContext()
	{
		super.generateContext();
		context.addAll(presetContext);
	}
	
	/**
	 * overridden to generate a training set for disambiguation, and also prepare disambiguation
	 * features for use by concept detection feature extractor.
	 */
	@Override
	protected void disambiguate()
	{
		generateTrainingSetForDisambiguation();
		super.disambiguate();
	}

	/**
	 * generate and store disambiguation features.
	 */
	private void generateTrainingSetForDisambiguation()
	{
		DisambiguationFeatureExtractor dfe = new DisambiguationFeatureExtractor();
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter("data/disambiguation-training.dat"));
			for (WikiAnchor anchor : context.getAnchors())
			{
				String surface = anchor.surface;
				List<String> concepts;
				try
				{
					concepts = DatabaseUtils.get().querySenses(surface);
					if (concepts.size() <= 1)
						continue;
					
					for (String concept : concepts)
					{
						DisambiguationInstance inst = dfe.extract(context, surface, concept);
						bw.write(String.format("%d,%f,%f,%f # %s -> %s\n",
								concept.equals(anchor.concept) ? DisambiguationInstance.posClassIntLabel
										: DisambiguationInstance.negClassIntLabel, inst.commonness,
								inst.contextualRelatedness, inst.contextQuality, inst.surface, inst.concept));
					}
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			bw.close();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * overridden to generate a training set for concept detection.
	 */
	@Override
	protected void detectConcepts()
	{
		DetectionFeatureExtractor dfe = new DetectionFeatureExtractor();

		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter("data/detection-training.dat"));
			for (String surface : surfaces)
			{
				if (disambiguators.containsKey(surface))
				{
					try
					{
						assert ngGen.ngrams.containsKey(surface) : "n-gram not found: " + surface;
						DetectionInstance inst = dfe.extract(ngGen.totalWordCount, ngGen.ngrams.get(surface),
								context, disambiguators.get(surface));
						bw.write(String.format("%d,%f,%f,%f,%f,%f,%f # %s -> %s\n", context.getSurfaces()
								.contains(surface) ? DetectionInstance.posClassIntLabel
								: DetectionInstance.negClassIntLabel, inst.keyphraseness,
								inst.contextualRelatedness, inst.averageRelatedness, inst.disambiguationConfidence,
								inst.occurrence, inst.frequency, inst.surface, inst.concept));
					}
					catch (SQLException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			bw.close();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException
	{
		MetaMetadataRepository repo = MetaMetadataRepository.load(new File("../cf/config/semantics/metametadata"));
		WikiInfoCollectorForTraining ic = new WikiInfoCollectorForTraining(repo , GeneratedMetadataTranslationScope.get());
		ParsedURL purl = ParsedURL.getAbsolute("http://achilles.cse.tamu.edu/wiki/articles/c/h/i/Chicago.html");
		ic.getContainerDownloadIfNeeded(null, purl, null, false, false, false);
	}
	
}
