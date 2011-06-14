package ecologylab.semantics.concept.train;

import org.hibernate.Session;

import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.detect.Doc;

public abstract class WikiDoc extends Doc
{

	private Session	session;

	private int			wikiConceptId;

	public WikiDoc(String title, Session session)
	{
		this(WikiConcept.getByTitle(title, session));
		this.session = session;
	}

	private WikiDoc(WikiConcept wikiConcept)
	{
		super(wikiConcept.getTitle(), wikiConcept.getText());
		this.wikiConceptId = wikiConcept.getId();
	}

	public Session getSession()
	{
		return session;
	}
	
	public int getWikiConceptId()
	{
		return wikiConceptId;
	}

	public void prepare()
	{
		extractSurfaces(session);
		disambiguateSurfaces(session);
		detectConcepts();
	}

}
