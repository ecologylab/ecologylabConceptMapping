package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

import libsvm.svm_node;

import ecologylab.semantics.concept.learning.Constants;
import ecologylab.semantics.concept.learning.svm.old.LearningUtils;
import ecologylab.semantics.concept.mapping.Context;
import ecologylab.semantics.concept.mapping.ExtractedSurface;
import ecologylab.semantics.concept.mapping.Surface;

public class DetectionTrainingSetPreparer extends TrainingSetPreparer
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
		Set<Surface> ambiSurfaces = doc.getAmbiSurfaces();
		for (Surface surface : ambiSurfaces)
		{
			try
			{
				if (surface == null)
				{
					System.err.println("warning: null (ambi) surface!");
					continue;
				}

				Context context = doc.getContext();
				ExtractedSurface inst = context.disambiguate(surface);
				if (inst == null)
				{
					System.err.println("warning: null instance!");
					continue;
				}
				if (inst.surface == null)
				{
					System.err.println("weird thing happening: inst.surface == null!");
					continue;
				}

				inst.keyphraseness = inst.surface.getKeyphraseness();
				inst.occurrence = doc.getNumberOfOccurrences(inst.surface);
				inst.frequency = ((double) inst.occurrence) / doc.getTotalWords();

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
			catch (Exception e)
			{
				System.err.println("EXCEPTION: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void reportInstance(BufferedWriter out, WikiDoc doc, ExtractedSurface instance, boolean isPositiveSample)
	{
		svm_node[] svmInst = LearningUtils.constructSVMInstanceForDetection(instance);
		StringBuilder sb = new StringBuilder();
		sb.append(isPositiveSample ? String.valueOf(Constants.POS_CLASS_INT_LABEL) : String
				.valueOf(Constants.NEG_CLASS_INT_LABEL));
		for (int i = 0; i < svmInst.length; ++i)
		{
			sb.append(",").append(String.valueOf(svmInst[i].value));
		}
		sb.append(" # ").append(doc.getTitle()).append(":");
		sb.append(instance.surface.word).append("->").append(instance.disambiguatedConcept.title);
		try
		{
			out.write(sb.toString());
			out.newLine();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
