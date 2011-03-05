package ecologylab.semantics.concept.wikiparsing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ecologylab.semantics.concept.service.Configs;

/**
 * Handle wiki concepts parsed from XML dump with a bunch of threads.
 * 
 * The number of threads is indicated by pref 'prep.wiki_concept_handler.number_of_threads'.
 * 
 * @author quyin
 * 
 */
public class WikiConceptHandlerBunchParsing implements WikiConceptHandler
{

	private ExecutorService						pool;

	/**
	 * parser must be thread safe!!!
	 */
	private WikiConceptHandlerParsing	parser;

	private int												waitMs;

	public WikiConceptHandlerBunchParsing() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		int nThreads = Configs.getInt("prep.wiki_concept_handler.number_of_threads");
		pool = Executors.newFixedThreadPool(nThreads);
		parser = new WikiConceptHandlerParsing();
		waitMs = Configs.getInt("prep.wiki_concept_handler.min_interval_between_concepts");
	}

	@Override
	public void handle(final int id, final String title, final String markups)
	{
		pool.submit(new Runnable()
		{
			@Override
			public void run()
			{
				parser.handle(id, title, markups);
			}
		});
		
		try
		{
			Thread.sleep(waitMs);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void finish()
	{
		try
		{
			pool.shutdown();
			pool.awaitTermination(3, TimeUnit.DAYS);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
