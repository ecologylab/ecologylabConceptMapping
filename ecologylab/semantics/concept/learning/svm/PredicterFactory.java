package ecologylab.semantics.concept.learning.svm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;

/**
 * This class holds a collection of loaded predicters, so that we don't need to load them again and
 * again.
 * 
 * @author quyin
 * 
 */
public class PredicterFactory
{

	private static final Map<String, SVMPredicter>	pathMap	= new HashMap<String, SVMPredicter>();

	/**
	 * Get a loaded predicter; if not yet loaded, load it.
	 * 
	 * @param modelPath
	 * @param normalizer
	 * @return
	 * @throws IOException
	 */
	public static SVMPredicter get(String modelPath, Normalizer normalizer) throws IOException
	{
		if (!pathMap.containsKey(modelPath))
		{
			svm_model model = svm.svm_load_model(modelPath);
			SVMPredicter predicter = new SVMPredicter(model, normalizer);
			pathMap.put(modelPath, predicter);
		}
		return pathMap.get(modelPath);
	}

}
