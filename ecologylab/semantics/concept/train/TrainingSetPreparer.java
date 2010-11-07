package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.detect.SurfaceDictionary;
import ecologylab.semantics.concept.utils.TextUtils;

public abstract class TrainingSetPreparer
{

	public abstract void prepare(WikiDoc doc, BufferedWriter out);

	protected abstract void reportInstance(BufferedWriter out, WikiDoc doc, Instance instance,
			boolean isPositiveSample);

	public static void prepare(File titleListFile, File resultTrainSet, TrainingSetPreparer preparer)
			throws IOException, SQLException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(resultTrainSet));
		
		List<String> titleList = TextUtils.loadTxtAsList(titleListFile, false);
		int i = 0;
		long t0 = System.currentTimeMillis();
		SurfaceDictionary dict = SurfaceDictionary.load(new File(ConceptConstants.DICTIONARY_PATH));
		for (String title : titleList)
		{
			System.out.format("doc [%s]: ", title);
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
			System.out.println(System.currentTimeMillis() - t1);
			
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
