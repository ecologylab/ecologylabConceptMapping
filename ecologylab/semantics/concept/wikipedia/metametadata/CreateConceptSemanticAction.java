package ecologylab.semantics.concept.wikipedia.metametadata;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionStandardMethods;
import ecologylab.semantics.concept.wikipedia.ConceptPool;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit
@xml_tag("create_concept")
public class CreateConceptSemanticAction extends SemanticAction implements
		SemanticActionStandardMethods
{
	@Override
	public String getActionName()
	{
		return "create_concept";
	}

	@Override
	public void handleError()
	{
	}

	@Override
	public Object perform(Object obj)
	{
		String title = (String) getArgumentObject("title");
		ParsedURL purl = (ParsedURL) getArgumentObject("location");

		ConceptPool.get().beginNewConcept(title, purl);
		return null;
	}
}
