package ecologylab.semantics.concept.detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ecologylab.semantics.concept.utils.TextUtils;

/**
 * textual context for given text. surfaces are recognized and sorted by ambiguity. occurrences are
 * recorded, based on which frequencies can be computed.
 * 
 * @author quyin
 * 
 */
public class Doc
{

	private final String					title;

	private final String					text;

	private final int							totalWords;

	private Set<Surface>					unambiSurfaces				= new HashSet<Surface>();

	private Set<Surface>					ambiSurfaces					= new HashSet<Surface>();

	private Map<Surface, Integer>	surfaceOccurrences		= new HashMap<Surface, Integer>();

	private Set<Instance>					disambiguationResults	= null;

	private Set<Instance>					detectionResults			= null;

	public Doc(String title, String text, TrieDict dictionary)
	{
		this.title = title;
		this.text = TextUtils.normalize(text);
		this.totalWords = TextUtils.count(text, " ") + 1;

		// extract surfaces
		int offset = 0;
		while (offset < text.length())
		{
			int len = dictionary.longestMatch(text, offset);
			if (len > 0)
			{
				// matched, find the matched surface & count
				String matchedSurface = text.substring(offset, offset + len);
				Surface surface = Surface.get(matchedSurface);

				if (dictionary.isAmbiguous(matchedSurface))
					ambiSurfaces.add(surface);
				else
					unambiSurfaces.add(surface);

				if (!surfaceOccurrences.containsKey(surface))
					surfaceOccurrences.put(surface, 1);
				else
					surfaceOccurrences.put(surface, surfaceOccurrences.get(surface) + 1);

				offset += len;
				offset = TextUtils.nextNonWhitespaceIndex(text, offset);
			}
			else
			{
				// not matched, skip a word
				offset = TextUtils.nextWhitespaceIndex(text, offset);
				offset = TextUtils.nextNonWhitespaceIndex(text, offset);
			}
		}
	}

	public String getTitle()
	{
		return title;
	}

	public String getText()
	{
		return text;
	}

	public int getTotalWords()
	{
		return totalWords;
	}

	public Set<Surface> getUnambiSurfaces()
	{
		return unambiSurfaces;
	}

	public Set<Surface> getAmbiSurfaces()
	{
		return ambiSurfaces;
	}

	public Map<Surface, Integer> getSurfaceOccurrences()
	{
		return surfaceOccurrences;
	}

	public int getNumberOfOccurrences(Surface surface)
	{
		if (surfaceOccurrences.containsKey(surface))
		{
			return surfaceOccurrences.get(surface);
		}
		return 0;
	}

	public Set<Instance> disambiguate()
	{
		if (disambiguationResults == null)
		{
			Disambiguator disambiguator = new Disambiguator(this);
			try
			{
				disambiguationResults = disambiguator.disambiguate();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return disambiguationResults;
	}

	public Set<Instance> detect()
	{
		if (detectionResults == null)
		{
			Set<Instance> instances = disambiguate();
			Detector detector = new Detector(this);
			try
			{
				detectionResults = detector.detect(instances);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return detectionResults;
	}

	/* ==================== test ==================== */

	public static void main(String[] args) throws IOException
	{
		String text = readString("usa.wiki");

		int n = 1000;
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < n; ++i)
		{
			Doc doc = new Doc("USA", text, TrieDict.load(new File("data/freq-surfaces.dat")));
			for (Surface surface : doc.getUnambiSurfaces())
			{
				System.out.println(surface);
			}
		}
		long t1 = System.currentTimeMillis();

		System.out.println("average time in ms: " + (t1 - t0) * 1.0 / n);
	}

	private static String readString(String filePath) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		while ((line = br.readLine()) != null)
		{
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}

}
