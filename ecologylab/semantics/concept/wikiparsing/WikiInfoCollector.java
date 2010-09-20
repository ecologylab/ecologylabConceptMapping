package ecologylab.semantics.concept.wikiparsing;

import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.example.MyInfoCollector;
import ecologylab.serialization.TranslationScope;

public class WikiInfoCollector extends MyInfoCollector<WikiPageContainer>
{
	
	public static interface SemanticActionHandlerFactory
	{
		SemanticActionHandler create();
	}

	private SemanticActionHandlerFactory	handlerFactory;

	public WikiInfoCollector(MetaMetadataRepository repo, TranslationScope metadataTranslationScope,
			SemanticActionHandlerFactory semanticActionHandlerFactory)
	{
		super(repo, metadataTranslationScope);
		handlerFactory = semanticActionHandlerFactory;
	}

	@Override
	public SemanticActionHandler createSemanticActionHandler()
	{
		return handlerFactory.create();
	}

}
