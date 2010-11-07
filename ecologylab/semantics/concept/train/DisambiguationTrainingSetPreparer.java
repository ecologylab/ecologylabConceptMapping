package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import ecologylab.semantics.concept.detect.Concept;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.detect.Surface;

public abstract class DisambiguationTrainingSetPreparer extends TrainingSetPreparer
{

	/**
	 * find those ambiguous, linked surfaces from a wikipedia article, extract features for them
	 * (using the context consists of unambiguous and linked surfaces), convert them into instances
	 * and report.
	 * 
	 * @param doc
	 * @param out
	 */
	@Override
	public void prepare(WikiDoc doc, BufferedWriter out)
	{
		// feature extraction
		Set<Surface> targetSurfaces = doc.getAmbiSurfaces();
		targetSurfaces.retainAll(doc.getLinkedSurfaces().keySet());
		for (Surface surface : targetSurfaces)
		{
			for (Concept concept : surface.getSenses())
			{
				Instance inst = Instance.getForDisambiguation(doc.getContext(), surface, concept);
				boolean isTarget = false;
				if (concept.equals(doc.getLinkedSurfaces().get(surface)))
					isTarget = true;
				reportInstance(out, doc, inst, isTarget);
			}
		}
	}

	public static void reportDisambiguationInstance(BufferedWriter out, WikiDoc doc,
			Instance instance, boolean isPositiveSample)
	{
		String line = String.format("%d,%f,%f,%f # %s:%s->%s",
						isPositiveSample ? 1 : -1,
						instance.commonness,
						instance.contextualRelatedness,
						instance.contextQuality,
						doc.getTitle(),
						instance.surface.word,
						instance.disambiguatedConcept.title
						);
		try
		{
			out.write(line);
			out.newLine();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		if (args.length != 2)
		{
			System.err.println("args: <in:title-list-file-path> <out:result-train-set-file-path>");
			System.exit(-1);
		}
		
		String infp = args[0];
		String oufp = args[1];
		
		File inf = new File(infp);
		File ouf = new File(oufp);
		
		DisambiguationTrainingSetPreparer preparer = new DisambiguationTrainingSetPreparer()
		{
			@Override
			protected void reportInstance(BufferedWriter out, WikiDoc doc, Instance instance,
					boolean isPositiveSample)
			{
				reportDisambiguationInstance(out, doc, instance, isPositiveSample);
			}
		};
		prepare(inf, ouf, preparer);
	}

}
