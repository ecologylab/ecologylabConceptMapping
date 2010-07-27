package ecologylab.semantics.concept.wikipedia;

import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.example.MyInfoCollector;
import ecologylab.serialization.TranslationScope;

public class WikiInfoCollector extends MyInfoCollector
{

	public WikiInfoCollector(MetaMetadataRepository repo, TranslationScope metadataTranslationScope)
	{
		super(repo, metadataTranslationScope);
	}

	@Override
	public SemanticActionHandler createSemanticActionHandler()
	{
		return new WikiSemanticActionHandler();
	}

}
