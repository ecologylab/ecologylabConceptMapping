package ecologylab.semantics.concept.train;

import ecologylab.generic.DispatchTarget;
import ecologylab.net.ParsedURL;
import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.example.MyContainer;
import ecologylab.semantics.metametadata.example.MyInfoCollector;
import ecologylab.semantics.seeding.Seed;
import ecologylab.serialization.TranslationScope;

public class WikiInfoCollectorForTraining extends MyInfoCollector implements DispatchTarget
{

	public static final int	MAX_INTERVAL_BETWEEN_PROCESSING	= 5000;
	private Object	processingLock	= new Object();

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

	@Override
	public void delivery(Object o)
	{
		synchronized (processingLock)
		{
			processingLock.notify();
		}
	}

	@Override
	public MyContainer getContainerDownloadIfNeeded(MyContainer ancestor, ParsedURL purl, Seed seed,
			boolean dnd, boolean justCrawl, boolean justMedia)
	{
		synchronized (processingLock)
		{
			debug("sending url to downloadMonitor: " + purl);
			MyContainer container = super.getContainerDownloadIfNeeded(ancestor, purl, seed, dnd,
					justCrawl, justMedia, this);
			try
			{
				processingLock.wait(MAX_INTERVAL_BETWEEN_PROCESSING);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return container;
		}
	}

}
