package ecologylab.semantics.concept.learning.svm;

import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import ecologylab.generic.Debug;
import ecologylab.semantics.concept.learning.Classifier;
import ecologylab.semantics.concept.learning.Constants;
import ecologylab.semantics.concept.learning.DataSet;
import ecologylab.semantics.concept.learning.Normalizer;

public class SvmClassifier extends Debug
		implements Classifier<svm_node[], svm_model, SvmParameter>
{

	private DataSet<svm_node[]>	trainSet;

	private svm_problem					problem;

	private svm_model						model;

	@Override
	public void setTrainingDataSet(DataSet<svm_node[]> trainSet)
	{
		this.trainSet = trainSet;

		problem = new svm_problem();
		problem.l = trainSet.getLabels().size();
		problem.x = new svm_node[problem.l][];
		problem.y = new double[problem.l];
		for (int i = 0; i < problem.l; ++i)
		{
			problem.x[i] = trainSet.getInstances().get(i);
			problem.y[i] = trainSet.getLabels().get(i);
		}
	}

	@Override
	public svm_model trainModel(SvmParameter parameters)
	{
		// for convenience
		double C = parameters.C;
		double gamma = parameters.gamma;

		debug("training the model with params: C=" + C + ", gamma=" + gamma + "...");

		double negWeight = (double) trainSet.getNumberOfSamplesWithLabel(Constants.POS_CLASS_INT_LABEL)
				/ trainSet.getNumberOfSamplesWithLabel(Constants.NEG_CLASS_INT_LABEL);
		debug("negative class weight: " + negWeight);

		// construct svm_parameter
		svm_parameter param = new svm_parameter();
		// defaults
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.degree = 3;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 100;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 0; // don't use shrinking to speed up training
		param.probability = 1; // probability model by default
		param.nr_weight = 2;
		param.weight_label = new int[] { Constants.POS_CLASS_INT_LABEL, Constants.NEG_CLASS_INT_LABEL };
		param.weight = new double[] { 1, negWeight };
		// inputs
		param.C = C;
		param.gamma = gamma;

		model = svm.svm_train(problem, param);
		return model;
	}

	@Override
	public void useModel(svm_model model)
	{
		this.model = model;
	}

	@Override
	public int classify(Normalizer normalizer, svm_node[] instance, Map<Integer, Double> results)
	{
		if (normalizer != null)
			normalizer.normalize(instance);

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
