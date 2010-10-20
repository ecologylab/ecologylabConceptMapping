package ecologylab.semantics.concept.detect;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import libsvm.svm_node;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.learning.svm.SVMGaussianNormalization;
import ecologylab.semantics.concept.learning.svm.SVMPredicter;
import ecologylab.semantics.concept.learning.svm.Utils;

public class Disambiguator
{

	// TODO
	public static final double				threshold1	= 0;

	// TODO
	public static final double				threshold2	= 0;

	private Doc												doc;

	private Context										context;

	private Set<Surface>							candidateSurfaces;

	private Set<Instance>							resultInstances;

	private SVMGaussianNormalization	normalizer;

	private SVMPredicter							predictor;

	public Disambiguator() throws IOException
	{
		normalizer = new SVMGaussianNormalization(ConceptConstants.DETECT_PARAM_FILE_PATH);
		predictor = new SVMPredicter(ConceptConstants.DETECT_MODEL_FILE_PATH);
	}

	public Set<Instance> disambiguate(Doc doc)
	{
		if (resultInstances == null)
		{
			this.doc = doc;
			context = new Context();
			candidateSurfaces = new HashSet<Surface>();

			resultInstances = disambiguate();
		}
		return resultInstances;
	}

	private Set<Instance> disambiguate()
	{
		HashSet<Instance> rst = new HashSet<Instance>();

		// init
		for (Surface surface : doc.getUnambiSurfaces())
		{
			context.addConcept((Concept) surface.getSenses().toArray()[0], surface);
		}

		for (Surface surface : doc.getAmbiSurfaces())
		{
			candidateSurfaces.add(surface);
		}

		if (context.size() == 0)
		{
			// TODO no unambiguous surfaces? do a best guess ...
		}

		while (candidateSurfaces.size() > 0)
		{
			// find related surfaces
			Set<Surface> relatedSurfaces = new HashSet<Surface>();
			for (Surface surface : candidateSurfaces)
			{
				if (isRelatedSurface(surface, context))
				{
					relatedSurfaces.add(surface);
				}

			}

			if (relatedSurfaces.size() == 0)
			{
				// TODO no related surfaces? find the most related one ...
			}

			Set<Instance> disambiguated = new HashSet<Instance>();
			for (Surface surface : relatedSurfaces)
			{
				try
				{
					Instance instance = disambiguate(surface, context);
					if (instance.disambiguationConfidence > threshold2)
					{
						disambiguated.add(instance);
					}
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (disambiguated.size() == 0)
			{
				// TODO no surfaces are disambiguated confidently enough? find the most confident one ...
			}

			for (Instance instance : disambiguated)
			{
				Concept concept = instance.disambiguatedConcept;
				context.addConcept(concept, instance.surface);
				candidateSurfaces.remove(instance.surface);
			}
		}

		return rst;
	}

	private boolean isRelatedSurface(Surface surface, Context context)
	{
		Set<Concept> senses = surface.getSenses();
		for (Concept concept : senses)
		{
			for (Concept c : context.getConcepts())
			{
				double rel = c.getRelatedness(concept);
				if (rel > threshold1)
				{
					return true;
				}
			}
		}
		return false;
	}

	private Instance disambiguate(Surface surface, Context context) throws IOException
	{
		Instance bestInst = null;

		for (Concept sense : surface.getSenses())
		{
			Instance inst = Instance.get(doc, context, surface, sense);
			svm_node[] svmInst = Utils.constructSVMInstance(
					inst.commonness,
					inst.contextualRelatedness,
					inst.contextQuality
					);
			normalizer.normalize(svmInst);
			Map<Integer, Double> buf = new HashMap<Integer, Double>();
			predictor.predict(svmInst, buf);
			inst.disambiguationConfidence = buf.get(ConceptConstants.POS_CLASS_INT_LABEL);

			if (bestInst == null || inst.disambiguationConfidence > bestInst.disambiguationConfidence)
			{
				bestInst = inst;
			}
		}

		return bestInst;
	}

}
