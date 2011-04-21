package wikxplorer.messages;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import wikxplorer.ScopeKeys;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.semantics.concept.database.orm.Relatedness;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.preparation.postparsing.WikiLink;
import ecologylab.serialization.simpl_inherit;

/**
 * Request suggestions for a concept.
 * 
 * @author quyin
 * 
 */
@simpl_inherit
public class SuggestionRequest extends RequestMessage
{

	private static final double	MIN_DIST_THREASHOLD	= 0.5;

	/**
	 * The title of the source concept.
	 */
	@simpl_scalar
	private String							source;

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	@Override
	public SuggestionResponse performService(Scope clientSessionScope)
	{
		Session session = (Session) clientSessionScope.get(ScopeKeys.SESSION);

		Map<String, WikiConcept> clippingContext = (Map<String, WikiConcept>) clientSessionScope
				.get(ScopeKeys.CLIPPING_CONTEXT);

		WikiConcept sourceConcept = null;
		if (clippingContext.containsKey(source))
		{
			sourceConcept = clippingContext.get(source);
		}
		else
		{
			sourceConcept = WikiConcept.get(source, session);
		}

		SuggestionResponse resp = new SuggestionResponse();
		if (sourceConcept != null)
		{
			List<WikiLink> inlinks = WikiLink.getByDestination(sourceConcept.getId(), session);
			List<WikiLink> outlinks = WikiLink.getBySource(sourceConcept.getId(), session);

			for (WikiLink inlink : inlinks)
			{
				int id = inlink.getFromId();
				double relatedness = Relatedness.get(id, sourceConcept.getId(), session);
				if (relatedness < MIN_DIST_THREASHOLD)
				{
					WikiConcept concept = (WikiConcept) session.get(WikiConcept.class, id);
					Concept c = new Concept();
					c.setTitle(concept.getTitle());
					c.setType(Concept.INLINK);
					c.setRelatedness(relatedness);
					ConceptGroup cg = new ConceptGroup();
					cg.getConcepts().put(c.getTitle(), c);
					cg.setTopTitle(c.getTitle());
					cg.setAverageRelatedness(relatedness);
					resp.getGroups().add(cg);
				}
			}

			for (WikiLink outlink : outlinks)
			{
				int id = outlink.getToId();
				double relatedness = Relatedness.get(id, sourceConcept.getId(), session);
				if (relatedness < MIN_DIST_THREASHOLD)
				{
					WikiConcept concept = (WikiConcept) session.get(WikiConcept.class, id);
					Concept c = new Concept();
					c.setTitle(concept.getTitle());
					c.setType(Concept.OUTLINK);
					c.setRelatedness(relatedness);
					ConceptGroup cg = new ConceptGroup();
					cg.getConcepts().put(c.getTitle(), c);
					cg.setTopTitle(c.getTitle());
					cg.setAverageRelatedness(relatedness);
					resp.getGroups().add(cg);
				}
			}

			resp.setOk(true);
		}

		return resp;
	}

}
