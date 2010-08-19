package ecologylab.semantics.concept.learning.svm;

import java.util.ArrayList;
import java.util.List;

import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class DataSet extends Debug
{
	
	protected class LineStruct
	{
		public int label;
		public svm_node[] feature;
		public String comment;
	}
	
	private List<Integer> labels;
	private List<svm_node[]> features;
	private List<String> comments;
	
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
	
	public int readALine(String line, LineStruct buf)
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
				int k = s + j;
				rst[i].labels.add(labels.get(k));
				rst[i].features.add(features.get(k));
				rst[i].comments.add(comments.get(k));
			}
			s += n;
		}
		
		return rst;
	}
	
	public void save(String filepath)
	{
		// TODO
		return;
	}
	
	public static DataSet read(String filepath)
	{
		// TODO
		return null;
	}
	
}
