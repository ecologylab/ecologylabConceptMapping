package ecologylab.semantics.concept.train;

import ecologylab.semantics.actions.ParseDocumentSemanticAction;
import ecologylab.semantics.actions.SemanticActionStandardMethods;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit
@xml_tag(SemanticActionStandardMethods.PARSE_DOCUMENT)
public class WikiParsingLinkHandler extends
		ParseDocumentSemanticAction<WikiInfoCollectorForTraining, WikiSemanticActionHandlerForTraining>
{

	@simpl_scalar
	private String	linkType	= "anchor";

	@Override
	public Object perform(Object obj)
	{
		if (linkType.equals("anchor"))
		{
			return performForAnchor(obj);
		}
		else if (linkType.equals("category"))
		{
			return performForCategory(obj);
		}
		else
		{
			return null;
		}
	}

	private Object performForAnchor(Object obj)
	{
		String surface = (String) getArgumentObject("surface");
		String concept = (String) getArgumentObject("target_title");

		if (surface != null && concept != null && !surface.isEmpty() && !concept.isEmpty())
			semanticActionHandler.context.add(surface, concept);

		return null;
	}

	private Object performForCategory(Object obj)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
