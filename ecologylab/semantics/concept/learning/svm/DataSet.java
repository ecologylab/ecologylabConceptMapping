package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class DataSet extends Debug
{

	private int								dim				= 0;

	private List<Integer>			labels		= new ArrayList<Integer>();

	private List<svm_node[]>	features	= new ArrayList<svm_node[]>();

	private List<String>			comments	= new ArrayList<String>();

	private int								nPos			= 0;

	private int								nNeg			= 0;

	public int getDimension()
	{
		return dim;
	}

	public List<Integer> getLabels()
	{
		return labels;
	}

	public List<svm_node[]> getFeatures()
	{
		return features;
	}

	public List<String> getComments()
	{
		return comments;
	}

	public int getNumberOfPositiveSamples()
	{
		return nPos;
	}

	public int getNumberOfNegativeSamples()
	{
		return nNeg;
	}

	/**
	 * load a DataSet from a file. must be in "label:feature1,feature2,... #comments" format.
	 * 
	 * @param dataSetFile
	 * @return
	 * @throws IOException
	 */
	public static DataSet load(File dataSetFile) throws IOException
	{
		DataSet ds = new DataSet();
		ds.debug("reading data from " + dataSetFile.getAbsolutePath() + " ...");

		BufferedReader in = new BufferedReader(new FileReader(dataSetFile));
		String line = null;
		while ((line = in.readLine()) != null)
		{
			String comment = null;
			int sharp = line.indexOf('#');
			if (sharp >= 0)
			{
				comment = line.substring(sharp + 1);
				line = line.substring(0, sharp);
			}
			line = line.trim();
			if (line.isEmpty())
				continue;

			String[] parts = line.split(",");
			int label = Integer.parseInt(parts[0]);
			if (label == ConceptConstants.POS_CLASS_INT_LABEL)
				ds.nPos++;
			else if (label == ConceptConstants.NEG_CLASS_INT_LABEL)
				ds.nNeg++;

			if (ds.dim == 0)
			{
				ds.dim = parts.length - 1;
			}
			else
			{
				assert ds.dim == parts.length - 1 : "format error: " + line;
			}

			svm_node[] feature = new svm_node[ds.dim];
			for (int i = 0; i < ds.dim; ++i)
			{
				feature[i] = new svm_node();
				feature[i].index = i + 1;
				feature[i].value = Double.valueOf(parts[i + 1]);
			}

			ds.labels.add(label);
			ds.features.add(feature);
			ds.comments.add(comment);
		}
		in.close();

		return ds;
	}

	/**
	 * randomly split a DataSet into several ones, according to the given ratios.
	 * 
	 * @param ratios
	 * @return
	 */
	public DataSet[] randomSplit(double... ratios)
	{
		DataSet[] rst = new DataSet[ratios.length];

		int size = labels.size();
		assert size == features.size() && size == comments.size();

		List<Integer> p = new ArrayList<Integer>(size);
		for (int i = 0; i < size; ++i)
		{
			p.add(i);
		}
		CollectionUtils.randomPermute(p);

		int s = 0;
		for (int i = 0; i < ratios.length; ++i)
		{
			double ratio = ratios[i];
			int n = (int) (size * ratio);

			rst[i] = new DataSet();
			for (int j = 0; j < n; ++j)
			{
				int k = p.get(s + j);
				rst[i].labels.add(labels.get(k));
				rst[i].features.add(features.get(k));
				rst[i].comments.add(comments.get(k));
			}
			s += n;
		}

		return rst;
	}

	/**
	 * save the DataSet into a file.
	 * 
	 * @param filepath
	 * @throws IOException
	 */
	public void save(String filepath) throws IOException
	{
		assert labels.size() == features.size() && features.size() == comments.size();

		BufferedWriter out = new BufferedWriter(new FileWriter(filepath));
		for (int i = 0; i < labels.size(); ++i)
		{
			out.write(String.valueOf(labels.get(i)));
			for (svm_node featureNode : features.get(i))
			{
				out.write(",");
				out.write(String.valueOf(featureNode.value));
			}
			String comment = comments.get(i);
			if (comment != null)
			{
				out.write(" # ");
				out.write(comment);
			}
			out.newLine();
		}
		out.close();
	}

	// for test only
	public static void main(String[] args) throws IOException
	{
		DataSet ds = DataSet.load(new File("data/detect-training-balanced.dat"));
		DataSet[] dss = ds.randomSplit(0.5, 0.5);
		dss[0].save("data/detect-trainset.dat");
		dss[1].save("data/detect-testset.dat");
	}

}
