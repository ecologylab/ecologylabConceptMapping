package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.utils.TextUtils;
import ecologylab.semantics.concept.utils.Trie;

public class KeyphrasenessCalculator extends Debug
{
	
	public void compute(File primaryConcepts, File freqSurfaces) throws IOException
	{
		// load freq surfaces into a trie (prefix tree)
		Trie trie = new Trie();
		BufferedReader brFreqSurfaces = new BufferedReader(new FileReader(freqSurfaces));
		String line = null;
		while ((line = brFreqSurfaces.readLine()) != null)
		{
			String surface = line.trim();
			trie.add(surface);
		}
		brFreqSurfaces.close();
		
		// TODO save the trie for future use?
		
		// for each primary concept, retrive wikilinks & wikitexts, and count surface occurrences
		BufferedReader brPrimaryConcepts = new BufferedReader(new FileReader(primaryConcepts));
		line = null;
		while ((line = brPrimaryConcepts.readLine()) != null)
		{
			String concept = line.trim();
			
			// count all occurrences
			String text = getWikiText(concept);
			int offset = 0;
			while (offset < text.length())
			{
				int len = trie.longestMatch(text, offset);
				if (len > 0)
				{
					// matched, find the matched surface & count
					String matchedSurface = text.substring(offset, offset + len);
					countSurfaceOccurrence(matchedSurface);
					
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
			
			// count linked occurrences
			List<String> surfaces = getSurfaces(concept);
			for (String surface : surfaces)
			{
				countLinkedSurfaceOccurrence(surface);
			}
		}
		
		// calculate keyphraseness based on surface occurrences
	}

	private void countLinkedSurfaceOccurrence(String surface)
	{
		// TODO Auto-generated method stub
		
	}

	private void countSurfaceOccurrence(String matchedSurface)
	{
		// TODO Auto-generated method stub
		
	}

	private List<String> getSurfaces(String concept)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private String getWikiText(String concept)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
