package ecologylab.semantics.concept.preparation.postparsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Property;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiSurface;

/**
 * calculate commonness using wiki_links and wiki_surfaces.
 * 
 * @author quyin
 * 
 */
public class CommonnessCalculator
{
	
	public static void main(String[] args)
	{
		Session session = SessionManager.newSession();
		
		Criteria q = session.createCriteria(WikiSurface.class);
		
		ScrollableResults sr = q.scroll();
		while (sr.next())
		{
			session.beginTransaction();
			
			WikiSurface ws = (WikiSurface) sr.get(0);
			String surface = ws.getSurface();
			
			Criteria q2 = session.createCriteria(WikiLink.class);
			q2.add(Property.forName("surface").eq(surface));
			List<WikiLink> links = q2.list();
			
			Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
			int totalCount = 0;
			for (WikiLink link : links)
			{
				int cid = link.getToId();
				int prevCount = 0;
				if (counts.containsKey(cid))
					prevCount = counts.get(cid);
				counts.put(cid, prevCount + 1);
				totalCount++;
			}
			
			for (int cid : counts.keySet())
			{
				Commonness com = new Commonness();
				com.setSurface(surface);
				com.setConceptId(cid);
				com.setCommonness(counts.get(cid) * 1.0 / totalCount);
				session.save(com);
			}
			
			session.getTransaction().commit();
		}
		
		session.close();
	}
	
}
