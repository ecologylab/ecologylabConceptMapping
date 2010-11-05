package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import ecologylab.semantics.concept.ConceptTrainingConstants;
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
			long t1 = System.currentTimeMillis();
			for (Concept concept : surface.getSenses())
			{
				long t2 = System.currentTimeMillis();
				Instance inst = Instance.getForDisambiguation(doc.getContext(), surface, concept);
				boolean isTarget = false;
				if (concept.equals(doc.getLinkedSurfaces().get(surface)))
					isTarget = true;
				reportInstance(out, doc, inst, isTarget);
				long senseTime = System.currentTimeMillis() - t2;
				System.out.println("doc.surface.sense time: " + senseTime);
				if (senseTime > 1000)
				{
					System.out.println(doc.getTitle() + "/" + surface + "/" + concept);
				}
			}
			System.out.println("doc.surface time: " + (System.currentTimeMillis() - t1));
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
		DisambiguationTrainingSetPreparer preparer = new DisambiguationTrainingSetPreparer()
		{
			@Override
			protected void reportInstance(BufferedWriter out, WikiDoc doc, Instance instance,
					boolean isPositiveSample)
			{
				reportDisambiguationInstance(out, doc, instance, isPositiveSample);
			}
		};
		prepare(ConceptTrainingConstants.DISAMBI_TRAINING_SET_FILE_PATH, preparer);
	}

}
