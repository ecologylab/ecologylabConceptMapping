package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.ConceptTrainingConstants;
import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.DetectionFeatureExtractor;
import ecologylab.semantics.concept.detect.DetectionInstance;
import ecologylab.semantics.concept.detect.Detector;
import ecologylab.semantics.concept.detect.FeatureExtractor;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.text.ConceptAnchor;
import ecologylab.semantics.generated.library.GeneratedMetadataTranslationScope;
import ecologylab.semantics.metametadata.MetaMetadataRepository;

public class DetectorForTraining extends Detector
{

	private Context	presetContext;

	public DetectorForTraining(Context presetContext)
	{
		this.presetContext = presetContext;
	}

	/**
	 * overridden to enlarge the context with the preset context.
	 */
	@Override
	protected void findSurfacesAndGenerateContext()
	{
		super.findSurfacesAndGenerateContext();
		context.addAll(presetContext);
		for (ConceptAnchor anchor : presetContext.getAnchors())
		{
			if (ambiSurfacesAndSenses.containsKey(anchor.getSurface()))
			{
				ambiSurfacesAndSenses.remove(anchor.getSurface());
			}
		}
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
		FeatureExtractor dfe = new FeatureExtractor(context);
		BufferedWriter bw;
		try
		{
			bw = new BufferedWriter(new FileWriter(
					ConceptTrainingConstants.DISAMBI_TRAINING_SET_FILE_PATH));
			for (ConceptAnchor anchor : context.getAnchors())
			{
				String surface = anchor.getSurface();
				Map<String, Double> concepts = DatabaseUtils.get().querySenses(surface);
				for (String concept : concepts.keySet())
				{
					Instance inst = dfe.extract(surface, concept, concepts.get(concept));
					bw.write(String.format("%d,%f,%f,%f # %s -> %s\n",
							concept.equals(anchor.getConcept()) ? ConceptConstants.POS_CLASS_INT_LABEL
									: ConceptConstants.NEG_CLASS_INT_LABEL, inst.commonness,
							inst.contextualRelatedness, inst.contextQuality, inst.surface, inst.concept));
				}
			}
			bw.close();
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
		DetectionFeatureExtractor dfe = new DetectionFeatureExtractor();

		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(ConceptTrainingConstants.DETECT_TRAINING_SET_FILE_PATH));
			for (String surface : ambiSurfacesAndSenses.keySet())
			{
				Instance disambiInst = cachedDisambiguationInstances.get(surface);
						DetectionInstance inst = dfe.extract(ngGen.totalWordCount, ngGen.ngrams.get(surface), disambiInst);
						bw.write(String.format("%d,%f,%f,%f,%f,%f,%f # %s -> %s\n", context.getSurfaces()
								.contains(surface) ? DetectionInstance.posClassIntLabel
								: DetectionInstance.negClassIntLabel, inst.keyphraseness,
								inst.contextualRelatedness, inst.averageRelatedness, inst.disambiguationConfidence,
								inst.occurrence, inst.frequency, inst.surface, inst.concept));
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
		MetaMetadataRepository repo = MetaMetadataRepository.load(new File(
				"../cf/config/semantics/metametadata"));
		WikiInfoCollectorForTraining ic = new WikiInfoCollectorForTraining(repo,
				GeneratedMetadataTranslationScope.get());
		ParsedURL purl = ParsedURL
				.getAbsolute("http://achilles.cse.tamu.edu/wiki/articles/c/h/i/Chicago.html");
		ic.getContainerDownloadIfNeeded(null, purl, null, false, false, false);
	}

}
