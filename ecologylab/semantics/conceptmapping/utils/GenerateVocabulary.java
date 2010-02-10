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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

	private List<Keyphrase>	keyphrases	= new ArrayList<Keyphrase>();

	private List<Sense>			senses			= new ArrayList<Sense>();

	/**
	 * @param kp_filepath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void readKeyphraseFile(String kp_filepath) throws FileNotFoundException, IOException
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
			kp.term.surface = line.substring(first_space + 1);
			kp.term.normForm = TextPreprocessor.preprocess(kp.term.surface);
			String str_kpn = line.substring(0, first_space);
			kp.keyphraseness = Float.valueOf(str_kpn);

			keyphrases.add(kp);
		}
		inf.close();
	}

	/**
	 * @param sense_filepath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void readSenseFile(String sense_filepath) throws FileNotFoundException, IOException
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
			sense.term.surface = parts[1];
			sense.term.normForm = TextPreprocessor.preprocess(sense.term.surface);
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
				return kp1.term.normForm.compareTo(kp2.term.normForm);
			}
		});
		System.out.println("sorting senses ...");
		Collections.sort(senses, new Comparator<Sense>()
		{
			public int compare(Sense s1, Sense s2)
			{
				return s1.term.normForm.compareTo(s2.term.normForm);
			}
		});

		System.out.println("matching ...");
		PrintWriter pw1 = new PrintWriter("keyphrases.txt");
		PrintWriter pw2 = new PrintWriter("senses.txt");
		PrintWriter pw3 = new PrintWriter("keyphrase-only.txt");
		PrintWriter pw4 = new PrintWriter("sense-only.txt");

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
				compare = kp.term.normForm.compareTo(s.term.normForm);
			}
			
			if (compare == 0)
			{
				pw1.format("%s|%s|%f\n", kp.term.normForm, kp.term.surface, kp.keyphraseness);
				pw2.format("%s|%s|%d|%s\n", s.term.normForm, s.term.surface, s.occurrence, s.sense);
				i++;
				j++;
			}
			else if (compare < 0)
			{
				pw3.format("%s|%s|%f\n", kp.term.normForm, kp.term.surface, kp.keyphraseness);
				i++;
			}
			else if (compare > 0)
			{
				pw4.format("%s|%s|%d|%s\n", s.term.normForm, s.term.surface, s.occurrence, s.sense);
				j++;
			}
		}

		pw1.close();
		pw2.close();
		pw3.close();
		pw4.close();
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

		GenerateVocabulary gv = new GenerateVocabulary();
		System.out.println("reading keyphrases file ...");
		gv.readKeyphraseFile(kp_filepath);
		System.out.println("reading senses file ...");
		gv.readSenseFile(sense_filepath);
		System.out.println("matching surface names ...");
		gv.matchSurfaceNames();
	}
}
