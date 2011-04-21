package ecologylab.semantics.concept.preparation.postparsing;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiRedirect;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.utils.TextNormalizer;

/**
 * import redirects as links. should be called after wiki_redirects is done.
 * 
 * @author quyin
 *
 */
public class RedirectAsLink
{

	public static void main(String[] args)
	{
		Session session = SessionManager.newSession();
		session.beginTransaction();
		
		Criteria q = session.createCriteria(WikiRedirect.class);
		
		Session session2 = SessionManager.newSession();
		
		ScrollableResults sr = q.scroll();
		while (sr.next())
		{
			session2.beginTransaction();
			
			WikiRedirect wr = (WikiRedirect) sr.get(0);
			System.out.format("processing %s -> %s...\n", wr.getFromTitle(), wr.getToTitle());

			WikiConcept toConcept = WikiConcept.getByTitle(wr.getToTitle(), session2);
			if (toConcept != null)
			{
				String surface = TextNormalizer.normalize(wr.getFromTitle());
				WikiSurface wikiSurface = WikiSurface.get(surface, session2);
				if (wikiSurface == null)
				{
					wikiSurface = new WikiSurface();
					wikiSurface.setSurface(surface);
					session2.save(wikiSurface);
				}
				
				WikiLink link = new WikiLink();
				link.setFromId(toConcept.getId());
				link.setToId(toConcept.getId());
				link.setSurface(surface);
				session2.save(link);
			}
			
			session2.getTransaction().commit();
		}
		sr.close();
		
		session2.close();
		session.getTransaction().commit();
		session.close();
	}

}
