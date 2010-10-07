package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.utils.TextUtils;

public class UnescapeAndSortPrimaryConcepts extends Debug
{

	private static final String	originalPrimaryConceptListFilePath	= "data/primary-concepts-original.lst";

	private static final String	newPrimaryConceptListFilePath				= "data/primary-concepts.lst";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		System.out.println("loading original list ...");
		List<String> primaryConceptList = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(originalPrimaryConceptListFilePath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String concept = TextUtils.urlDecode(line.trim());
			primaryConceptList.add(concept.replaceAll("_", " "));
		}
		br.close();

		System.out.println("sorting ...");
		Collections.sort(primaryConceptList);

		System.out.println("saving ...");
		BufferedWriter bw = new BufferedWriter(new FileWriter(newPrimaryConceptListFilePath));
		for (String concept : primaryConceptList)
		{
			bw.write(concept);
			bw.newLine();
		}
		bw.close();

		System.out.println("done.");
	}

}
