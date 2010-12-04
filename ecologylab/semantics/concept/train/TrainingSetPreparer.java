package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.detect.SurfaceDictionary;

public abstract class TrainingSetPreparer
{

	public abstract void prepare(WikiDoc doc, BufferedWriter out);

	protected abstract void reportInstance(BufferedWriter out, WikiDoc doc, Instance instance,
			boolean isPositiveSample);

	public static void prepare(String wikiDocTitle, String outputDir, TrainingSetPreparer preparer)
			throws IOException, SQLException
	{
		SurfaceDictionary dict = SurfaceDictionary.get(ConceptConstants.DICTIONARY_PATH);

		long t0 = System.currentTimeMillis();

		long t1 = System.currentTimeMillis();
		WikiDoc doc = WikiDoc.get(wikiDocTitle, dict);
		long d1 = System.currentTimeMillis() - t1;

		String fileName = wikiDocTitle.replaceAll("[^0-9A-Za-z_]", "_") + ".result";
		File dir = new File(outputDir);
		File f = new File(dir.getPath() + File.separator + fileName);
		if (f.exists())
		{
			System.err.println("file already exists: " + fileName);
			return;
		}
		
		StringWriter str = new StringWriter();
		BufferedWriter out = new BufferedWriter(str);
		out.write(String.format("# start %s...", wikiDocTitle));
		out.newLine();

		long t2 = System.currentTimeMillis();
		if (doc != null)
		{
			try
			{
				preparer.prepare(doc, out);
				doc.recycle();
			}
			catch (Exception e)
			{
				System.err.println("EXCEPTION for wiki-doc " + wikiDocTitle + ": " + e.getMessage());
			}
		}
		else
		{
			System.out.println("warning: wikidoc not exist for " + wikiDocTitle);
		}
		long d2 = System.currentTimeMillis() - t2;

		long d0 = System.currentTimeMillis() - t0;
		
		out.write(String.format("# finished. time: %d = %d + %d", d0, d1, d2));
		out.newLine();
		out.close();
		String v = str.toString();
		
		BufferedWriter fout = new BufferedWriter(new FileWriter(f));
		fout.write(v);
		fout.newLine();
		fout.close();
	}

}
