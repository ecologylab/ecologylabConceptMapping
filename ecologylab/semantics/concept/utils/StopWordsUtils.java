package ecologylab.semantics.concept.utils;

import java.util.Arrays;

import ecologylab.semantics.model.text.TermDictionary;

public class StopWordsUtils
{
	private static String[] obviousStopWords;
	
	private static String[] moreStopWords;
	
	static
	{
		obviousStopWords = TermDictionary.mostObviousStopWordStrings;
		moreStopWords = TermDictionary.moreStopWordStrings;
		Arrays.sort(obviousStopWords);
		Arrays.sort(moreStopWords);
	}

	public static boolean isStopWord(String word)
	{
		word = word.toLowerCase();
		if (Arrays.binarySearch(obviousStopWords, word) >= 0)
			return true;
		if (Arrays.binarySearch(moreStopWords, word) >= 0)
			return true;
		return false;
	}
	
	public static boolean containsLetter(String word)
	{
		for (int i = 0; i < word.length(); ++i)
		{
			char c = word.charAt(i);
			if (Character.isLetter(c))
				return true;
		}
		return false;
	}
	
}
