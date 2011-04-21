package ecologylab.semantics.concept.preparation.parsing;

import java.util.HashSet;
import java.util.Set;

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

	private static class ParsingClosure implements Runnable
	{

		private WikiConceptHandler							worker;

		private WikiConceptHandlerBunchParsing	listener;

		private int															id;

		private String													title;

		private String													markups;

		public ParsingClosure(WikiConceptHandler worker, WikiConceptHandlerBunchParsing listener,
				int id, String title, String markups)
		{
			this.worker = worker;
			this.id = id;
			this.title = title;
			this.markups = markups;
			this.listener = listener;
		}

		@Override
		public void run()
		{
			worker.handle(id, title, markups);
			listener.notifyFinish(this);
		}

		public void start()
		{
			Thread t = new Thread(this);
			t.start();
		}

	}

	/**
	 * parser must be thread safe!!!
	 */
	private WikiConceptHandler	parser;

	private Set<ParsingClosure>	pool			= new HashSet<ParsingClosure>();

	private Object							lockPool	= new Object();

	private int									nThreads	= 1;

	public WikiConceptHandlerBunchParsing() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		parser = new WikiConceptHandlerParsing();
		nThreads = Configs.getInt("prep.wiki_concept_handler.number_of_threads");
	}

	@Override
	public void handle(int id, String title, String markups)
	{
		synchronized (lockPool)
		{
			int size = pool.size();
			if (size >= nThreads)
			{
				try
				{
					lockPool.wait();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			ParsingClosure closure = new ParsingClosure(parser, this, id, title, markups);
			pool.add(closure);
			closure.start();
		}
	}

	public void notifyFinish(ParsingClosure closure)
	{
		synchronized (lockPool)
		{
			pool.remove(closure);
			lockPool.notify();
		}
	}

	@Override
	public void finish()
	{
		// TODO Auto-generated method stub

	}

}
