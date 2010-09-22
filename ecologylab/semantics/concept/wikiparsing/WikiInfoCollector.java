package ecologylab.semantics.concept.wikiparsing;

import ecologylab.concurrent.DownloadMonitor;
import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.example.MyInfoCollector;
import ecologylab.serialization.TranslationScope;

public class WikiInfoCollector extends MyInfoCollector
{
	
	public static interface SemanticActionHandlerFactory
	{
		SemanticActionHandler create();
	}

	private SemanticActionHandlerFactory	handlerFactory;
	
	private int numDownloadThreads;

	public WikiInfoCollector(MetaMetadataRepository repo, TranslationScope metadataTranslationScope,
			SemanticActionHandlerFactory semanticActionHandlerFactory, int numDownloadThreads)
	{
		super(repo, metadataTranslationScope);
		handlerFactory = semanticActionHandlerFactory;
		this.numDownloadThreads = numDownloadThreads;
	}

	@Override
	public SemanticActionHandler createSemanticActionHandler()
	{
		return handlerFactory.create();
	}

	@Override
	public DownloadMonitor getDownloadMonitor()
	{
		if (downloadMonitor == null)
			downloadMonitor = new DownloadMonitor("wiki-info-collector", numDownloadThreads);
		return downloadMonitor;
	}

}
