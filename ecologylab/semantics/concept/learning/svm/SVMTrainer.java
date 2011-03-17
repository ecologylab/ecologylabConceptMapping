package ecologylab.semantics.concept.learning.svm;

import java.io.File;
import java.io.IOException;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 * The trainer that generates a SVM model (with normalization info) from a dataset and parameters.
 * 
 * @author quyin
 * 
 */
public class SVMTrainer extends Debug
{

	private DataSet			trainSet;

	private Normalizer	normalizer;

	private svm_model		model;

	private svm_problem	problem;

	/**
	 * Construct a trainer with a data set. The data set will be immediately normalized. However the
	 * training process will not start immediately because you may want to try different parameters.
	 * 
	 * @param trainSet
	 */
	public SVMTrainer(DataSet trainSet)
	{
		this.normalizer = NormalizerFactory.create();
		this.normalizer.initialize(trainSet);
		this.normalizer.normalize(trainSet);
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
	 */
	public void train(double C, double gamma)
	{
		debug("training the model with params: C=" + C + ", gamma=" + gamma + "...");

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
		param.shrinking = 0; // don't use shrinking to speed up training
		param.probability = 1; // probability model by default
		param.nr_weight = 2;
		param.weight_label = new int[] { ConceptConstants.POS_CLASS_INT_LABEL,
				ConceptConstants.NEG_CLASS_INT_LABEL };
		param.weight = new double[] { 1, negWeight };
		// inputs
		param.C = C;
		param.gamma = gamma;

		model = svm.svm_train(problem, param);
	}

	public svm_model getModel()
	{
		return model;
	}

	public Normalizer getNormalizer()
	{
		return normalizer;
	}

	/**
	 * Save the training results (including the model and normalization parameters) to fiels.
	 * 
	 * @param modelPath
	 * @param normParamsPath
	 * @throws IOException
	 */
	public void saveTrainingResults(String modelPath, String normParamsPath) throws IOException
	{
		svm.svm_save_model(modelPath, model);
		normalizer.save(new File(normParamsPath));
	}

	// for test only
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println("args: <training-set-data-file-path> <result-prefix>");
		}
		String dataPath = args[0];
		String prefix = args[1];

		DataSet trainSet = DataSet.load(new File(dataPath));
		SVMTrainer t = new SVMTrainer(trainSet);
		t.train(8, 2);
		t.saveTrainingResults(prefix + ".model", prefix + ".norm");
	}

}
