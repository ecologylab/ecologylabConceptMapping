package ecologylab.semantics.concept.wikiparsing;

import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiRedirect;

public class WikiConceptHandlerInitial implements WikiConceptHandler
{

	@Override
	public void handle(int id, String title, String markups)
	{
		Session session = SessionManager.getSession();

		session.beginTransaction();

		WikiConcept concept = null;
		WikiRedirect redirect = (WikiRedirect) session.get(WikiRedirect.class, title);
		if (redirect == null)
		{
			concept = new WikiConcept();
			concept.setId(id);
			concept.setTitle(title);
			concept.setText("");
			session.save(concept);
		}

		session.getTransaction().commit();
		
		if (concept != null)
			session.evict(concept);
		if (redirect != null)
			session.evict(redirect);
	}

}
