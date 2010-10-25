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
				Instance instance = context.disambiguate(surface);
				if (instance.disambiguationConfidence > ConceptConstants.threshold2)
				{
					disambiguated.add(instance);
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
				if (rel > ConceptConstants.threshold1)
				{
					return true;
				}
			}
		}
		return false;
	}

}
