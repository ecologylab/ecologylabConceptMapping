package ecologylab.semantics.concept.learning.svm.old;

import java.io.IOException;
import java.util.Map;

import ecologylab.semantics.concept.learning.Normalizer;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class SVMPredictor
{

	private svm_model		model;

	private Normalizer	normalizer;

	/**
	 * 
	 * @param modelPath
	 *          Path to the model file.
	 * @param normParamsPath
	 *          Path to the normalization parameters file.
	 * @throws IOException
	 */
	public SVMPredictor(svm_model model, Normalizer normalizer) throws IOException
	{
		this.model = model;
		this.normalizer = normalizer;
	}

	public int getNumOfSVs()
	{
		return model.getTotalNumOfSVs();
	}

	/**
	 * Predict for a given instance. The resulted probability estimation is saved in results which
	 * will be cleared first. The label with maximum probability estimation is returned.
	 * <p />
	 * This method is thread-safe, given thread-safe results and kvalueBuffer.
	 * 
	 * @param instance
	 *          The instance to be predicted.
	 * @param results
	 *          A buffer to store the output probability estimation, in the format of a mapping from
	 *          label to probability.
	 * @param kvaluebuffer
	 *          a buffer used to eliminate unnecessary creation of double[]s.
	 * @return The label with maximum probability estimation.
	 */
	public int predict(svm_node[] instance, Map<Integer, Double> results, double[] kvalueBuffer)
	{
		normalizer.normalize(instance);

		int nr_class = svm.svm_get_nr_class(model);
		int[] labels = new int[nr_class];
		svm.svm_get_labels(model, labels);

		if (svm.svm_check_probability_model(model) == 1)
		{
			double[] prob_estimates = new double[nr_class];
			svm.svm_predict_probability(model, instance, prob_estimates, kvalueBuffer);

			int max_label = 0;
			double max_prob = 0;
			for (int j = 0; j < nr_class; j++)
			{
				if (results != null)
				{
					results.put(labels[j], prob_estimates[j]);
				}

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

}
