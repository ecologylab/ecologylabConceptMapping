package ecologylab.semantics.concept.wikiparsing.passtwo;

import ecologylab.semantics.actions.ParseDocumentSemanticAction;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit
@xml_tag("parse_document")
public class CollectWikiLinkSemanticAction extends ParseDocumentSemanticAction
{

	@Override
	public Object perform(Object obj)
	{
		// TODO Auto-generated method stub
		return super.perform(obj);
	}

}
