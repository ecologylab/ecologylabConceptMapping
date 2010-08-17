package ecologylab.semantics.concept.wikipedia.metametadata;

import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionStandardMethods;
import ecologylab.semantics.concept.wikipedia.ConceptPool;
import ecologylab.semantics.concept.wikipedia.StringPool;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit
@xml_tag("add_concept_outlink")
public class AddConceptOutlinkSemanticAction extends SemanticAction implements
		SemanticActionStandardMethods
{
	@Override
	public String getActionName()
	{
		return "add_concept_outlink";
	}

	@Override
	public void handleError()
	{
	}

	@Override
	public Object perform(Object obj)
	{
		String surface = (String) getArgumentObject("surface");
		String targetConcept = (String) getArgumentObject("target_concept");

		if (surface != null && targetConcept != null && !surface.isEmpty() && !targetConcept.isEmpty())
		{
			String linkS = String.format(
					"[esc:name \"%s\"] esc:linked_by [esc:name \"%s\"]; esc:surface \"%s\" .", targetConcept,
					ConceptPool.get().getCurrent().getName(), surface);
			StringPool.get("inlinks.n3").addLine(linkS);

			String surfaceS = String.format("[esc:words \"%s\"] esc:surface_of [esc:name \"%s\"] .",
					surface, targetConcept);
			StringPool.get("surfaces.n3").addLine(surfaceS);
		}

		return null;
	}
}
