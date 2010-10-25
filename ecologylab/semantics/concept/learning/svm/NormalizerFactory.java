package ecologylab.semantics.concept.learning.svm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NormalizerFactory
{

	private static final Map<String, SVMGaussianNormalization>	pathMap = new HashMap<String, SVMGaussianNormalization>();
	
	public static SVMGaussianNormalization get(String dataFilePath) throws IOException
	{
		if (!pathMap.containsKey(dataFilePath))
		{
			SVMGaussianNormalization normalizer = new SVMGaussianNormalization(dataFilePath);
			pathMap.put(dataFilePath, normalizer);
		}
		return pathMap.get(dataFilePath);
	}
	
}
