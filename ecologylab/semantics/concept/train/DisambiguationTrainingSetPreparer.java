package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
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

}
