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
	protected class LineStruct
	{
		public String head;
		public double[] values;
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
			LineStruct ls = processLine(line);
			if (ls.head.equals("means"))
				means = ls.values;
			else if (ls.head.equals("stds"))
				stds = ls.values;
		}
		br.close();
	}

	/**
	 * Calculate normalization parameters given a collection of instances.
	 * 
	 * @param x
	 */
	public void generateParameters(List<svm_node[]> x)
	{

		for (int i = 0; i < numAttributes; ++i)
		{
			means[i] = getMean(x, i);
			stds[i] = getStd(x, i, means[i]);
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
	 * Do normalization on an existing instance. The previous values will be replaced.
	 * 
	 * @param instance
	 * @return
	 */
	public void normalize(svm_node[] instance)
	{
		for (int i = 0; i < instance.length; ++i)
		{
			assert stds[i] != 0 : "zero standard deviation at index " + i;
			instance[i].value = (instance[i].value - means[i]) / stds[i];
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

	protected LineStruct processLine(String line)
	{
		LineStruct ls = new LineStruct();
		
		String[] parts1 = line.split(":");
		assert parts1.length == 2 : "unknown line format: " + line;
		ls.head = parts1[0];
		String[] parts2 = parts1[1].split(",");
		ls.values = new double[parts2.length];
		for (int i = 0; i < parts2.length; ++i)
		{
			ls.values[i] = Double.valueOf(parts2[i]);
		}
		return ls;
	}

}
