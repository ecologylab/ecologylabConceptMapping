package ecologylab.semantics.concept.learning.svm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory of normalizers. It controls the creation of normalizers, and caches all the loaded
 * normalizers.
 * 
 * @author quyin
 * 
 */
public class NormalizerFactory
{

	private static final Map<String, Normalizer>	theMap	= new HashMap<String, Normalizer>();

	/**
	 * Create an empty normalizer.
	 * 
	 * @return
	 */
	public static Normalizer create()
	{
		return new GaussianNormalizer();
	}

	/**
	 * Get a loaded normalizer from the cache. If it is not loaded yet, load it and cache it.
	 * 
	 * @param paramsPath
	 * @return
	 * @throws IOException
	 */
	public static Normalizer get(File paramsPath) throws IOException
	{
		String absolutePath = paramsPath.getAbsolutePath();
		if (!theMap.containsKey(absolutePath))
		{
			Normalizer normalizer = create();
			normalizer.load(paramsPath);
			theMap.put(absolutePath, normalizer);
		}
		return theMap.get(absolutePath);
	}

}
