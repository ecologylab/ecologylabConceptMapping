package ecologylab.semantics.concept.detect;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ecologylab.semantics.concept.ConceptConstants;

/**
 * internal class used for disambiguation.
 * 
 * @author quyin
 * 
 */
class Disambiguator
{

	private Doc	doc;

	public Disambiguator(Doc doc)
	{
		this.doc = doc;
	}

	public Set<Instance> disambiguate() throws IOException
	{
		HashSet<Instance> rst = new HashSet<Instance>();

		// init
		Context context = new Context();
		for (Surface surface : doc.getUnambiSurfaces())
		{
			context.addConcept((Concept) surface.getSenses().toArray()[0], surface);
		}
		Set<Surface> candidateSurfaces = new HashSet<Surface>();
		for (Surface surface : doc.getAmbiSurfaces())
		{
			candidateSurfaces.add(surface);
		}

		if (context.size() == 0)
		{
			// no unambiguous surfaces? do a best guess ...
			Surface s = findSurfaceWithLargestCommonness(doc);
			candidateSurfaces.add(s);
		}

		while (candidateSurfaces.size() > 0)
		{
			// find related surfaces
			Set<Surface> relatedSurfaces = new HashSet<Surface>();
			Surface bestRelatedOne = null;
			double bestRelatedOneRelatedness = 0;
			for (Surface surface : candidateSurfaces)
			{
				double relatedness = getRelatedness(surface, context);
				if (relatedness > ConceptConstants.threshold1)
				{
					relatedSurfaces.add(surface);
				}
				if (bestRelatedOne == null ||
						bestRelatedOneRelatedness < relatedness)
				{
					bestRelatedOne = surface;
					bestRelatedOneRelatedness = relatedness;
				}
			}

			if (relatedSurfaces.size() == 0)
			{
				// no related surfaces? find the most related one ...
				relatedSurfaces.add(bestRelatedOne);
			}
			if (relatedSurfaces.size() == 0)
			{
				// still no related surfaces, move on
				continue;
			}

			Set<Instance> disambiguated = new HashSet<Instance>();
			Instance bestConfidentOne = null;
			for (Surface surface : relatedSurfaces)
			{
				Instance instance = context.disambiguate(surface);
				if (instance.disambiguationConfidence > ConceptConstants.threshold2)
				{
					disambiguated.add(instance);
				}
				if (bestConfidentOne == null ||
							bestConfidentOne.disambiguationConfidence < instance.disambiguationConfidence)
					bestConfidentOne = instance;
			}

			if (disambiguated.size() == 0)
			{
				// no surfaces are disambiguated confidently enough? find the most confident one ...
				disambiguated.add(bestConfidentOne);
			}
			if (disambiguated.size() == 0)
			{
				// still no disambiguated surfaces, move on
				continue;
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

	private double getRelatedness(Surface surface, Context context)
	{
		double rst = 0;
		Set<Concept> senses = surface.getSenses();
		for (Concept concept : senses)
		{
			for (Concept c : context.getConcepts())
			{
				double rel = c.getRelatedness(concept);
				if (rel > rst)
					rst = rel;
			}
		}
		return rst;
	}

	private Surface findSurfaceWithLargestCommonness(Doc theDoc)
	{
		Surface best = null;
		double bestCommonness = 0;
		
		for (Surface s : theDoc.getAmbiSurfaces())
		{
			for (Concept c : s.getSenses())
			{
				double commonness = s.getCommonness(c);
				if (best == null ||
						bestCommonness < commonness)
				{
					best = s;
					bestCommonness = commonness;
				}
			}
		}
		
		return best;
	}

}
