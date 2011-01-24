package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.detect.SurfaceDictionary;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.TextUtils;

public abstract class TrainingSetPreparer
{
	
	private static SurfaceDictionary dict;
	
	static
	{
		try
		{
			dict = SurfaceDictionary.load(ConceptConstants.DICTIONARY_PATH);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	abstract public void prepare(WikiDoc doc, BufferedWriter out);

	abstract protected void reportInstance(BufferedWriter out, WikiDoc doc, Instance instance, boolean isPositiveSample);

	public static void prepare(String wikiDocTitle, String outputDir, TrainingSetPreparer preparer)
			throws IOException, SQLException
	{
		String fileName = wikiDocTitle.replaceAll("[^0-9A-Za-z_]", "_") + ".result";
		File dir = new File(outputDir);
		File f = new File(dir.getPath() + File.separator + fileName);
		if (f.exists())
		{
			System.err.println("file already exists: " + fileName);
			return;
		}
		
		long t0 = System.currentTimeMillis();

		long t1 = System.currentTimeMillis();
		WikiDoc doc = WikiDoc.get(wikiDocTitle, dict);
		long d1 = System.currentTimeMillis() - t1;

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
	
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		if (args.length != 2)
		{
			System.err.println("args: <wiki-article-title-list> <output-dir-path>");
			System.exit(-1);
		}
		
		String titleListFile = args[0];
		String outputDir = args[1];
		String preparerClassName = Configs.getString("train.trainset_preparer");
		
		Class<?> preparerClass = Class.forName(preparerClassName);
		TrainingSetPreparer preparer = (TrainingSetPreparer) preparerClass.newInstance();
		List<String> titles = TextUtils.loadTxtAsList(new File(titleListFile), false);
		for (String title : titles)
		{
			prepare(title, outputDir, preparer);
		}
	}

}
