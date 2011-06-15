package ecologylab.semantics.concept.train;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.Join;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Property;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.preparation.postparsing.WikiLink;

public class ContextForDisambiguationFeatures extends Context
{
	
	private Map<String, WikiConcept> linkedSurfaces = new HashMap<String, WikiConcept>();

	public ContextForDisambiguationFeatures(WikiDoc wikiDoc)
	{
		super(wikiDoc);
		
		Session session2 = SessionManager.newSession();
		
		Criteria q = wikiDoc.getSession().createCriteria(WikiLink.class);
		q.add(Property.forName("fromId").eq(wikiDoc.getWikiConceptId()));
		q.setFetchSize(100);
		ScrollableResults rs = q.scroll();
		while (rs.next())
		{
			WikiLink wl = (WikiLink) rs.get(0);
			if (wl != null)
			{
				WikiConcept toConcept = WikiConcept.getById(wl.getToId(), session2);
				linkedSurfaces.put(wl.getSurface(), toConcept);
			}
		}
		rs.close();
		
		session2.close();
	}

	@Override
	public void disambiguate(ExtractedSurface instance, Session session)
	{
		// TODO Auto-generated method stub
		super.disambiguate(instance, session);
	}

	@Override
	public void add(ExtractedSurface instance, Session session)
	{
		// TODO Auto-generated method stub
		super.add(instance, session);
	}

}
