package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import libsvm.svm_node;

public class GaussianNormalizer implements Normalizer
{

	private int				numAttributes;

	private double[]	means;

	private double[]	stds;

	@Override
	public void initialize(DataSet dataSet)
	{
		numAttributes = dataSet.getDimension();
		means = new double[numAttributes];
		stds = new double[numAttributes];
		for (int i = 0; i < numAttributes; ++i)
		{
			means[i] = getMean(dataSet.getFeatures(), i);
			stds[i] = getStd(dataSet.getFeatures(), i, means[i]);
		}
	}

	@Override
	public void load(File paramsPath) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(paramsPath));
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
		assert means.length == stds.length : "length of means and stds not matching!";
		numAttributes = means.length;
	}

	@Override
	public void normalize(svm_node[] instance)
	{
		for (int j = 0; j < instance.length; ++j)
		{
			assert stds[j] != 0 : "zero standard deviation at index " + j;
			instance[j].value = (instance[j].value - means[j]) / stds[j];
		}
	}

	@Override
	public void normalize(DataSet dataset)
	{
		for (int i = 0; i < dataset.getFeatures().size(); ++i)
		{
			svm_node[] instance = dataset.getFeatures().get(i);
			normalize(instance);
		}
	}

	@Override
	public void save(File paramsPath) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(paramsPath));
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

	private double getStd(List<svm_node[]> instances, int i, double mean)
	{
		double sum_sqr = 0;
		for (svm_node[] instance : instances)
		{
			double d = instance[i].value - mean;
			sum_sqr += d * d;
		}
		return Math.sqrt(sum_sqr / instances.size());
	}

	private double getMean(List<svm_node[]> instances, int i)
	{
		double sum = 0;
		for (svm_node[] instance : instances)
		{
			sum += instance[i].value;
		}
		return sum / instances.size();
	}

}
