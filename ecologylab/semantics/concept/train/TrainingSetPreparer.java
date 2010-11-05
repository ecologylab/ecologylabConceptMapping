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
import ecologylab.semantics.concept.detect.SurfaceDictionary;

public abstract class TrainingSetPreparer
{

	public abstract void prepare(WikiDoc doc, BufferedWriter out);

	protected abstract void reportInstance(BufferedWriter out, WikiDoc doc, Instance instance,
			boolean isPositiveSample);

	public static void prepare(String trainSetFilePath, TrainingSetPreparer preparer)
			throws IOException, SQLException
	{
		// prepare output
		File outf = new File(trainSetFilePath);
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
		int i = 0;
		long t0 = System.currentTimeMillis();
		SurfaceDictionary dict = SurfaceDictionary.load(new File(ConceptConstants.DICTIONARY_PATH));
		for (String title : titleList)
		{
			long t1 = System.currentTimeMillis();
			WikiDoc doc = WikiDoc.get(title, dict);
			if (doc != null)
			{
				preparer.prepare(doc, out);
				doc.recycle();
			}
			else
			{
				System.out.println("warning: wikidoc not exist for " + title);
			}
			System.out.println("doc time: " + (System.currentTimeMillis() - t1));
			
			i++;
			if (i % 10 == 0)
			{
				long dt = System.currentTimeMillis() - t0;
				System.out.println(i + " of " + titleList.size() + " wiki articles processed: " + dt + " ms.");
			}
		}

		out.close();
	}

}
