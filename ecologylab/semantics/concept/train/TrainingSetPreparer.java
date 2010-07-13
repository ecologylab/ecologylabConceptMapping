package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import ecologylab.semantics.concept.detect.DetectionFeatureExtractor;
import ecologylab.semantics.concept.detect.Detector;
import ecologylab.semantics.concept.detect.DisambiguationFeatureExtractor;
import ecologylab.semantics.concept.detect.DisambiguationInstance;
import ecologylab.semantics.concept.text.WikiAnchor;
import ecologylab.semantics.concept.text.WikiNGramGenerator;

public class TrainingSetPreparer extends Detector
{

	protected WikiNGramGenerator							wikiNgGen;

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
		for (WikiAnchor anchor : wikiNgGen.anchors.values())
		{
			if (!context.containsKey(anchor.concept))
			{
				context.put(anchor.concept, anchor);
			}
		}
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
			for (WikiAnchor anchor : wikiNgGen.anchors.values())
			{
				String surface = anchor.surface;
				List<String> concepts = dbUtils.querySenses(surface);
				for (String concept : concepts)
				{
					DisambiguationInstance inst = dfe.extract(wikiNgGen.anchors, surface, concept);
					bw.write(String.format("%d,%f,%f,%f # %s -> %s\n",
							concept.equals(anchor.concept) ? DisambiguationInstance.posClassIntLabel : DisambiguationInstance.negClassIntLabel,
							inst.commonness,
							inst.contextualRelatedness,
							inst.contextQuality,
							inst.surface,
							inst.concept));
				}
			}
			bw.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void detect()
	{
		// TODO
	}

}
