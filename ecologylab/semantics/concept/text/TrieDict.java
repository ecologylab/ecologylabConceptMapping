package ecologylab.semantics.concept.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.utils.Trie;

public class TrieDict extends Debug
{
	
	private Trie trie = new Trie();
	
	public static TrieDict convert(List<String> list)
	{
		TrieDict td = new TrieDict();
		td.trie.addAll(list.toArray(new String[0]));
		return td;
	}
	
	public static TrieDict load(File dictionary) throws IOException
	{
		TrieDict td = new TrieDict();
		BufferedReader br = new BufferedReader(new FileReader(dictionary));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String surface = line.trim();
			td.trie.add(surface);
		}
		br.close();
		return td;
	}
	
	public void save(File dictionary) throws IOException
	{
		trie.writeFile(dictionary, "\n", "");
	}
	
	public int longestMatch(String text, int offset)
	{
		return trie.longestMatch(text, offset);
	}

	public String[] getAll()
	{
		return trie.getAll();
	}
	
	public static void main(String[] args) throws IOException
	{
		TrieDict td = TrieDict.load(new File("data/freq-surfaces.dat"));
		td.save(new File("data/freq-surfaces.trie"));
	}
	
}
