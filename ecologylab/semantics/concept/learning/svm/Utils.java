package ecologylab.semantics.concept.learning.svm;

import ecologylab.semantics.concept.ConceptConstants;
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

	/**
	 * read a line and convert it to svm_node[]. the line must be in format of
	 * "label,feature1,feature2,... # comments".
	 * 
	 * @param line
	 * @param buffy
	 *          a buffer storing resulting svm_node[]. must be initialized to the appropriate size,
	 *          with null values in each position.
	 * @return the class label of this line. 0 indicates an ignored line.
	 */
	public static int lineToInstance(String line, svm_node[] buffy)
	{
		int sharp = line.indexOf('#');
		if (sharp >= 0)
		{
			line = line.substring(0, sharp);
		}
		line = line.trim();
		if (line.isEmpty())
			return 0;

		String[] parts = line.split(",");
		int label = Integer.parseInt(parts[0]);

		if (buffy != null)
		{
			assert buffy.length == parts.length - 1 : "format error: " + line;
			for (int i = 0; i < buffy.length; ++i)
			{
				buffy[i] = new svm_node();
				buffy[i].index = i + 1;
				buffy[i].value = Double.valueOf(parts[i + 1]);
			}
		}

		return label;
	}
}
