package ecologylab.semantics.conceptmapping.learning.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVMTrainer
{

	/**
	 * Number of attributes, i.e. dimensionality of x. NOT including label
	 */
	public int									numAttributes	= 3;

	/**
	 * Positive label value
	 */
	public String								posLabel			= "true";

	/**
	 * Negative label value
	 */
	public String								negLabel			= "false";

	/**
	 * The parameter C (cost) in SVM model
	 */
	public double								C							= 8;

	/**
	 * The parameter gamma (RBF kernel shape) in SVM model
	 */
	public double								gamma					= 2;

	protected List<Double>			y;

	protected List<svm_node[]>	x;

	/**
	 * The output model
	 */
	public svm_model						model;

	/**
	 * Train an SVM given parameters and data, and output results into file(s).
	 * 
	 * @param numAttributes
	 * @param posLabel
	 * @param negLabel
	 * @param C
	 * @param gamma
	 * @param dataFilePath
	 *          The training set, in the format like "true,1.0,2.0,3.0 # comments"
	 * @param outputParameterFilePath
	 *          Data pre-processing parameter file path. These parameters are generated during
	 *          training, and will be used in predicting
	 * @param outputModelFilePath
	 *          The output SVM model, in libsvm format
	 * @throws IOException
	 */
	public SVMTrainer(int numAttributes, String posLabel, String negLabel, double C, double gamma,
			String dataFilePath, String outputParameterFilePath, String outputModelFilePath)
			throws IOException
	{
		this.numAttributes = numAttributes;
		this.posLabel = posLabel;
		this.negLabel = negLabel;
		this.C = C;
		this.gamma = gamma;
		readData(dataFilePath);
		preprocessData(outputParameterFilePath);
		train(outputModelFilePath);
	}

	protected void train(String outputModelFilePath) throws IOException
	{
		assert y.size() == x.size() : "data error: x.size() != y.size()";

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
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		// inputs
		param.C = C;
		param.gamma = gamma;

		model = svm.svm_train(prob, param);

		svm.svm_save_model(outputModelFilePath, model);
	}

	protected void preprocessData(String outputParameterFilePath) throws IOException
	{
		double[] means = new double[numAttributes];
		double[] stds = new double[numAttributes];

		for (int i = 0; i < numAttributes; ++i)
		{
			means[i] = getMean(x, i);
			stds[i] = getStd(x, i, means[i]);
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputParameterFilePath));
		bw.write("means:");
		for (int i = 0; i < numAttributes; ++i)
			bw.write((i == 0 ? "" : ",") + means[i]);
		bw.newLine();
		bw.write("stds:");
		for (int i = 0; i < numAttributes; ++i)
			bw.write((i == 0 ? "" : ",") + stds[i]);
		bw.newLine();
		bw.close();
	}

	protected double getStd(List<svm_node[]> instances, int i, double mean)
	{
		double sum_sqr = 0;
		for (svm_node[] instance : instances)
		{
			double d = instance[i].value - mean;
			sum_sqr += d * d;
		}
		return sum_sqr / instances.size();
	}

	protected double getMean(List<svm_node[]> instances, int i)
	{
		double sum = 0;
		for (svm_node[] instance : instances)
		{
			sum += instance[i].value;
		}
		return sum / instances.size();
	}

	protected void readData(String dataFilePath) throws IOException
	{
		y = new ArrayList<Double>();
		x = new ArrayList<svm_node[]>();
		BufferedReader br = new BufferedReader(new FileReader(dataFilePath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			int sharp = line.indexOf('#');
			if (sharp >= 0)
			{
				line = line.substring(0, sharp);
			}
			line = line.trim();
			if (line.isEmpty())
				continue;

			String[] parts = line.split(",");
			assert parts.length == numAttributes + 1 : "unknown format: " + line;

			if (parts[0].equals(posLabel))
				y.add(1.0);
			else if (parts[0].equals(negLabel))
				y.add(-1.0);
			else
				y.add(0.0);

			svm_node[] newInstance = new svm_node[numAttributes];
			for (int i = 0; i < numAttributes; ++i)
			{
				newInstance[i] = new svm_node();
				newInstance[i].index = i + 1;
				newInstance[i].value = Double.valueOf(parts[i + 1]);
			}
			x.add(newInstance);
		}
		br.close();
	}

	public static void main(String[] args) throws IOException
	{
		SVMTrainer t = new SVMTrainer(3, "true", "false", 8, 2, "data/disambi.qmlt.dat",
				"model/disambi.gaussian_normalization.params", "model/disambi.svm.model");
	}

}
