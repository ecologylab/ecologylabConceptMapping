package ecologylab.semantics.conceptmapping.wikipedia.metametadata;

import ecologylab.semantics.tools.MetadataCompiler;
import ecologylab.xml.XMLTranslationException;

public class Compiler
{

	public static void main(String[] args) throws XMLTranslationException
	{
		MetadataCompiler compiler = new MetadataCompiler(args);

		Utils.addSemanticAction(CreateConceptSemanticAction.class, AddConceptOutlinkSemanticAction.class,
				AddConceptCategorySemanticAction.class, FinishConceptSemanticAction.class);

		compiler.compile(".", ".");
	}

}
