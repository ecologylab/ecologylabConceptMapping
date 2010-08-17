package ecologylab.semantics.concept.detect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.text.ConceptAnchor;
import ecologylab.semantics.concept.utils.CollectionUtils;
import ecologylab.semantics.concept.utils.Pair;

public class Context extends Debug
{

	private Set<ConceptAnchor>															anchors;

	private Set<String>																			surfaces;

	private Map<String, List<String>>												inlinksMap;

	private Map<Pair<ConceptAnchor, ConceptAnchor>, Double>	mutualRelatedness;

	private Map<ConceptAnchor, Double>											weights;

	private double																					quality;

	public Set<ConceptAnchor> getAnchors()
	{
		if (anchors == null)
		{
			anchors = new HashSet<ConceptAnchor>();
		}
		return anchors;
	}

	public Set<String> getSurfaces()
	{
		if (surfaces == null)
		{
			surfaces = new HashSet<String>();
		}
		return surfaces;
	}

	public List<String> getConceptInlinks(String concept)
	{
		if (inlinksMap.containsKey(concept))
		{
			return inlinksMap.get(concept);
		}
		return DatabaseUtils.get().queryFromConceptsForConcept(concept);
	}

	public double getWeight(ConceptAnchor anchor)
	{
		return weights.get(anchor);
	}

	public double getQuality()
	{
		return quality;
	}

	public int size()
	{
		return getAnchors().size();
	}

	public void add(ConceptAnchor anchor)
	{
		getAnchors().add(anchor);
		getSurfaces().add(anchor.getSurface());
	}

	public void add(String surface, String concept)
	{
		ConceptAnchor anchor = new ConceptAnchor(surface, concept);
		add(anchor);
	}

	public void addAll(Context other)
	{
		for (ConceptAnchor anchor : other.getAnchors())
		{
			add(anchor);
		}
	}

	public void init()
	{
		retrieveInlinks();
		calcMutualRelatedness();
		calcWeights();
		calcQuality();
	}

	private void retrieveInlinks()
	{
		debug("retreiving inlinks ...");

		inlinksMap = new HashMap<String, List<String>>();
		for (ConceptAnchor anchor : getAnchors())
		{
			String concept = anchor.getConcept();
			List<String> inlinks = DatabaseUtils.get().queryFromConceptsForConcept(concept);
			inlinksMap.put(concept, inlinks);
		}
	}

	private void calcMutualRelatedness()
	{
		debug("calculating mutual relatedness ...");

		int N = size() * size();
		int n = 0;
		debug(N + " pair(s) in total;");

		mutualRelatedness = new HashMap<Pair<ConceptAnchor, ConceptAnchor>, Double>();
		for (ConceptAnchor a1 : anchors)
		{
			for (ConceptAnchor a2 : anchors)
			{
				Pair<ConceptAnchor, ConceptAnchor> p = new Pair<ConceptAnchor, ConceptAnchor>(a1, a2);
				if (!mutualRelatedness.containsKey(p))
				{
					String c1 = a1.getConcept();
					String c2 = a2.getConcept();
					List<String> l1 = inlinksMap.get(c1);
					List<String> l2 = inlinksMap.get(c2);
					double rel = DatabaseUtils.get().queryRelatedness(l1, l2);
					mutualRelatedness.put(new Pair<ConceptAnchor, ConceptAnchor>(a1, a2), rel);
					mutualRelatedness.put(new Pair<ConceptAnchor, ConceptAnchor>(a2, a1), rel);

					n += 2;
					if (n % 1000 == 0)
					{
						debug(n + " pair(s) processed;");
					}
				}
			}
		}
		debug("done: " + n + " pair(s) processed");
	}

	private void calcWeights()
	{
		debug("calculating anchor weights ...");

		weights = new HashMap<ConceptAnchor, Double>();
		for (ConceptAnchor anchor : getAnchors())
		{
			String surface = anchor.getSurface();
			double kp = DatabaseUtils.get().queryKeyphraseness(surface);
			double rel = 0;
			for (ConceptAnchor a : getAnchors())
			{
				rel += mutualRelatedness.get(new Pair<ConceptAnchor, ConceptAnchor>(anchor, a));
			}
			rel /= size();
			double w = kp * ConceptConstants.WEIGHT_KEYPHRASENESS + rel
					* ConceptConstants.WEIGHT_MUTUAL_RELATEDNESS;
			weights.put(anchor, w);
		}
	}

	private void calcQuality()
	{
		debug("calculating context quality ...");

		quality = CollectionUtils.sum(weights.values());
	}

	@Override
	public String toString()
	{
		return "Context[" + anchors.size() + " anchors]";
	}

}
