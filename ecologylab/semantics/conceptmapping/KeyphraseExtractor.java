package ecologylab.semantics.conceptmapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ecologylab.semantics.conceptmapping.utils.Vocabulary;

public class KeyphraseExtractor
{
	public static final double											DEFAULT_KEYPHRASE_FRACTION			= 0.1;

	public static final int													KNOWN_MAX_KEYPHRASE_WORD_COUNT	= 7;

	protected HashMap<String, Vocabulary.Keyphrase>	keyphrases;

	public KeyphraseExtractor() throws NumberFormatException, IOException, Exception
	{
		loadKeyphrases(Vocabulary.DATA_KEYPHRASES_TXT);
	}

	protected void loadKeyphrases(String kp_filepath) throws NumberFormatException, IOException,
			Exception
	{
		keyphrases = new HashMap<String, Vocabulary.Keyphrase>();

		String line;
		BufferedReader inf = new BufferedReader(new FileReader(new File(kp_filepath)));
		while ((line = inf.readLine()) != null)
		{
			String[] parts = line.split("\\|");
			Vocabulary.Keyphrase kp = new Vocabulary.Keyphrase();
			kp.normForm = parts[0];
			kp.surface = parts[1];
			kp.keyphraseness = Float.valueOf(parts[2]);

			keyphrases.put(kp.normForm, kp);
		}
		inf.close();
	}

	public List<Keyphrase> extractKeyphrases(List<Token> tokens)
	{
		List<Keyphrase> results = new ArrayList<Keyphrase>();

		int i = 0;
		while (i < tokens.size())
		{
			int j = i + 1;
			String currentPhrase = null;
			boolean success = false;
			while (j < tokens.size() && j - i < KNOWN_MAX_KEYPHRASE_WORD_COUNT)
			{
				currentPhrase = getPhrase(tokens, i, j);
				String peekNextPhrase = getPhrase(tokens, i, j + 1);
				if (keyphrases.containsKey(currentPhrase) && !keyphrases.containsKey(peekNextPhrase))
				{
					success = true;
					break;
				}
				j++;
			}

			if (success)
			{
				Keyphrase kp = new Keyphrase();
				kp.context = tokens;
				kp.offsetBegin = i;
				kp.offsetEnd = j;
				kp.keyphraseness = keyphrases.get(currentPhrase).keyphraseness;
				results.add(kp);
			}

			i++;
		}

		Collections.sort(results, new Comparator<Keyphrase>()
		{

			@Override
			public int compare(Keyphrase o1, Keyphrase o2)
			{
				return -Float.compare(o1.keyphraseness, o2.keyphraseness);
			}

		});

		int n = (int) Math.round(tokens.size() * DEFAULT_KEYPHRASE_FRACTION);
		if (results.size() > n)
		{
			results = results.subList(0, n);
		}
		return results;
	}

	protected String getPhrase(List<Token> tokens, int offsetBegin, int offsetEnd)
	{
		String result = "";
		for (int i = offsetBegin; i < offsetEnd; ++i)
			result += tokens.get(i).normForm + " ";
		return result.trim();
	}
	
	public static void main(String[] args) throws Exception
	{
		String text = "West End theatre is a popular term for mainstream professional theatre staged in the large theatres of London's \"Theatreland\".[1]  Along with New York's Broadway theatre, West End theatre is usually considered to represent the highest level of commercial theatre in the English speaking world. Seeing a West End show is a common tourist activity in London.[1]\nTotal attendances first surpassed 12 million in 2002, and in June 2005 The Times reported that this record might be beaten in 2005. Total attendance numbers surpassed 13 million in 2007,[2] setting a new record for the West End. Factors behind high ticket sales in the first half of 2005 included new hit musicals such as Billy Elliot, The Producers and Mary Poppins and the high number of film stars appearing. Since the late 1990s there has been an increase in the number of American screen actors on the London stage, and in 2005 these included Brooke Shields, Val Kilmer, Rob Lowe, David Schwimmer and Kevin Spacey.";
		List<Token> tokens = TextPreprocessor.preprocess(text, false);
		KeyphraseExtractor kpe = new KeyphraseExtractor();
		List<Keyphrase> kps = kpe.extractKeyphrases(tokens);
		for (Keyphrase kp : kps)
		{
			System.out.println(kpe.getPhrase(tokens, kp.offsetBegin, kp.offsetEnd) + ":" + kp.keyphraseness);
		}
	}
}
