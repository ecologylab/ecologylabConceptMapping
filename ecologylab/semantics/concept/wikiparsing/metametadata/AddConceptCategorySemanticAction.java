package ecologylab.semantics.concept.wikiparsing.metametadata;

import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionStandardMethods;
import ecologylab.semantics.concept.wikiparsing.ConceptPool;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit
@xml_tag("add_concept_category")
public class AddConceptCategorySemanticAction extends SemanticAction implements
		SemanticActionStandardMethods
{

	@Override
	public String getActionName()
	{
		return "add_concept_category";
	}

	@Override
	public void handleError()
	{
	}

	@Override
	public Object perform(Object obj)
	{
		String categoryName = (String) getArgumentObject("category");
		
		ConceptPool.get().addCategory(categoryName);
		return null;
	}
}
