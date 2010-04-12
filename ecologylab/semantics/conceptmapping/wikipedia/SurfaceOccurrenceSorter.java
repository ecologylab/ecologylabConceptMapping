package ecologylab.semantics.conceptmapping.wikipedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecologylab.semantics.conceptmapping.wikipedia.SurfaceN3Parser.Surface;

public class SurfaceOccurrenceSorter
{
	protected class Item
	{
		public String surface;
		public int count;
	}
	
	public Map<String, Item> items = new HashMap<String, Item>();
	
	public void process(String inFilePath, String outFilePath) throws IOException
	{
		SurfaceN3Parser parser = new SurfaceN3Parser();
		
		BufferedReader br = new BufferedReader(new FileReader(inFilePath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			line.trim();
			Surface s = parser.parse(line);
			if (items.containsKey(s.surfaceName))
			{
				items.get(s.surfaceName).count++;
			}
			else
			{
				Item it = new Item();
				it.surface = s.surfaceName;
				it.count = 1;
				items.put(s.surfaceName, it);
			}
		}
		br.close();
		
		List<Item> list = new ArrayList<Item>(items.size());
		for (Item item : items.values())
		{
			list.add(item);
		}
		Collections.sort(list, new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2)
			{
				return o2.count - o1.count;
			}
		});
		
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outFilePath)));
		for (Item item : list)
		{
			pw.format("%s\t%d\n", item.surface, item.count);
		}
		pw.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println("usage: <input-surface-n3-file> <output-occurrence-sorted-surfaces-file>");
			return;
		}
		
		SurfaceOccurrenceSorter scs = new SurfaceOccurrenceSorter();
		scs.process(args[0], args[1]);
	}
}
