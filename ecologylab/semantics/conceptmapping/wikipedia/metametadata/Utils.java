package ecologylab.semantics.conceptmapping.wikipedia.metametadata;

import ecologylab.semantics.actions.NestedSemanticActionsTranslationScope;
import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.metametadata.MetaMetadataTranslationScope;

public class Utils
{

	public static void addSemanticAction(Class<? extends SemanticAction>... semanticActionClasses)
	{
		for (Class sac : semanticActionClasses)
		{
			MetaMetadataTranslationScope.get().addTranslation(sac);
			NestedSemanticActionsTranslationScope.get().addTranslation(sac);
		}
	}

}
