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
import ecologylab.semantics.concept.utils.TextUtils;

public class SurfaceDictionaryPreprocessor
{

	public static int preprocess(File src, File dest) throws IOException
	{
		int longestInWord = 0;

		List<SurfaceRecord> surfaces = new ArrayList<SurfaceRecord>();
		BufferedReader br = new BufferedReader(new FileReader(src));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			SurfaceRecord sr = SurfaceRecord.get(line);
			if (sr != null)
			{
				surfaces.add(sr);
				int len = TextUtils.count(sr.surface, " ") + 1;
				if (len > longestInWord)
					longestInWord = len;
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

		return longestInWord;
	}

	public static void main(String[] args) throws IOException
	{
		File src = new File(ConceptConstants.DICTIONARY_PATH);
		File dest = new File(ConceptConstants.DICTIONARY_PATH + ".new");
		int n = preprocess(src, dest);
		System.out.println(n);
	}

}
