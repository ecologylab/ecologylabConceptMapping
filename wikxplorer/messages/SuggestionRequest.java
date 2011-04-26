package wikxplorer.messages;

import java.util.Map;

import org.hibernate.Session;

import wikxplorer.ScopeKeys;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.semantics.concept.database.orm.WikiConcept;
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
			sourceConcept = WikiConcept.getByTitle(source, session);
		}

		SuggestionResponse resp = new SuggestionResponse();
		if (sourceConcept != null)
		{
			Map<WikiConcept, String> inlinks = sourceConcept.getInlinks();
			Map<WikiConcept, String> outlinks = sourceConcept.getOutlinks();

			for (WikiConcept inlink : inlinks.keySet())
			{
				double relatedness = sourceConcept.getRelatedness(inlink);
				if (relatedness < MIN_DIST_THREASHOLD)
				{
					Concept c = new Concept();
					c.setTitle(inlink.getTitle());
					c.setType(Concept.INLINK);
					c.setRelatedness(relatedness);
					ConceptGroup cg = new ConceptGroup();
					cg.getConcepts().put(c.getTitle(), c);
					cg.setTopTitle(c.getTitle());
					cg.setAverageRelatedness(relatedness);
					resp.getGroups().add(cg);
				}
			}

			for (WikiConcept outlink : outlinks.keySet())
			{
				double relatedness = sourceConcept.getRelatedness(outlink);
				if (relatedness < MIN_DIST_THREASHOLD)
				{
					Concept c = new Concept();
					c.setTitle(outlink.getTitle());
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
