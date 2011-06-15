package ecologylab.semantics.concept.learning.svm.old;

import libsvm.svm_node;

/**
 * Utility functions for this package.
 * 
 * @author quyin
 * 
 */
public class LearningUtils
{

	public static String instanceToString(svm_node[] instance)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < instance.length; ++i)
		{
			sb.append((i == 0 ? "" : ",") + instance[i].index + ":"
					+ String.format("%+.4f", instance[i].value));
		}
		return sb.toString();
	}

}
