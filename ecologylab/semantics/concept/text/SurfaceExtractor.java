package ecologylab.semantics.concept.text;

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

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
