package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

import libsvm.svm_node;

import ecologylab.semantics.concept.Constants;
import ecologylab.semantics.concept.detect.Concept;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.detect.Surface;
import ecologylab.semantics.concept.learning.svm.LearningUtils;

public class DisambiguationTrainingSetPreparer extends TrainingSetPreparer
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

		Context context = doc.getContext();
		for (Surface surface : targetSurfaces)
		{
			for (Concept concept : surface.getSenses())
			{
				Instance inst = Instance.getForDisambiguation(context, surface, concept);
				boolean isTarget = false;
				if (concept.equals(doc.getLinkedSurfaces().get(surface)))
					isTarget = true;
				reportInstance(out, doc, inst, isTarget);
			}
		}
	}

	@Override
	protected void reportInstance(BufferedWriter out, WikiDoc doc, Instance instance, boolean isPositiveSample)
	{
		svm_node[] svmInst = LearningUtils.constructSVMInstanceForDisambiguation(instance);
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
