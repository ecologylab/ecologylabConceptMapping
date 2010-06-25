package ecologylab.semantics.conceptmapping.learning.svm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class SVMPredicter
{
	private SVMGaussianNormalization normalization;
	
	private svm_model	model;

	/**
	 * Construct a predictor using given parameters and model.
	 * 
	 * @param parameterFilePath
	 *          This points to the file containing data pre-processing parameters.
	 * @param modelFilePath
	 *          This points to the SVM model file saved by the trainer.
	 * @throws IOException 
	 */
	public SVMPredicter(String parameterFilePath, String modelFilePath) throws IOException
	{
		normalization = new SVMGaussianNormalization(parameterFilePath);
		model = svm.svm_load_model(modelFilePath);
	}

	/**
	 * Predict for a given instance. The resulted probability estimation is saved in results which
	 * will be cleared first. The label with maximum probability estimation is returned.
	 * 
	 * @param instance
	 *          The instance to be predicted.
	 * @param results
	 *          A buffer to store the output probability estimation, in the format of a mapping from
	 *          label to probability.
	 * @return The label with maximum probability estimation.
	 */
	public int predict(svm_node[] instance, Map<Integer, Double> results)
	{
		results.clear();
		normalization.normalize(instance);

		int nr_class = svm.svm_get_nr_class(model);
		int[] labels = new int[nr_class];
		svm.svm_get_labels(model, labels);
		
		if (svm.svm_check_probability_model(model) == 1)
		{
			double[] prob_estimates = new double[nr_class];
			svm.svm_predict_probability(model, instance, prob_estimates);
	
			int max_label = 0;
			double max_prob = 0;
			for (int j = 0; j < nr_class; j++)
			{
				results.put(labels[j], prob_estimates[j]);
				if (prob_estimates[j] > max_prob)
				{
					max_prob = prob_estimates[j];
					max_label = labels[j];
				}
			}
			return max_label;
		}
		else
		{
			return (int) Math.round(svm.svm_predict(model, instance));
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		int numAttribute = 3;
		SVMPredicter p = new SVMPredicter("model/disambi.gaussian_normalization.params", "model/disambi.svm.model");
		
		double[][] test = {
				{0.000022,34.757521,34.757521}, // false
				{0.500000,12.030561,20.921073}, // true
				{0.027237,21.838401,22.880950}, // false
				{0.500000,2.830788,6.330549}, // true
		};
		Map<Integer, Double> results = new HashMap<Integer, Double>();
		
		for (int i = 0; i < test.length; ++i)
		{
			svm_node[] node = new svm_node[numAttribute];
			for (int j = 0; j < numAttribute; ++j)
			{
				node[j] = new svm_node();
				node[j].index = j;
				node[j].value = test[i][j];
			}
			
			int max_label = p.predict(node, results);
			StringBuilder sb = new StringBuilder();
			sb.append(max_label + ": ");
			boolean first = true;
			for (Integer l : results.keySet())
			{
				sb.append((first?"":", ") + l + "(" + results.get(l) + ")");
				first = false;
			}
			System.out.println(sb.toString());
		}
	}

}
