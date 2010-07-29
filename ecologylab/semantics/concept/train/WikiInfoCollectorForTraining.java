package ecologylab.semantics.concept.train;

import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.example.MyInfoCollector;
import ecologylab.serialization.TranslationScope;

public class WikiInfoCollectorForTraining extends MyInfoCollector
{

	public WikiInfoCollectorForTraining(MetaMetadataRepository repo,
			TranslationScope metadataTranslationScope)
	{
		super(repo, metadataTranslationScope);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SemanticActionHandler createSemanticActionHandler()
	{
		return new WikiSemanticActionHandlerForTraining();
	}

}
