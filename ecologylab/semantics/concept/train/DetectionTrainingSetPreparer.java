package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.IOException;
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

}
