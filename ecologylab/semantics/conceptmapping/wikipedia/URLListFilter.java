package ecologylab.semantics.conceptmapping.wikipedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class URLListFilter
{
	public static void main(String[] args) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter("filtered.lst"));
		
		BufferedReader br = new BufferedReader(new FileReader("Z:\\wikipedia-en-html\\html.lst"));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.trim().isEmpty())
				break;

			if (WikipediaPageParsing.isSpecialPage(line))
				continue;
			
			bw.write(line);
			bw.newLine();
		}
		br.close();
		
		bw.close();
	}
}
