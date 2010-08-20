package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.learning.svm.DataSet.LineStruct;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVMTrainer extends Debug
{

	/**
	 * Number of attributes, i.e. dimensionality of x. NOT including label
	 */
	public int									numAttributes;

	protected List<Double>			y;

	public int									nPos;

	public int									nNeg;

	protected List<svm_node[]>	x;

	/**
	 * Train an SVM given parameters and data, and output results into file(s).
	 * 
	 * @param numAttributes
	 * @param dataFilePath
	 *          The training set, in the format like "1,1.0,2.0,3.0 # comments". The first field (1
	 *          herein the example) must be an integer indicating the class label.
	 * @param outputParameterFilePath
	 *          Data pre-processing parameter file path. These parameters are generated during
	 *          training, and will be used in predicting
	 * @param outputModelFilePath
	 *          The output SVM model, in libsvm format
	 * @throws IOException
	 */
	public SVMTrainer(int numAttributes, String dataFilePath, String outputParameterFilePath)
			throws IOException
	{
		this.numAttributes = numAttributes;
		readData(dataFilePath);
		preprocessData(outputParameterFilePath);
	}

	public svm_model train(double C, double gamma)
	{
		assert y.size() == x.size() : "data error: x.size() != y.size()";

		debug("training the model ...");

		// construct svm_problem
		svm_problem prob = new svm_problem();
		prob.l = x.size();
		prob.x = new svm_node[prob.l][];
		prob.y = new double[prob.l];
		for (int i = 0; i < prob.l; ++i)
		{
			prob.x[i] = x.get(i);
			prob.y[i] = y.get(i);
		}

		double negWeight = (double) nPos / nNeg;
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
		param.weight_label = new int[]
		{ ConceptConstants.POS_CLASS_INT_LABEL, ConceptConstants.NEG_CLASS_INT_LABEL };
		param.weight = new double[]
		{ 1, negWeight };
		// inputs
		param.C = C;
		param.gamma = gamma;

		return svm.svm_train(prob, param);
	}

	protected void preprocessData(String outputParameterFilePath) throws IOException
	{
		SVMGaussianNormalization gn = new SVMGaussianNormalization(numAttributes);
		gn.generateParameters(x);
		gn.save(outputParameterFilePath);
		for (svm_node[] inst : x)
			gn.normalize(inst);
	}

	protected void readData(String dataFilePath) throws IOException
	{
		debug("reading data from " + dataFilePath + " ...");

		nPos = 0;
		nNeg = 0;
		y = new ArrayList<Double>();
		x = new ArrayList<svm_node[]>();

		BufferedReader br = new BufferedReader(new FileReader(dataFilePath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			LineStruct ls = new LineStruct();
			int label = DataSet.readALine(line, ls);
			assert ls.feature.length == numAttributes : "format error: " + line;
			if (label == ConceptConstants.POS_CLASS_INT_LABEL)
			{
				nPos++;
			}
			else if (label == ConceptConstants.NEG_CLASS_INT_LABEL)
			{
				nNeg++;
			}
			y.add((double) label);
			x.add(ls.feature);
		}
		br.close();
	}

	public static void saveModel(svm_model model, String modelFilepath) throws IOException
	{
		svm.svm_save_model(modelFilepath, model);
	}

	public static void main(String[] args) throws IOException
	{
		SVMTrainer t = new SVMTrainer(3, "data/disambi-training-balanced.dat",
				"model/disambi-norm-params.dat");
		svm_model model = t.train(8.0, 2.0);
		saveModel(model, "model/disambi-svm.model");
	}

}
