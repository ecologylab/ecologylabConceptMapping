package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.detect.Surface;

public abstract class DetectionTrainingSetPreparer extends TrainingSetPreparer
{

	/**
	 * find all ambiguous surfaces. for each one, disambiguate it in the context consisting of
	 * unambiguous surfaces & linked concepts. linked ones are treated as positive samples, while
	 * unlinked ones negative. 
	 * 
	 * @param doc
	 * @param out
	 */
	@Override
	public void prepare(WikiDoc doc, BufferedWriter out)
	{
		//
		Set<Surface> ambiSurfaces=  doc.getAmbiSurfaces();
		for (Surface surface : ambiSurfaces)
		{
			try
			{
				Context context = doc.getContext();
				Instance inst = context.disambiguate(surface);
				if (doc.getLinkedSurfaces().containsKey(surface))
				{
					// linked, positive
					reportInstance(out, doc, inst, true);
				}
				else
				{
					// not linked, negative
					reportInstance(out, doc, inst, false);
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void reportDetectionInstance(BufferedWriter out, WikiDoc doc, Instance instance,
			boolean isPositiveSample)
	{
		String line = String.format("%d,%f,%f,%f,%f,%f,%f,%f  # %s:%s->%s",
						isPositiveSample ? 1 : -1,
						instance.commonness,
						instance.contextualRelatedness,
						instance.contextQuality,
						instance.disambiguationConfidence,
						instance.keyphraseness,
						instance.occurrence,
						instance.frequency,
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
				reportDetectionInstance(out, doc, instance, isPositiveSample);
			}
		};
		prepare(inf, ouf, preparer);
	}

}
