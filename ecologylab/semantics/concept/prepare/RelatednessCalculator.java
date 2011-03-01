package ecologylab.semantics.concept.prepare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ecologylab.semantics.concept.detect.Concept;
import ecologylab.semantics.concept.utils.Log;
import ecologylab.semantics.concept.utils.TextUtils;

public class RelatednessCalculator extends Log
{

	private static final double	epsilon						= 0.0000000001;

	private static final int		d									= 100;

	private PrintWriter					out;

	private String							lastKey						= null;

	private long								total							= 0;

	private long								processed					= 0;

	private long								processedNonZero	= 0;

	public RelatednessCalculator(PrintWriter out, String lastKey)
	{
		this.out = out;
		this.lastKey = lastKey;
	}

	/**
	 * assuming that the two lists have been sorted.
	 * 
	 * @param titleList1
	 * @param titleList2
	 * @param out
	 */
	public void calcRelatedness(List<String> titleList1, List<String> titleList2)
	{
		long a = titleList1.size();
		long b = titleList2.size();
		total = a * b;

		for (String concept1 : titleList1)
		{
			for (String concept2 : titleList2)
			{
				String key = null;
				key = concept1 + "\t" + concept2;

				if (concept1.compareTo(concept2) > 0)
				{
					log("skipping [%s]...", key);
					progress(false);
					continue;
				}

				if (lastKey != null && key.compareTo(lastKey) <= 0)
				{
					log("skipping [%s], already processed by previous runs...", key);
					progress(false);
					continue;
				}

				double relatedness = 0;
				Concept c1 = new Concept(concept1);
				Concept c2 = new Concept(concept2);
				relatedness = c1.getRelatedness(c2);
				if (relatedness > epsilon)
				{
					progress(true);
					out.format("%s\t%s\t%f\n", concept1, concept2, relatedness);
				}
				else
				{
					progress(false);
				}
			}
		}
	}

	private void progress(boolean incNonZero)
	{
		processed++;
		if (incNonZero)
			processedNonZero++;
		if (processed % d == 0)
		{
			log("processed/total: %d/%d (%f%%)", processed, total, processed * 100.0 / total);
			log("non-zero/processed: %d/%d (%f%%)", processedNonZero, processed, processedNonZero * 100.0
					/ processed);
			out.flush();
		}
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 3)
		{
			System.err.println("args: <concept-list-1> <concept-list-2> <result-file-name>");
			System.exit(-1);
		}

		File f1 = new File(args[0]);
		File f2 = new File(args[1]);
		File ouf = new File(args[2]);

		List<String> pairs = new ArrayList<String>();
		List<Double> rs = new ArrayList<Double>();
		String lastKey = null;
		if (ouf.exists())
		{
			BufferedReader br = new BufferedReader(new FileReader(ouf));
			String line = null;
			while ((line = br.readLine()) != null)
			{
				String[] parts = line.split("\t");
				if (parts.length != 3)
				{
					System.err.println("broken line: " + line);
					continue;
				}
				String pair = parts[0] + "\t" + parts[1];
				double r = Double.parseDouble(parts[2]);
				pairs.add(pair);
				rs.add(r);
				lastKey = pair;
			}
			br.close();

			System.err.println(pairs.size() + " pair(s) read from previous results.");
			System.err.println("last pair: " + lastKey);
		}

		PrintWriter out = new PrintWriter(ouf);
		if (pairs != null)
		{
			for (int i = 0; i < pairs.size(); ++i)
			{
				out.format("%s\t%f\n", pairs.get(i), rs.get(i));
			}
			out.flush();
		}

		List<String> list1 = TextUtils.loadTxtAsList(f1, false);
		List<String> list2 = TextUtils.loadTxtAsList(f2, false);
		Collections.sort(list1);
		Collections.sort(list2);

		RelatednessCalculator rc = new RelatednessCalculator(out, lastKey);
		rc.setLogDestination(new PrintStream(new File(ouf.getName() + ".log")));
		rc.calcRelatedness(list1, list2);

		out.close();
	}

}
