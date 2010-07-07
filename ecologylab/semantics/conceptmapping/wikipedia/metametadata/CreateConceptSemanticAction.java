package ecologylab.semantics.conceptmapping.wikipedia.metametadata;

import java.util.ArrayList;
import java.util.Map;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionStandardMethods;
import ecologylab.semantics.conceptmapping.wikipedia.ConceptPool;
import ecologylab.xml.simpl_inherit;
import ecologylab.xml.ElementState.xml_tag;

@simpl_inherit
@xml_tag("create_concept")
public class CreateConceptSemanticAction extends SemanticAction implements SemanticActionStandardMethods
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
	public Object handle(Object obj, Map<String, Object> args)
	{
		String title = (String) args.get("title");
		ParsedURL purl = (ParsedURL) args.get("location");
		
		ConceptPool.get().beginNewConcept(title, purl);
		return null;
	}
}
