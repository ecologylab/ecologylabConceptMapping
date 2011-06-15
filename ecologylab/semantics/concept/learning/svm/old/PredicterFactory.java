package ecologylab.semantics.concept.learning.svm.old;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ecologylab.semantics.concept.learning.Normalizer;

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

	private static final Map<String, SVMPredictor>	pathMap	= new HashMap<String, SVMPredictor>();

	/**
	 * Get a loaded predicter; if not yet loaded, load it.
	 * 
	 * @param modelPath
	 * @param normalizer
	 * @return
	 */
	public static SVMPredictor get(File modelFile, Normalizer normalizer)
	{
		String modelPath = modelFile.getAbsolutePath();
		if (!pathMap.containsKey(modelPath))
		{
			try
			{
				svm_model model = svm.svm_load_model(modelPath);
				SVMPredictor predicter = new SVMPredictor(model, normalizer);
				pathMap.put(modelPath, predicter);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pathMap.get(modelPath);
	}

}
