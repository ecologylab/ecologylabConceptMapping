package ecologylab.semantics.conceptmapping.wikipedia.metametadata;

import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.tools.MetadataCompiler;
import ecologylab.xml.XMLTranslationException;

public class Compiler
{

	public static void main(String[] args) throws XMLTranslationException
	{
		SemanticAction.register(CreateConceptSemanticAction.class, AddConceptOutlinkSemanticAction.class,
				AddConceptCategorySemanticAction.class, FinishConceptSemanticAction.class);

		MetadataCompiler compiler = new MetadataCompiler(args);
		compiler.compile(".", ".");
	}

}
