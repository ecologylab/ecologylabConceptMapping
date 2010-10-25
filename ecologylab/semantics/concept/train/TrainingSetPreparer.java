package ecologylab.semantics.concept.train;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.ConceptTrainingConstants;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.detect.TrieDict;

public abstract class TrainingSetPreparer
{

	public abstract void prepare(WikiDoc doc, BufferedWriter out);

	protected abstract void reportInstance(BufferedWriter out, WikiDoc doc, Instance instance,
			boolean isPositiveSample);

	public static void reportDisambiguationInstance(BufferedWriter out, WikiDoc doc,
			Instance instance, boolean isPositiveSample)
	{
		String line = String.format("%d,%f,%f,%f # %s:%s->%s",
						isPositiveSample ? 1 : -1,
						instance.commonness,
						instance.contextualRelatedness,
						instance.contextQuality,
						doc.getTitle(),
						instance.surface.word,
						instance.disambiguatedConcept.title
						);
		try
		{
			out.write(line);
			out.newLine();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void reportDetectionInstance(BufferedWriter out, WikiDoc doc, Instance instance,
			boolean isPositiveSample)
	{
		String line = String.format("%d,%f,%f,%f,%f,%f,%f,%f  # %s:%s->%s",
						isPositiveSample ? 1 : -1,
						instance.commonness,
						instance.contextualRelatedness,
						instance.contextQuality,
						instance.disambiguationConfidence,
						instance.keyphraseness,
						instance.occurrence,
						instance.frequency,
						doc.getTitle(),
						instance.surface.word,
						instance.disambiguatedConcept.title
						);
		try
		{
			out.write(line);
			out.newLine();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		// prepare output
		File outf = new File(ConceptTrainingConstants.DISAMBI_TRAINING_SET_FILE_PATH);
		if (outf.exists())
		{
			System.err.println("training set data file already exists at " + outf.getAbsolutePath());
			System.err.println("if you want to regenerate it please delete the old one first.");
			System.exit(-1);
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(outf));

		// read in title list for generating training set
		List<String> titleList = new ArrayList<String>();
		File inf = new File(ConceptTrainingConstants.TRAINSET_ARTICLE_LIST_FILE_PATH);
		if (!inf.exists())
		{
			System.err.println("training set article list file not found at " + inf.getAbsolutePath());
			System.exit(-1);
		}
		BufferedReader br = new BufferedReader(new FileReader(inf));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			titleList.add(line);
		}
		br.close();

		// prepare
		TrieDict dict = TrieDict.load(new File(ConceptConstants.DICTIONARY_PATH));
		TrainingSetPreparer tsp = new DisambiguationTrainingSetPreparer()
		{
			@Override
			public void reportInstance(BufferedWriter out, WikiDoc doc, Instance instance,
					boolean isPositiveSample)
			{
				reportDisambiguationInstance(out, doc, instance, isPositiveSample);
			}
		};

		for (String title : titleList)
		{
			WikiDoc doc = WikiDoc.get(title, dict);
			tsp.prepare(doc, out);
		}

		out.close();
	}

}
