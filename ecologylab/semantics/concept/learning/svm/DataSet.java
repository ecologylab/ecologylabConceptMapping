package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class DataSet extends Debug
{
	
	protected static class LineStruct
	{
		public int label;
		public svm_node[] feature;
		public String comment;
	}
	
	private List<Integer> labels = new ArrayList<Integer>();
	private List<svm_node[]> features = new ArrayList<svm_node[]>();
	private List<String> comments = new ArrayList<String>();
	
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
			int n = (int)(size * ratio);
			
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
	
	/**
	 * read a line and convert it to a LineStruct object. the line must be in format of
	 * "label,feature1,feature2,... # comments".
	 * 
	 * @param line
	 * @param buf
	 *          a buffer storing resulting LineStruct. must be initialized. null indicates specific
	 *          structure is not needed -- only return the label.
	 * @return the class label of this line. 0 indicates an ignored line.
	 */
	public static int readALine(String line, LineStruct buf)
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
			return 0; 

		String[] parts = line.split(",");
		int label = Integer.parseInt(parts[0]);

		if (buf != null)
		{
			buf.label = label;
			buf.feature = new svm_node[parts.length - 1];
			for (int i = 0; i < buf.feature.length; ++i)
			{
				buf.feature[i] = new svm_node();
				buf.feature[i].index = i + 1;
				buf.feature[i].value = Double.valueOf(parts[i + 1]);
			}
			buf.comment = comment;
		}

		return label;
	}
	
	public static DataSet read(String filepath) throws IOException
	{
		DataSet ds = new DataSet();
		ds.debug("reading data from " + filepath + " ...");
		
		BufferedReader in = new BufferedReader(new FileReader(filepath));
		String line = null;
		while ((line = in.readLine()) != null)
		{
			LineStruct ls = new LineStruct();
			readALine(line, ls);
			ds.labels.add(ls.label);
			ds.features.add(ls.feature);
			ds.comments.add(ls.comment);
		}
		in.close();
			 
		return ds;
	}
	
	@Test
	public void test() throws IOException
	{
		DataSet ds = DataSet.read("data/disambi-training-balanced.dat");
		DataSet[] dss = ds.randomSplit(0.5, 0.1, 0.4);
		dss[0].save("data/disambi-trainset.dat");
		dss[1].save("data/disambi-validateset.dat");
		dss[2].save("data/disambi-testset.dat");
	}

}
