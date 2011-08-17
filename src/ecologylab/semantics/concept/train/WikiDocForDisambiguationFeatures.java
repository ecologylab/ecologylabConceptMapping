package ecologylab.semantics.concept.train;

import org.hibernate.Session;

import ecologylab.semantics.concept.mapping.Context;

public class WikiDocForDisambiguationFeatures extends WikiDoc
{

	public WikiDocForDisambiguationFeatures(String title, Session session)
	{
		super(title, session);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Context createContext()
	{
		return new ContextForDisambiguationFeatures(this);
	}

	@Override
	protected void detectConcepts()
	{
		debug("detectConcepts() does nothing.");
	}

}
