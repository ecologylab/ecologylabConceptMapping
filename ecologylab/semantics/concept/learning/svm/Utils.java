package ecologylab.semantics.concept.learning.svm;

import libsvm.svm_node;

public class Utils
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

	public static svm_node[] constructSVMInstance(double... features)
	{
		svm_node[] instance = new svm_node[features.length];
		for (int i = 0; i < instance.length; ++i)
		{
			instance[i] = new svm_node();
			instance[i].index = i + 1;
			instance[i].value = features[i];
		}
		return instance;
	}

}
