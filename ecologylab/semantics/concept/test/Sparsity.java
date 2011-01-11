package ecologylab.semantics.concept.test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import ecologylab.semantics.concept.ConceptTrainingConstants;
import ecologylab.semantics.concept.detect.Concept;
import ecologylab.semantics.concept.utils.TextUtils;

public class Sparsity
{

	private static final double	epsilon	= 0.0000000001;

	public int calcSparsity(int n) throws IOException
	{
		
		File primaryConceptListFile = new File(ConceptTrainingConstants.PRIMARY_CONCEPTS_FILE_PATH);
		List<String> primaryConcepts = TextUtils.loadTxtAsList(primaryConceptListFile, false);
		Collections.shuffle(primaryConcepts);
		List<String> concepts = primaryConcepts.subList(0, n);
		
		int nonZero = 0;
		int counter = 0;
		for (int i = 0; i < n; ++i)
		{
			for (int j = i + 1; j < n; ++j)
			{
				Concept c1 = new Concept(concepts.get(i));
				Concept c2 = new Concept(concepts.get(j));
				double relatedness = c1.getRelatedness(c2);
				if (relatedness > epsilon)
				{
					nonZero++;
				}
				
				counter++;
				System.err.println("processed: " + counter);
			}
		}
		
		return nonZero;
	}
	
	public static void main(String[] args) throws IOException
	{
		System.err.println("args: <num-of-concepts>");
		int n = 1000;
		if (args.length == 1)
		{
			n = Integer.parseInt(args[0]);
		}
		
		Sparsity s = new Sparsity();
		int nonZero = s.calcSparsity(n);
		System.out.println("non zero relatedness pairs: " + nonZero);
		System.out.println("sparsity: " + nonZero * 2.0 / n / n);
	}
	
}
