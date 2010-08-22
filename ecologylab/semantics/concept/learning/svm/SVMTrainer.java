package ecologylab.semantics.concept.learning.svm;

import java.io.IOException;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVMTrainer extends Debug
{

	private DataSet			trainSet;

	private svm_problem	problem;

	/**
	 * Train an SVM given parameters and data, and output results into file(s).
	 * 
	 * @param trainSet
	 *          The training set. you can load a dataset from a file using DataSet.load().
	 */
	public SVMTrainer(DataSet trainSet)
	{
		this.trainSet = trainSet;

		problem = new svm_problem();
		problem.l = trainSet.getLabels().size();
		problem.x = new svm_node[problem.l][];
		problem.y = new double[problem.l];
		for (int i = 0; i < problem.l; ++i)
		{
			problem.x[i] = trainSet.getFeatures().get(i);
			problem.y[i] = trainSet.getLabels().get(i);
		}

	}

	/**
	 * train a probabilistic SVM model with given parameters.
	 * 
	 * @param C
	 * @param gamma
	 * @return
	 */
	public svm_model train(double C, double gamma)
	{
		debug("training the model ...");

		double negWeight = (double) trainSet.getNumberOfPositiveSamples()
				/ trainSet.getNumberOfNegativeSamples();
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
		param.shrinking = 1;
		param.probability = 1; // probability model by default
		param.nr_weight = 2;
		param.weight_label = new int[] { ConceptConstants.POS_CLASS_INT_LABEL, ConceptConstants.NEG_CLASS_INT_LABEL };
		param.weight = new double[] { 1, negWeight };
		// inputs
		param.C = C;
		param.gamma = gamma;

		return svm.svm_train(problem, param);
	}

	/**
	 * save a trained model into a file.
	 * 
	 * @param model
	 * @param modelFilepath
	 * @throws IOException
	 */
	public static void saveModel(svm_model model, String modelFilepath) throws IOException
	{
		svm.svm_save_model(modelFilepath, model);
	}

	public static void main(String[] args) throws IOException
	{
		DataSet trainSet = DataSet.load("data/disambi-training-balanced.dat");
		SVMTrainer t = new SVMTrainer(trainSet);
		svm_model model = t.train(8.0, 2.0);
		saveModel(model, "model/disambi-svm.model");
	}

}
