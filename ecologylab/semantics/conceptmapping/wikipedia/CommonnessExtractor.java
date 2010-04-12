package ecologylab.semantics.conceptmapping.wikipedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ecologylab.semantics.conceptmapping.wikipedia.SurfaceN3Parser.Surface;

public class CommonnessExtractor
{
	private SurfaceN3Parser				parser	= new SurfaceN3Parser();

	private String								surface;

	private Map<String, Integer>	mcc			= new HashMap<String, Integer>();

	private String								sortedSurfacesFilePath;

	private String								unambiSurfaceFilePath;

	private String								ambiSurfaceFilePath;

	private String								commonnessFilePath;

	public CommonnessExtractor()
	{
		this("sorted-surfaces.n3");
	}

	public CommonnessExtractor(String sortedSurfacesFilePath)
	{
		this(sortedSurfacesFilePath, "unambi-surfaces.lst", "ambi-surfaces.lst", "commonness.tsv");
	}

	public CommonnessExtractor(String sortedSurfacesFilePath, String unambiSurfaceFilePath,
			String ambiSurfaceFilePath, String commonnessFilePath)
	{
		this.sortedSurfacesFilePath = sortedSurfacesFilePath;
		this.unambiSurfaceFilePath = unambiSurfaceFilePath;
		this.ambiSurfaceFilePath = ambiSurfaceFilePath;
		this.commonnessFilePath = commonnessFilePath;
	}

	public void extract() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(sortedSurfacesFilePath));

		boolean firstLine = true;
		String line = br.readLine();
		do
		{
			if (line == null)
			{
				processSurface();
				break;
			}

			line = line.trim();
			Surface sf = parser.parse(line);
			if (sf != null)
			{
				if (firstLine)
				{
					surface = sf.surfaceName;
				}

				if (!firstLine && !sf.surfaceName.equals(surface))
				{
					processSurface();

					surface = sf.surfaceName;
					mcc.clear();
					processConcept(sf);
				}
				else
				{
					processConcept(sf);
				}
				firstLine = false;
			}

			line = br.readLine();
		}
		while (true);
		br.close();

		StringPool.closeAll();
	}

	private void processSurface()
	{
		if (mcc.size() == 1)
		{
			StringPool.get(unambiSurfaceFilePath).addLine(surface);
		}
		else
		{
			StringPool.get(ambiSurfaceFilePath).addLine(surface);
		}

		int n = 0; // total # of references of this surface
		for (int k : mcc.values())
			n += k;

		for (String concept : mcc.keySet())
		{
			float commonness = (float) mcc.get(concept) / n;
			String s = String.format("%s\t%s\t%f", surface, concept, commonness);
			StringPool.get(commonnessFilePath).addLine(s);
		}
	}

	private void processConcept(Surface sf)
	{
		int cc = mapTryGet(mcc, sf.concept, 0);
		mcc.put(sf.concept, cc + 1);
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
		CommonnessExtractor ce = new CommonnessExtractor(
				"C:/Users/quyin/run/common-surfaces/sorted-surfaces.n3");
		ce.extract();
	}
}
