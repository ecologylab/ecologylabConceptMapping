/**
 * 
 */
package ecologylab.semantics.conceptmapping.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ecologylab.semantics.conceptmapping.Term;
import ecologylab.semantics.conceptmapping.TextPreprocessor;

/**
 * @author quyin
 * 
 */
public class GenerateVocabulary
{
	public static class Keyphrase
	{
		public Term		term	= new Term();

		public float	keyphraseness;
	}

	public static class Sense
	{
		public Term		term	= new Term();

		public int		occurrence;

		public String	sense;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		if (args.length != 2)
		{
			System.out
					.println("usage: GenerateVocabulary <keyphraseness-file-path> <word-sense-file-path>");
			System.exit(-1);
		}

		String kp_filepath = args[0];
		String sense_filepath = args[1];
		String line = null;

		ArrayList<Keyphrase> keyphrases = new ArrayList<Keyphrase>();
		BufferedReader inf = new BufferedReader(new FileReader(new File(kp_filepath)));
		while ((line = inf.readLine()) != null)
		{
			int first_space = line.indexOf(' ');
			if (first_space < 0)
			{
				System.err.println("the line does not contain a space: " + line);
				continue;
			}

			Keyphrase kp = new Keyphrase();
			kp.term.surface = line.substring(first_space + 1);
			kp.term.normForm = TextPreprocessor.preprocess(line);
			String str_kpn = line.substring(0, first_space);
			kp.keyphraseness = Float.valueOf(str_kpn);

			keyphrases.add(kp);
		}
		inf.close();

		ArrayList<Sense> senses = new ArrayList<Sense>();
		inf = new BufferedReader(new FileReader(new File(sense_filepath)));
		while ((line = inf.readLine()) != null)
		{
			String[] parts = line.split("\\|");
			if (parts.length != 3)
			{
				System.err.println("can't parse the line: " + line);
				continue;
			}

			Sense sense = new Sense();
			sense.term.surface = parts[1];
			sense.term.normForm = TextPreprocessor.preprocess(line);
			sense.occurrence = Integer.valueOf(parts[0]);
			sense.sense = parts[2];

			senses.add(sense);
		}
		inf.close();
	}

}
