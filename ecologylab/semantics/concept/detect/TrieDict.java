package ecologylab.semantics.concept.detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.utils.Trie;

public class TrieDict extends Debug
{

	private Trie					trie							= new Trie();

	private List<String>	ambiguousSurfaces	= new ArrayList<String>();

	private TrieDict()
	{

	}

	public static TrieDict load(File dictionary) throws IOException
	{
		TrieDict td = new TrieDict();
		BufferedReader br = new BufferedReader(new FileReader(dictionary));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] parts = line.trim().split("\t");

			String surface = parts[0];
			td.trie.add(surface);

			int countSenses = Integer.parseInt(parts[1]);
			if (countSenses > 1)
			{
				td.ambiguousSurfaces.add(surface);
			}
		}
		br.close();
		Collections.sort(td.ambiguousSurfaces);
		return td;
	}

	public int longestMatch(String text, int offset)
	{
		return trie.longestMatch(text, offset);
	}

	public String[] getAll()
	{
		return trie.getAll();
	}

	public boolean isAmbiguous(String surface)
	{
		return Collections.binarySearch(ambiguousSurfaces, surface) >= 0;
	}

	public static void main(String[] args) throws IOException
	{
		TrieDict td = TrieDict.load(new File("data/freq-surfaces.dat"));
		td.longestMatch("abc", 0);
	}

}
