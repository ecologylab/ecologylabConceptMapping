package ecologylab.semantics.conceptmapping.wikipedia.metametadata;

import java.util.Map;

import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionStandardMethods;
import ecologylab.semantics.conceptmapping.wikipedia.ConceptPool;
import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;

@xml_inherit
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
	public Object handle(Object obj, Map<String, Object> args)
	{
		String categoryName = (String) args.get("category");
		
		ConceptPool.get().addCategory(categoryName);
		return null;
	}
}
