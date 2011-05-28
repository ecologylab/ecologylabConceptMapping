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
	 * @param paramsFile
	 * @return
	 */
	public static Normalizer get(File paramsFile)
	{
		String absolutePath = paramsFile.getAbsolutePath();
		if (!theMap.containsKey(absolutePath))
		{
			Normalizer normalizer = create();
			try
			{
				normalizer.load(paramsFile);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			theMap.put(absolutePath, normalizer);
		}
		return theMap.get(absolutePath);
	}

}
