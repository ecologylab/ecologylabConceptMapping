package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.learning.DataSet;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class SvmDataSet extends Debug implements DataSet<svm_node[]>
{

	private int								dim						= 0;

	private List<Integer>			labels				= new ArrayList<Integer>();

	private List<svm_node[]>	instances			= new ArrayList<svm_node[]>();

	private List<String>			comments			= new ArrayList<String>();

	Map<Integer, Integer>			countByLabel	= new HashMap<Integer, Integer>();

	@Override
	public int getDimension()
	{
		return dim;
	}

	@Override
	public int getSize()
	{
		return getInstances().size();
	}

	@Override
	public List<Integer> getLabels()
	{
		return labels;
	}

	@Override
	public List<svm_node[]> getInstances()
	{
		return instances;
	}

	@Override
	public List<String> getComments()
	{
		return comments;
	}

	@Override
	public void add(int label, svm_node[] instance, String comment)
	{
		labels.add(label);
		instances.add(instance);
		comments.add(comment);
		incCount(label);
	}

	@Override
	public SvmDataSet[] randomSplit(double... ratios)
	{
		SvmDataSet[] rst = new SvmDataSet[ratios.length];

		int size = labels.size();
		assert size == instances.size() && size == comments.size() : "corrupted!";

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

			rst[i] = new SvmDataSet();
			for (int j = 0; j < n; ++j)
			{
				int k = p.get(s + j);
				rst[i].add(labels.get(k), instances.get(k), comments.get(k));
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
	@Override
	public void save(File outFile) throws IOException
	{
		assert labels.size() == instances.size() && instances.size() == comments.size() : "corrupted!";

		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
		for (int i = 0; i < labels.size(); ++i)
		{
			out.write(String.valueOf(labels.get(i)));
			for (svm_node featureNode : instances.get(i))
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
	
	private void incCount(int label)
	{
		int prevCount = countByLabel.containsKey(label) ? countByLabel.get(label) : 0;
		countByLabel.put(label, prevCount + 1);
	}

	/**
	 * load a DataSet from a file. must be in "label:feature1,feature2,... #comments" format.
	 * 
	 * @param dataSetFile
	 * @return
	 * @throws IOException
	 */
	public static SvmDataSet load(File dataSetFile) throws IOException
	{
		SvmDataSet ds = new SvmDataSet();
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

			if (ds.dim == 0)
			{
				ds.dim = parts.length - 1;
			}
			else
			{
				assert ds.dim == parts.length - 1 : "dimension error: " + line;
			}

			svm_node[] instance = new svm_node[ds.dim];
			for (int i = 0; i < ds.dim; ++i)
			{
				instance[i] = new svm_node();
				instance[i].index = i + 1;
				instance[i].value = Double.valueOf(parts[i + 1]);
			}

			ds.add(label, instance, comment);
		}
		in.close();

		return ds;
	}

	@Override
	public int getNumberOfSamplesWithLabel(int label)
	{
		return countByLabel.get(label);
	}

}
