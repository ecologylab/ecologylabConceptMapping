package ecologylab.semantics.concept.learning.svm;

import java.util.HashMap;
import java.util.Map;

public class PredicterFactory
{

	private static final Map<String, SVMPredicter>	pathMap = new HashMap<String, SVMPredicter>();
	
	public static SVMPredicter get(String modelFilePath)
	{
		if (!pathMap.containsKey(modelFilePath))
		{
			SVMPredicter predicter = new SVMPredicter(modelFilePath);
			pathMap.put(modelFilePath, predicter);
		}
		return pathMap.get(modelFilePath);
	}
	
}
