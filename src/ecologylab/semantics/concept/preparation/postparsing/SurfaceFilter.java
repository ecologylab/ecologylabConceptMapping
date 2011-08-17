package ecologylab.semantics.concept.preparation.postparsing;

import java.util.Arrays;

import ecologylab.semantics.model.text.TermDictionary;

public class SurfaceFilter
{
	private static String[]	obviousStopWords;

	private static String[]	moreStopWords;

	private static String[]	manualFiltered	= {
																					"of the same name",
																					"same name",
																					"film of the same name",
																					"novel of the same name",
																					"following season",
																					"third",
																					"2",
																					"the season",
																					"film version",
																					"the following season",
																					"the same name",
																					"3",
																					"her class",
																					"first season",
																					"hist",
																					"4",
																					"the following year",
																					"first",
																					"fourth",
																					"5",
																					"home",
																					"album of the same name",
																					"1st",
																					"here",
																					"b",
																					"fifth",
																					"6",
																					"2nd",
																					"8",
																					"10",
																					"7",
																					"2008 season",
																					"movie of the same name",
																					"n",
																					"following year",
																					"2007 season",
																					"2009 season",
																					"that season",
																					"3rd",
																					"sixth",
																					"next season",
																					"his father",
																					"previous season",
																					"11th",
																					"15th",
																					"second",
																					"seventh",
																					"14th",
																					"ii",
																					"9",
																					"book of the same name",
																					"2006 season",
																					"2009",
																					"12th",
																					"1995",
																					"song of the same name",
																					"his first season",
																					"16th",
																					"i",
																					"1996",
																					"2008",
																					"1",
																					"the novel",
																					"4th",
																					"2008 09 season",
																					"1999",
																					"19th",
																					"eighth",
																					"1992",
																					"1997",
																					"20th",
																					"1989",
																					"2008 09",
																					"one",
																					"second season",
																					"coat of arms of",
																					"6th",
																					"1988",
																					"5th",
																					"ninth",
																					"play of the same name",
																					"2000",
																					"1984",
																					"1998",
																					"2007",
																					"1990",
																					"1979",
																					"1994",
																					"main article",
																					"2010",
																					"7th",
																					"2007 08 season",
																					"21st",
																					"18th",
																					"1987",
																					"2003",
																					"17th",
																					"1993",
																					"2002",
																					"25th",
																					"1980",
																					"2001",
																					"1991",
																					"13th",
																					"9th",
																					"1982",
																					"finals",
																					"11",
																					"1974",
																					"1972",
																					"1975",
																					"2007 08",
																					"2009 10",
																					"iii",
																					"the final",
																					"12",
																					"p",
																					"22nd",
																					"1985",
																					"23rd",
																					"8th",
																					"1977",
																					"1976",
																					"1971",
																					"16",
																					"1981",
																					"tenth",
																					"1986",
																					"t",
																					"13",
																					"that year",
																					"film of the same title",
																					"2005",
																					"2004",
																					"2009 10 season",
																					"10th",
																					"2005 season",
																					"1973",
																					"1983",
																					"1978",
																					"the next season",
																					"a",
																					"15",
																					"2006 07 season",
																					"x",
																					"24th",
																					"26th",
																					"2006",
																					"novel of the same title",
																					"1967",
																					"the previous season",
																					"2006 election",
																					"2006 07",
																					"1970",
																					"25",
																					"1968",
																					"27th",
																					"a film",
																					"30",
																					"1964",
																					"first album",
																					"18",
																					"the king",
																					"d",
																					"next year",
																					"20",
																					"22",
																					"first film",
																					"self titled",
																					"his wife",
																					"1969",
																					"17",
																					"29th",
																					"28th",
																					"2005 06",
																					"1 time",
																					"e",
																					"2004 05",
																					"19",
																					"play of the same title",
																					"14",
																					"s",
																					"the city",
																					"iv",
																					"21",
																					"1960",
																					"2005 06 season",
																					"27",
																					"the film",
																					"23",
																					"1963",
																					"main line",
																					"nba finals",
																					"upper",
																					"two",
																					"first round",
																					"an episode",
																					"30th",
																					"35th",
																					"2008 election",
																					"28",
																					"same title",
																					"g",
																					"1952",
																					"v",
																					"previous election",
																					"1956",
																					"big brother",
																					"two years later",
																					"a year later",
																					"1948",
																					"1966",
																					"2004 season",
																					"2010 season",
																					"alpha",
																					"31st",
																					"1962",
																					};

	static
	{
		obviousStopWords = TermDictionary.mostObviousStopWordStrings;
		moreStopWords = TermDictionary.moreStopWordStrings;
		Arrays.sort(obviousStopWords);
		Arrays.sort(moreStopWords);
		Arrays.sort(manualFiltered);
	}

	/**
	 * filter this word as a surface.
	 * 
	 * @param word
	 * @return true if it should be filtered out. Otherwise false.
	 */
	public static boolean filter(String word)
	{
		word = word.toLowerCase();
		if (Arrays.binarySearch(obviousStopWords, word) >= 0)
			return true;
		if (Arrays.binarySearch(moreStopWords, word) >= 0)
			return true;
		if (Arrays.binarySearch(manualFiltered, word) >= 0)
			return true;
		return false;
	}

	/**
	 * if this word contains a letter. pure symbols or digits are not considered as surfaces.
	 * 
	 * @param word
	 * @return
	 */
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
