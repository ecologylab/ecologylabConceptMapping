package ecologylab.semantics.conceptmapping.wikipedia.dbprepare;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ecologylab.semantics.conceptmapping.wikipedia.StringPool;
import ecologylab.semantics.conceptmapping.wikipedia.dbprepare.InlinkN3Parser.Inlink;
import ecologylab.semantics.conceptmapping.wikipedia.dbprepare.SurfaceN3Parser.Surface;

public class FormatConverter
{
	
	private InlinkN3Parser inlinkParser = new InlinkN3Parser();
	
	private SurfaceN3Parser surfaceParser = new SurfaceN3Parser();
	
	public void convertInlink(String inN3Filepath, String outTSVFilepath) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(inN3Filepath));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.isEmpty())
				continue;
			Inlink il = inlinkParser.parse(line);
			String s = String.format("%s\t%s\t%s", il.toConcept, il.fromConcept, il.surface);
			StringPool.get(outTSVFilepath).addLine(s); // on Windows, line terminator is \r\n
		}
		br.close();
		
		StringPool.closeAll();
	}

	public void convertSurface(String inN3Filepath, String outTSVFilepath) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(inN3Filepath));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.isEmpty())
				continue;
			Surface sf = surfaceParser.parse(line);
			String s = String.format("%s\t%s", sf.surfaceName, sf.concept);
			StringPool.get(outTSVFilepath).addLine(s); // on Windows, line terminator is \r\n
		}
		br.close();
		
		StringPool.closeAll();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		FormatConverter fc = new FormatConverter();
		fc.convertInlink("C:/run/sorted/sorted-inlinks.n3", "C:/run/tsv/sorted-inlinks.tsv");
		fc.convertSurface("C:/run/sorted/sorted-surfaces.n3", "C:/run/tsv/sorted-surfaces.tsv");
	}

}
