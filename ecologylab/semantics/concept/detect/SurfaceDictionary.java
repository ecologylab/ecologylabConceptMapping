package ecologylab.semantics.concept.detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.utils.StopWordsUtils;
import ecologylab.semantics.concept.utils.TextUtils;

public class SurfaceDictionary extends Debug
{

	private int						longestInWord			= 0;

	private List<String>	surfaces					= new ArrayList<String>();

	private List<String>	ambiguousSurfaces	= new ArrayList<String>();

	private SurfaceDictionary()
	{

	}

	public static SurfaceDictionary load(File dictionary) throws IOException
	{
		SurfaceDictionary dict = new SurfaceDictionary();

		BufferedReader br = new BufferedReader(new FileReader(dictionary));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] parts = line.trim().split("\t");

			String surface = parts[0];
			if (StopWordsUtils.isStopWord(surface))
				continue;
			
			dict.surfaces.add(surface);
			int len = TextUtils.count(surface, " ") + 1;
			if (len > dict.longestInWord)
				dict.longestInWord = len;

			int countSenses = Integer.parseInt(parts[1]);
			if (countSenses > 1)
			{
				dict.ambiguousSurfaces.add(surface);
			}
		}
		br.close();

		Collections.sort(dict.surfaces);
		Collections.sort(dict.ambiguousSurfaces);
		return dict;
	}

	public String longestMatch(String text, int offset)
	{
		String candidate = TextUtils.getWords(text, offset, longestInWord);
		while (true)
		{
			if (Collections.binarySearch(surfaces, candidate) >= 0)
				return candidate;
			int p = candidate.lastIndexOf(' ');
			if (p < 0)
				break;
			else
				candidate = candidate.substring(0, p);
		}
		return null;
	}

	public boolean isAmbiguous(String surface)
	{
		return Collections.binarySearch(ambiguousSurfaces, surface) >= 0;
	}
	
	public List<String> getAll()
	{
		return surfaces;
	}

	public Set<String> extractSurfaces(String text)
	{
		Set<String> surfaces = new HashSet<String>();
		
		int offset = 0;
		while (offset < text.length())
		{
			String match = longestMatch(text, offset);
			if (match != null)
			{
				surfaces.add(match);
			}
			offset = TextUtils.nextWhitespaceIndex(text, offset);
			offset = TextUtils.nextNonWhitespaceIndex(text, offset);
		}
		
		return surfaces;
	}

	public static void main(String[] args) throws IOException
	{
		SurfaceDictionary dict = SurfaceDictionary.load(new File(ConceptConstants.DICTIONARY_PATH));
		String testString = "united states 2000 census is famous";
		System.out.println(dict.longestMatch(testString, 0));
	}

}
