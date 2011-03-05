package ecologylab.semantics.concept.wikiparsing;

import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiRedirect;

/**
 * Store only IDs and titles to the database, as the initial building-up of the database.
 * 
 * @author quyin
 *
 */
public class WikiConceptHandlerInitial implements WikiConceptHandler
{

	@Override
	public void handle(int id, String title, String markups)
	{
		Session session = SessionManager.getSession();
		
		session.beginTransaction();

		WikiConcept concept = null;
		if (WikiRedirect.getRedirected(title, session) == null)
		{
			concept = new WikiConcept();
			concept.setId(id);
			concept.setTitle(title);
			concept.setText("");
			session.save(concept);
		}

		session.getTransaction().commit();
		session.close();
	}

}
