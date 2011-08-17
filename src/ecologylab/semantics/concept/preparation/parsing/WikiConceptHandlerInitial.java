package ecologylab.semantics.concept.preparation.parsing;

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
		Session session = SessionManager.newSession();
		session.beginTransaction();

		WikiConcept concept = null;

		// we don't need to handle case problems here, because these titles are true titles (not links
		// in the markups), so their cases must be correct.
		if (WikiRedirect.getRedirected(title) == null)
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

	@Override
	public void finish()
	{

	}

}
