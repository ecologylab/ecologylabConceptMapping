package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import ecologylab.semantics.concept.detect.DetectionFeatureExtractor;
import ecologylab.semantics.concept.detect.DetectionInstance;
import ecologylab.semantics.concept.detect.Detector;
import ecologylab.semantics.concept.detect.DisambiguationFeatureExtractor;
import ecologylab.semantics.concept.detect.DisambiguationInstance;
import ecologylab.semantics.concept.text.WikiAnchor;
import ecologylab.semantics.concept.text.WikiNGramGenerator;

public class TrainingSetPreparer extends Detector
{

	protected WikiNGramGenerator	wikiNgGen;

	public TrainingSetPreparer(String wikiHtmlText) throws SQLException
	{
		super(wikiHtmlText);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void generateNGrams(String text)
	{
		ngGen = new WikiNGramGenerator(text);
		wikiNgGen = ((WikiNGramGenerator) ngGen);
	}

	@Override
	protected void generateContext()
	{
		super.generateContext();
		context.addAll(wikiNgGen.context);
	}

	@Override
	protected void disambiguate()
	{
		generateTrainingSetForDisambiguation();
		super.disambiguate();
	}

	private void generateTrainingSetForDisambiguation()
	{
		DisambiguationFeatureExtractor dfe = new DisambiguationFeatureExtractor();
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter("data/disambiguation-training.dat"));
			for (WikiAnchor anchor : wikiNgGen.context.getAnchors())
			{
				String surface = anchor.surface;
				List<String> concepts;
				try
				{
					concepts = dbUtils.querySenses(surface);
					for (String concept : concepts)
					{
						DisambiguationInstance inst = dfe.extract(wikiNgGen.context, surface, concept);
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

	@Override
	protected void detect()
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
								inst.contextualRelatedness, inst.averageRelatedness, inst.dismabiguationConfidence,
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

}
