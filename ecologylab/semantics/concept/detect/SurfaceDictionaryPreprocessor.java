package ecologylab.semantics.concept.detect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.detect.SurfaceDictionary.SurfaceRecord;
import ecologylab.semantics.concept.utils.StopWordsUtils;

public class SurfaceDictionaryPreprocessor
{

	public static void preprocess(File src, File dest) throws IOException
	{
		List<SurfaceRecord> surfaces = new ArrayList<SurfaceRecord>();
		BufferedReader br = new BufferedReader(new FileReader(src));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			SurfaceRecord sr = SurfaceRecord.get(line);
			if (sr != null && StopWordsUtils.containsLetter(sr.surface) && !StopWordsUtils.isStopWord(sr.surface))
			{
				surfaces.add(sr);
			}
			else
			{
				System.err.println("ignoring line: " + line);
			}
		}
		br.close();

		Collections.sort(surfaces);

		BufferedWriter bw = new BufferedWriter(new FileWriter(dest));
		for (int i = 0; i < surfaces.size(); ++i)
		{
			SurfaceRecord sr = surfaces.get(i);
			bw.write(sr.toString());
			bw.newLine();
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException
	{
		File src = new File(ConceptConstants.DICTIONARY_PATH);
		File dest = new File(ConceptConstants.DICTIONARY_PATH + ".new");
		preprocess(src, dest);
	}

}
