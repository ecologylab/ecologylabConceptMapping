/**
 * 
 */
package ecologylab.semantics.conceptmapping.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ecologylab.semantics.conceptmapping.TextPreprocessor;
import ecologylab.semantics.conceptmapping.Token;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;

/**
 * @author quyin
 * 
 */
public class Vocabulary
{
	public static class Keyphrase
	{
		public String surface;
		public String normForm;

		public float	keyphraseness;
	}

	public static class Sense
	{
		public String surface;
		public String normForm;

		public int		occurrence;

		public String	sense;
	}

	private List<Keyphrase>	keyphrases	= new ArrayList<Keyphrase>();

	private List<Sense>			senses			= new ArrayList<Sense>();

	/**
	 * @param kp_filepath
	 * @throws Exception 
	 */
	private void readKeyphraseFile(String kp_filepath) throws Exception
	{
		String line;
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
			kp.surface = line.substring(first_space + 1);
			kp.normForm = preprocessVocabulary(kp.surface);
			String str_kpn = line.substring(0, first_space);
			kp.keyphraseness = Float.valueOf(str_kpn);

			keyphrases.add(kp);
		}
		inf.close();
	}

	/**
	 * @param sense_filepath
	 * @throws Exception 
	 */
	private void readSenseFile(String sense_filepath) throws Exception
	{
		String line;
		BufferedReader inf = new BufferedReader(new FileReader(new File(sense_filepath)));
		while ((line = inf.readLine()) != null)
		{
			String[] parts = line.split("\\|");
			if (parts.length != 3)
			{
				System.err.println("can't parse the line: " + line);
				continue;
			}

			Sense sense = new Sense();
			sense.surface = parts[1];
			sense.normForm = preprocessVocabulary(sense.surface);
			sense.occurrence = Integer.valueOf(parts[0]);
			sense.sense = parts[2];

			senses.add(sense);
		}
		inf.close();
	}

	public void matchSurfaceNames() throws FileNotFoundException
	{
		System.out.println("sorting keyphrases ...");
		Collections.sort(keyphrases, new Comparator<Keyphrase>()
		{
			public int compare(Keyphrase kp1, Keyphrase kp2)
			{
				return kp1.normForm.compareTo(kp2.normForm);
			}
		});
		System.out.println("sorting senses ...");
		Collections.sort(senses, new Comparator<Sense>()
		{
			public int compare(Sense s1, Sense s2)
			{
				return s1.normForm.compareTo(s2.normForm);
			}
		});

		System.out.println("matching ...");
		PrintWriter pw1 = new PrintWriter("data/keyphrases.txt");
		PrintWriter pw2 = new PrintWriter("data/senses.txt");
		PrintWriter pw3 = new PrintWriter("data/keyphrase-only.txt");
		PrintWriter pw4 = new PrintWriter("data/sense-only.txt");

		int i = 0, j = 0;
		Keyphrase kp = null;
		Sense s = null;
		while (i < keyphrases.size() || j < senses.size())
		{
			kp = null;
			s = null;
			if (i < keyphrases.size())
				kp = keyphrases.get(i);
			if (j < senses.size())
				s = senses.get(j);

			int compare = 0;
			if (kp == null)
			{
				compare = 1;
			}
			if (s == null)
			{
				compare = -1;
			}
			if (kp != null && s != null)
			{
				compare = kp.normForm.compareTo(s.normForm);
			}

			if (compare == 0)
			{
				pw1.format("%s|%s|%f\n", kp.normForm, kp.surface, kp.keyphraseness);
				pw2.format("%s|%s|%d|%s\n", s.normForm, s.surface, s.occurrence, s.sense);
				i++;
				j++;
			}
			else if (compare < 0)
			{
				pw3.format("%s|%s|%f\n", kp.normForm, kp.surface, kp.keyphraseness);
				i++;
			}
			else if (compare > 0)
			{
				pw4.format("%s|%s|%d|%s\n", s.normForm, s.surface, s.occurrence, s.sense);
				j++;
			}
		}

		pw1.close();
		pw2.close();
		pw3.close();
		pw4.close();
	}

	public void generateVocabulary(String keyphrasenessFilepath, String sensesFilepath)
			throws Exception
	{

		System.out.println("reading keyphrases file ...");
		readKeyphraseFile(keyphrasenessFilepath);
		System.out.println("reading senses file ...");
		readSenseFile(sensesFilepath);
		System.out.println("matching surface names ...");
		matchSurfaceNames();
	}
	
	private String preprocessVocabulary(String phrase) throws Exception
	{
		String result = "";
		List<Token> tokens = TextPreprocessor.preprocess(phrase, false);
		for (Token tk : tokens)
		{
			result += tk.normForm;
		}
		return result;
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

		Vocabulary gv = new Vocabulary();
		gv.generateVocabulary(kp_filepath, sense_filepath);
	}
}
