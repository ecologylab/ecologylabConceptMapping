package ecologylab.semantics.concept.wikiparsing.metametadata;

import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.tools.MetadataCompiler;
import ecologylab.serialization.SIMPLTranslationException;

public class Compiler
{

	public static void main(String[] args) throws SIMPLTranslationException
	{
		SemanticAction.register(CreateConceptSemanticAction.class, AddConceptOutlinkSemanticAction.class,
				AddConceptCategorySemanticAction.class, FinishConceptSemanticAction.class);

		MetadataCompiler compiler = new MetadataCompiler(args);
		compiler.compile(".", ".");
	}

}
