package ecologylab.semantics.concept.detect;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.service.Configs;

/**
 * internal class used for disambiguation.
 * 
 * @author quyin
 * 
 */
class Disambiguator
{

	private Doc			doc;

	private Context	context;

	public Disambiguator(Doc doc)
	{
		this.doc = doc;
		this.context = new Context(doc);
	}

	/**
	 * Disambiguate a Doc.
	 * 
	 * @throws IOException
	 */
	public void disambiguate() throws IOException
	{
		Set<Surface> unresolvedSurfaces = new HashSet<Surface>();
		for (Surface surface : doc.getSurfaces())
		{
			if (surface.getSenses().size() == 1)
			{
				context.add(surface, surface.getDisambiguatedConcept());
			}
			else
			{
				unresolvedSurfaces.add(surface);
			}
		}

		if (context.size() == 0)
		{
			// no unambiguous surfaces? do a best guess ...
			Surface s = findSurfaceWithLargestCommonnessAndDisambiguate(unresolvedSurfaces);
			context.add(s, s.getDisambiguatedConcept());
		}

		while (unresolvedSurfaces.size() > 0)
		{
			// find related surfaces
			Set<Surface> relatedSurfaces = new HashSet<Surface>();
			Surface bestRelatedOne = null;
			double bestRelatedOneRelatedness = 0;
			for (Surface surface : unresolvedSurfaces)
			{
				double relatedness = getRelatedness(surface);
				if (relatedness > Configs.getDouble("feature_extraction.related_surface_threshold"))
				{
					relatedSurfaces.add(surface);
				}
				if (bestRelatedOne == null || bestRelatedOneRelatedness < relatedness)
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

			Set<Surface> resolved = new HashSet<Surface>();
			Surface bestConfidentOne = null;
			for (Surface surface : relatedSurfaces)
			{
				context.disambiguate(surface);
				double disambiguationConfidence = surface.getDisambiguatedInstance().disambiguationConfidence;
				if (disambiguationConfidence > Configs
						.getDouble("feature_extraction.disambiguation_confidence_threshold"))
				{
					resolved.add(surface);
				}
				if (bestConfidentOne == null
						|| bestConfidentOne.getDisambiguatedInstance().disambiguationConfidence < disambiguationConfidence)
					bestConfidentOne = surface;
			}

			if (resolved.size() == 0)
			{
				// no surfaces are disambiguated confidently enough? find the most confident one ...
				resolved.add(bestConfidentOne);
			}

			for (Surface surface : resolved)
			{
				context.add(surface, surface.getDisambiguatedConcept());
				unresolvedSurfaces.remove(surface);
			}
		}
	}

	private double getRelatedness(Surface surface)
	{
		double rst = 0;
		Set<WikiConcept> senses = surface.getSenses().keySet();
		for (WikiConcept concept : senses)
		{
			double rel = context.getContextualRelatedness(concept);
			if (rel > rst)
				rst = rel;
		}
		return rst;
	}

	private Surface findSurfaceWithLargestCommonnessAndDisambiguate(Set<Surface> unresolvedSurfaces)
	{
		Surface best = null;
		double bestCommonness = 0;

		for (Surface s : unresolvedSurfaces)
		{
			for (WikiConcept c : s.getSenses().keySet())
			{
				double commonness = s.getSenses().get(c).commonness;
				if (best == null || bestCommonness < commonness)
				{
					best = s;
					bestCommonness = commonness;
				}
			}
		}

		return best;
	}

}
