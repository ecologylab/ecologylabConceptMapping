package ecologylab.semantics.conceptmapping.wikipedia;

import ecologylab.semantics.actions.NestedSemanticActionsTranslationScope;
import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.MetaMetadataTranslationScope;
import ecologylab.semantics.tools.MetadataCompiler;
import ecologylab.xml.TranslationScope;
import ecologylab.xml.XMLTranslationException;

public class Preparation
{

	/**
	 * @param args
	 * @throws XMLTranslationException
	 */
	public static void main(String[] args) throws XMLTranslationException
	{
		MetadataCompiler compiler = new MetadataCompiler(args);

		addSemanticAction(CreateConceptOutlinkSemanticAction.class, AnalyzeParagraphSemanticAction.class);

		compiler.compile(".", ".");
	}

	public static void addSemanticAction(Class<? extends SemanticAction>... semanticActionClasses)
	{
		for (Class sac : semanticActionClasses)
		{
			MetaMetadataTranslationScope.get().addTranslation(sac);
			NestedSemanticActionsTranslationScope.get().addTranslation(sac);
		}
	}

}
