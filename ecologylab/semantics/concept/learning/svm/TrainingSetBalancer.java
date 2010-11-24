package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class TrainingSetBalancer
{
	
	private List<String>	posLines;

	private List<String>	negLines;

	private List<String>	primaryClassLines;

	private List<String>	secondaryClassLines;
	
	public void balance(String inFilepath, String outFilepath) throws IOException
	{
		posLines = new ArrayList<String>();
		negLines = new ArrayList<String>();
		
		BufferedReader in = new BufferedReader(new FileReader(inFilepath));
		String line = null;
		while ((line = in.readLine()) != null)
		{
			int label = Integer.parseInt(line.substring(0, line.indexOf(',')));
			if (label == ConceptConstants.POS_CLASS_INT_LABEL)
			{
				posLines.add(line);
			}
			else if (label == ConceptConstants.NEG_CLASS_INT_LABEL)
			{
				negLines.add(line);
			}
		}
		in.close();
		
		if (posLines.size() > negLines.size())
		{
			primaryClassLines = negLines;
			secondaryClassLines = posLines;
		}
		else
		{
			primaryClassLines = posLines;
			secondaryClassLines = negLines;
		}
		
		if (posLines.size() != negLines.size())
		{
			CollectionUtils.randomPermute(secondaryClassLines, primaryClassLines.size());
			secondaryClassLines = secondaryClassLines.subList(0, primaryClassLines.size());
		}
		
		List<String> all = new ArrayList<String>(primaryClassLines);
		all.addAll(secondaryClassLines);
		CollectionUtils.randomPermute(all);
		
		output(outFilepath, all);
	}
	
	private void output(String outFilepath, List<String>... lists) throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(outFilepath));
		for (List<String> list : lists)
		{
			for (String line : list)
			{
				out.write(line);
				out.newLine();
			}
		}
		out.close();
	}
	
	public static void main(String[] args)
	{
		TrainingSetBalancer tsb = new TrainingSetBalancer();
		try
		{
			tsb.balance("data/detect-all.dat", "data/detect-all-balanced.dat");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
