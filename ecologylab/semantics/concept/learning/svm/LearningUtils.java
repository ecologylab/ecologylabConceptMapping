package ecologylab.semantics.concept.learning.svm;

import ecologylab.semantics.concept.detect.Instance;
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

	private static svm_node[] constructSVMInstance(double... features)
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

	public static svm_node[] constructSVMInstanceForDisambiguation(Instance inst)
	{
		svm_node[] svmInst = LearningUtils.constructSVMInstance(
					inst.commonness,
					inst.contextualRelatedness,
					inst.contextQuality
					);
		return svmInst;
	}

	public static svm_node[] constructSVMInstanceForDetection(Instance inst)
	{
		svm_node[] svmInst = LearningUtils.constructSVMInstance(
					inst.keyphraseness,
					inst.contextualRelatedness,
					inst.disambiguationConfidence,
					inst.occurrence,
					inst.frequency
					);
		return svmInst;
	}

}
