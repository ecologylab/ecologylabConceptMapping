package ecologylab.semantics.conceptmapping.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmbiguousPhrasesFinder
{
	public static final String SENSE_FILEPATH = "data/senses.txt";
	public static final String AMBI_WORDS_FILEPATH = "data/ambi-words.txt";
	public static final String AMBI_SENSES_FILEPATH = "data/ambi-senses.txt";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		Map<String, List<String>> wordMap = new HashMap<String, List<String>>();
		Map<String, List<String>> senseMap = new HashMap<String, List<String>>();
		
		BufferedReader br = new BufferedReader(new FileReader(SENSE_FILEPATH));
		while (true)
		{
			String line = br.readLine();
			if (line == null || line.isEmpty())
				break;
			
			String[] parts = line.split("\\|");
			if (!wordMap.containsKey(parts[0]))
			{
				wordMap.put(parts[0], new ArrayList<String>());
			}
			wordMap.get(parts[0]).add(line);
			if (!senseMap.containsKey(parts[3]))
			{
				senseMap.put(parts[3], new ArrayList<String>());
			}
			senseMap.get(parts[3]).add(line);
		}
		br.close();
		
		PrintWriter pw = new PrintWriter(new FileWriter(AMBI_WORDS_FILEPATH));
		List<String> ambiWords =  new ArrayList<String>();
		for (String key : wordMap.keySet())
		{
			List<String> lines = wordMap.get(key);
			if (lines.size() > 1)
			{
				ambiWords.add(key);
				for (String line : lines)
				{
					pw.println(line);
				}
			}
		}
		pw.close();
		
		pw = new PrintWriter(new FileWriter(AMBI_SENSES_FILEPATH));
		List<String> ambiSenses = new ArrayList<String>();
		for (String key : senseMap.keySet())
		{
			List<String> lines = senseMap.get(key);
			if (lines.size() > 1)
			{
				ambiSenses.add(key);
				for (String line : lines)
				{
					pw.println(line);
				}
			}
		}
		pw.close();
		
		System.out.format("%d ambiguous words in total.\n", ambiWords.size());
		System.out.format("%d ambiguous senses in total.\n", ambiSenses.size());
	}

}
