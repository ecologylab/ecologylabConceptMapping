package ecologylab.semantics.concept.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.utils.TextUtils;

public class SurfaceExtractor extends Debug
{
	
	TrieDict trieDict;

	public SurfaceExtractor(TrieDict dictionary)
	{
		trieDict = dictionary;
	}

	public Set<String> extract(String text)
	{
		Set<String> surfaces = new HashSet<String>();
		
		int offset = 0;
		while (offset < text.length())
		{
			int len = trieDict.longestMatch(text, offset);
			if (len > 0)
			{
				// matched, find the matched surface & count
				String matchedSurface = text.substring(offset, offset + len);
				surfaces.add(matchedSurface);

				offset += len;
				offset = TextUtils.nextNonWhitespaceIndex(text, offset);
			}
			else
			{
				// not matched, skip a word
				offset = TextUtils.nextWhitespaceIndex(text, offset);
				offset = TextUtils.nextNonWhitespaceIndex(text, offset);
			}
		}
		
		return surfaces;
	}

	public static void main(String[] args) throws IOException
	{
		SurfaceExtractor se = new SurfaceExtractor(TrieDict.load(new File("data/freq-surfaces.dat")));
		String text = readString("usa.wiki");
		
		int n = 1000;
		Set<String> surfaces = null;
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < n; ++i)
			surfaces = se.extract(text);
		long t1 = System.currentTimeMillis();
		
		for (String surface : surfaces)
		{
			System.out.println(surface);
		}
		System.out.println("average time in ms: " + (t1 - t0) * 1.0 / n);
	}

	private static String readString(String filePath) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		while ((line = br.readLine())!= null)
		{
			sb.append(line);
		}
		return sb.toString();
	}

}
