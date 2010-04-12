package ecologylab.semantics.conceptmapping.wikipedia;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ecologylab.semantics.conceptmapping.wikipedia.InlinkN3Parser.Inlink;

public class RelatednessExtractor
{

	public int												reportEvery	= 1000000;

	private InlinkN3Parser						parser			= new InlinkN3Parser();

	private Map<String, Set<String>>	mci					= new HashMap<String, Set<String>>();

	private String										inlinksFilePath;

	private String										relatednessFilePath;

	public RelatednessExtractor(String inlinksFilePath, String relatednessFilePath)
			throws FileNotFoundException, IOException
	{
		this.inlinksFilePath = inlinksFilePath;
		this.relatednessFilePath = relatednessFilePath;
		loadInlinks();
	}

	public void extract()
	{
		List<String> concepts = new ArrayList<String>(mci.keySet());
		System.out.println("calculating relatedness ...");
		for (long i = 0; i < concepts.size(); ++i)
		{
			for (long j = i + 1; j < concepts.size(); ++j)
			{
				String c1 = concepts.get((int) i);
				String c2 = concepts.get((int) j);
				long idx = i * concepts.size() + j;
				if (idx % reportEvery == 0)
				{
					System.out.format("[%d]processing %s and %s ...\n", idx, c1, c2);
				}
				double relatedness = getRelatedness(c1, c2);
				if (relatedness == 0)
					continue;
				String s = String.format("%s\t%s\t%f", c1, c2, relatedness);
				StringPool.get(relatednessFilePath).addLine(s);
			}
		}

		System.out.println("finished.");
		StringPool.closeAll();
	}

	private void loadInlinks() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(inlinksFilePath));

		System.out.println("reading inlink data ...");
		String line;
		while ((line = br.readLine()) != null)
		{
			line = line.trim();
			Inlink il = parser.parse(line);
			if (il != null)
			{
				Set<String> inlinks = mapTryGet(mci, il.toConcept, new HashSet<String>());
				inlinks.add(il.fromConcept);
			}
		}
		System.out.format("%d concepts loaded.\n", mci.size());
		br.close();
	}

	public double getRelatedness(String c1, String c2)
	{
		if (!mci.containsKey(c1) || !mci.containsKey(c2))
			return 0;

		Set<String> set1 = mci.get(c1);
		Set<String> set2 = mci.get(c2);
		Set<String> intersection = new HashSet<String>(set1);
		intersection.retainAll(set2);

		int s1 = set1.size();
		int s2 = set2.size();
		int si = intersection.size();

		if (s1 == 0 || s2 == 0 || si == 0)
			return 0;

		return (Math.log(Math.max(s1, s2)) - Math.log(si))
				/ (Math.log(mci.size()) - Math.log(Math.min(s1, s2)));
	}

	private <TKey, TValue> TValue mapTryGet(Map<TKey, TValue> map, TKey key, TValue defaultValue)
	{
		if (map.containsKey(key))
			return map.get(key);
		map.put(key, defaultValue);
		return defaultValue;
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println("arguments: <inlink-n3-file-path> <output-relatedness-file-path>");
			return;
		}

		RelatednessExtractor re = new RelatednessExtractor(args[0], args[1]);
		re.extract();
	}

}
