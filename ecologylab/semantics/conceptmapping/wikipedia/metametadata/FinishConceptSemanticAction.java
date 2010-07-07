package ecologylab.semantics.conceptmapping.wikipedia.metametadata;

import java.util.Map;

import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionStandardMethods;
import ecologylab.semantics.conceptmapping.wikipedia.ConceptPool;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit
@xml_tag("finish_concept")
public class FinishConceptSemanticAction extends SemanticAction implements SemanticActionStandardMethods
{
	@Override
	public String getActionName()
	{
		return "finish_concept";
	}

	@Override
	public void handleError()
	{
	}

	@Override
	public Object handle(Object obj, Map<String, Object> args)
	{
		ConceptPool.get().endNewConcept();
		return null;
	}
}
