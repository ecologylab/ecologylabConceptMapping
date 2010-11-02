package ecologylab.semantics.concept.detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ecologylab.semantics.concept.ConceptConstants;
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

	public Doc(String title, String text, SurfaceDictionary dictionary)
	{
		this.title = title;
		this.text = TextUtils.normalize(text);
		this.totalWords = TextUtils.count(text, " ") + 1;

		// extract surfaces
		for (String surfaceWord : dictionary.extractSurfaces(text))
		{
			Surface surface = Surface.get(surfaceWord);

			if (dictionary.isAmbiguous(surfaceWord))
				ambiSurfaces.add(surface);
			else
				unambiSurfaces.add(surface);

			if (!surfaceOccurrences.containsKey(surface))
				surfaceOccurrences.put(surface, 1);
			else
				surfaceOccurrences.put(surface, surfaceOccurrences.get(surface) + 1);
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
		SurfaceDictionary dict = SurfaceDictionary.load(new File(ConceptConstants.DICTIONARY_PATH));
		Doc doc = null;

		int n = 100;
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < n; ++i)
		{
			doc = new Doc("USA", text, dict);
		}
		long t1 = System.currentTimeMillis();

		if (doc != null)
		{
			System.out.println(doc.getUnambiSurfaces().size() + " unambiguous surfaces found.");
			System.out.println(doc.getAmbiSurfaces().size() + " ambiguous surfaces found.");
			System.out.println("average time in ms: " + (t1 - t0) * 1.0 / n);
		}
		else
		{
			System.out.println("oops! failed.");
		}
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
