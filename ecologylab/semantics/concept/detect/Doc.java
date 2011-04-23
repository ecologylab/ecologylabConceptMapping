package ecologylab.semantics.concept.detect;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.database.SessionPool;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.utils.TextNormalizer;
import ecologylab.semantics.concept.utils.TextUtils;

/**
 * The central class for concept mapping. Not thread-safe (an instance of this class is supposed to
 * be used in a single thread).
 * 
 * @author quyin
 * 
 */
public class Doc
{

	private final String					title;

	private final String					text;

	private final int							totalWords;

	private Map<String, Instance>	instances;

	public Doc(String title, String text)
	{
		this.title = title;
		this.text = TextNormalizer.normalize(text);
		this.totalWords = TextUtils.count(text, " ") + 1;
		
		Session session = SessionPool.getSession();

		// extract surfaces
		for (String surface : SurfaceDictionary.get().extractSurfaces(text))
		{
			WikiSurface ws = WikiSurface.get(surface, session);
			surfaces.add(surface);
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

	public Set<Surface> getSurfaces()
	{
		return surfaces;
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

	Session getSession()
	{
		return session;
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

	public static void main(String[] args) throws IOException
	{
		// String text = TextUtils.readString("usa.wiki");
		String text = "we know that united states census 2000 is famous in united states";
		Doc doc = null;

		int n = 100;
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < n; ++i)
		{
			doc = new Doc("USA", text);
		}
		long t1 = System.currentTimeMillis();

		if (doc != null)
		{
			System.out.println(doc.getSurfaces().size() + " surfaces found.");
			System.out.println("average time in ms: " + (t1 - t0) * 1.0 / n);
		}
		else
		{
			System.out.println("oops! failed.");
		}
	}

}
