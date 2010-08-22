package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import libsvm.svm_node;

public class SVMGaussianNormalization
{
	protected static class LineStruct
	{
	}

	protected int				numAttributes;

	protected double[]	means;

	protected double[]	stds;

	/**
	 * Construct a normalization object using given number of attributes. Typically used when you want
	 * to calculate normalization parameters.
	 * 
	 * @param numAttributes
	 */
	public SVMGaussianNormalization(int numAttributes)
	{
		this.numAttributes = numAttributes;
		means = new double[numAttributes];
		stds = new double[numAttributes];
	}

	/**
	 * Construct a normalization object using saved parameter file. Typically used when you do
	 * normalization at runtime (predicting phase).
	 * 
	 * @param parameterFilePath
	 * @throws IOException
	 */
	public SVMGaussianNormalization(String parameterFilePath) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(parameterFilePath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] parts1 = line.split(":");
			assert parts1.length == 2 : "unknown line format: " + line;
			String head = parts1[0];
			String[] parts2 = parts1[1].split(",");
			double[] values = new double[parts2.length];
			for (int i = 0; i < parts2.length; ++i)
			{
				values[i] = Double.valueOf(parts2[i]);
			}
			if (head.equals("means"))
				means = values;
			else if (head.equals("stds"))
				stds = values;
		}
		br.close();
		assert means.length == stds.length : "errors in parameters.";
		numAttributes = means.length;
	}

	/**
	 * Calculate normalization parameters given a dataset.
	 * 
	 * @param dataset
	 */
	public void generateParameters(DataSet dataset)
	{

		for (int i = 0; i < dataset.getDimension(); ++i)
		{
			means[i] = getMean(dataset.getFeatures(), i);
			stds[i] = getStd(dataset.getFeatures(), i, means[i]);
		}
	}

	/**
	 * Save calculated normalization parameters for future use.
	 * 
	 * @param outputParameterFilePath
	 * @throws IOException
	 */
	public void save(String outputParameterFilePath) throws IOException
	{
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

	/**
	 * normalize a dataset using calculated or loaded parameters. values in the dataset will be
	 * replaced.
	 * 
	 * @param dataset
	 */
	public void normalize(DataSet dataset)
	{
		for (int i = 0; i < dataset.getFeatures().size(); ++i)
		{
			svm_node[] instance = dataset.getFeatures().get(i);
			normalize(instance);
		}
	}
	
	/**
	 * normalize an instance using calculated or loaded parameters. values in the instance will be
	 * replaced.
	 * 
	 * @param instance
	 */
	public void normalize(svm_node[] instance)
	{
		for (int j = 0; j < instance.length; ++j)
		{
			assert stds[j] != 0 : "zero standard deviation at index " + j;
			instance[j].value = (instance[j].value - means[j]) / stds[j];
		}
	}

	protected double getStd(List<svm_node[]> instances, int i, double mean)
	{
		double sum_sqr = 0;
		for (svm_node[] instance : instances)
		{
			double d = instance[i].value - mean;
			sum_sqr += d * d;
		}
		return Math.sqrt(sum_sqr / instances.size());
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

}
