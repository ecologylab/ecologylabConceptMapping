package wikxplorer.model;

import java.util.List;
import java.util.Map;

import ecologylab.semantics.concept.database.orm.WikiConcept;

import wikxplorer.Server;
import wikxplorer.messages.Concept;
import wikxplorer.messages.Link;

/**
 * Abstraction of link suggestion algorithm.
 * 
 * @author quyin
 * 
 */
public abstract class LinkSuggestionStrategy
{

	static final double	WEIGHT_R0;

	static final double	WEIGHT_RC;

	static final double	WEIGHT_RA;

	static final double	MIN_DIST_THREASHOLD;

	static final int		RANDOM_LINK_NUMBER;

	static final int		MIN_LINKS_TO_RETURN;

	static
	{
		WEIGHT_R0 = Double.valueOf(Server.properties.getProperty("suggestion.weight_r0"));
		WEIGHT_RC = Double.valueOf(Server.properties.getProperty("suggestion.weight_rc"));
		WEIGHT_RA = Double.valueOf(Server.properties.getProperty("suggestion.weight_ra"));
		MIN_DIST_THREASHOLD = Double.valueOf(Server.properties.getProperty(
				"suggestion.min_dist_threashold"));
		RANDOM_LINK_NUMBER = Integer.valueOf(Server.properties.getProperty(
				"suggestion.random_link_number"));
		MIN_LINKS_TO_RETURN = Integer.valueOf(Server.properties
				.getProperty("suggestion.min_links_to_return"));
	}

	/**
	 * Given a concept and the context, suggest links. Generally, suggested links should not contain
	 * concepts already in the context. Implementations can specify orders of suggestions.
	 * 
	 * @param concept
	 *          The concept whose links are to be suggested.
	 * @param context
	 *          The context (in the form of a collection of concepts) that could be used in
	 *          suggestion.
	 * @return
	 */
	abstract public List<Link> suggestLinks(Concept concept, Map<String, Concept> context);

	protected double getContextuallyRelatedness(Map<String, Concept> context, WikiConcept concept,
			double r0)
	{
		double rc = WikiConcept.MAX_DIST; // min distance to contextual concepts
		double ra = 0; // average distance to contextual concepts
		for (Concept c : context.values())
		{
			if (c.wikiConcept.equals(concept))
				continue; // if concept is already in the context, don't compare with itself

			double rel = concept.getRelatedness(c.wikiConcept);
			if (rel < rc)
				rc = rel;
			ra += rel;
		}
		ra /= context.size();
		double r = r0 * WEIGHT_R0 + rc * WEIGHT_RC + ra * WEIGHT_RA;
		return r;
	}

}
