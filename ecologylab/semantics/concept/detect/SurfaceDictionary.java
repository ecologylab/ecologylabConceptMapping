package ecologylab.semantics.concept.detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.utils.TextUtils;
import ecologylab.semantics.concept.utils.Trie;

/**
 * data object model for client-side cached surface dictionary. retrieve and index surfaces with
 * number of senses. extract surfaces from free text.
 * 
 * @author quyin
 *
 */
// TODO 1. change data structure to sorted list to save memory;
//      2. refer to Milne paper to filter extracted surfaces.
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
			/*
			 * not necessary because the dictionary has been pre-processed
			 * 
			 * if (!StopWordsUtils.containsLetter(surface) || StopWordsUtils.isStopWord(surface)) return
			 * null;
			 */

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

	// these surface collections are fake because they have an extra trailing whitespace " ".
	private Trie	surfaces					= new Trie();

	private Trie	ambiguousSurfaces	= new Trie();

	private SurfaceDictionary()
	{

	}

	public static SurfaceDictionary load(File dictionary) throws IOException
	{
		System.err.print("loading dictionary from " + dictionary.getPath() + "...");
		
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

		System.err.println("loaded");
		return dict;
	}

	public static SurfaceDictionary load(String dictionaryPath) throws IOException
	{
		return load(new File(dictionaryPath));
	}

	public boolean hasSurface(String surface)
	{
		return surfaces.isEntry(surface);
	}

	public boolean isAmbiguous(String surface)
	{
		return ambiguousSurfaces.isEntry(surface);
	}

	public String[] getAll()
	{
		return surfaces.getAll();
	}

	/**
	 * extract all surfaces using this dictionary.
	 * 
	 * @param text
	 *          NORMALIZEDA text.
	 * @return
	 */
	public List<String> extractSurfaces(String text)
	{
		List<String> rst = new ArrayList<String>();

		int offset = 0;
		while (offset < text.length())
		{
			List<String> extracted = surfaces.match(text, offset);
			for (String s : extracted)
			{
				rst.add(s);
			}

			offset = TextUtils.nextWhitespaceIndex(text, offset);
			offset = TextUtils.nextNonWhitespaceIndex(text, offset);
		}

		return rst;
	}

	public static void main(String[] args) throws IOException
	{
		SurfaceDictionary dict = SurfaceDictionary.load(new File(ConceptConstants.DICTIONARY_PATH));
		String testString = "we know that united states 2000 census is famous in united states";
		System.out.println(dict.extractSurfaces(testString));
	}

}
