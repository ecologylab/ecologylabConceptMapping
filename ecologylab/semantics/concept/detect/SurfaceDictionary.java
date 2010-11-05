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

	public static final String	DELIM_SEQ		= "|";

	public static final String	DELIM_REGEX	= "\\|";

	static class SurfaceRecord implements Comparable<SurfaceRecord>
	{
		public String	surface;

		public int		senseCount;

		public static SurfaceRecord get(String line)
		{
			if (line == null)
				return null;

			String[] parts = line.trim().split(SurfaceDictionary.DELIM_REGEX);
			if (parts.length != 2)
				return null;

			String surface = parts[0];
			if (surface == null || surface.isEmpty())
				return null;
			if (!StopWordsUtils.containsLetter(surface) || StopWordsUtils.isStopWord(surface))
				return null;

			int count = Integer.parseInt(parts[1]);

			SurfaceRecord sr = new SurfaceRecord();
			sr.surface = surface;
			sr.senseCount = count;
			return sr;
		}

		private SurfaceRecord()
		{

		}

		@Override
		public int compareTo(SurfaceRecord other)
		{
			return surface.compareTo(other.surface);
		}

		@Override
		public String toString()
		{
			return String.format("%s%s%d", surface, SurfaceDictionary.DELIM_SEQ, senseCount);
		}
	}

	private int						longestInWord			= ConceptConstants.DICTIONARY_LONGEST_IN_WORD;

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
			SurfaceRecord sr = SurfaceRecord.get(line);
			if (sr == null)
			{
				System.err.println("ignoring dictionary line: " + line);
				continue;
			}

			dict.surfaces.add(sr.surface);
			if (sr.senseCount > 1)
			{
				dict.ambiguousSurfaces.add(sr.surface);
			}
		}
		br.close();

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
