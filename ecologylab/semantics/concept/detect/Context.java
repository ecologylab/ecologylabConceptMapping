package ecologylab.semantics.concept.detect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.database.DatabaseUtils;
import ecologylab.semantics.concept.text.ConceptAnchor;
import ecologylab.semantics.concept.utils.CollectionUtils;
import ecologylab.semantics.concept.utils.Pair;

public class Context
{

	private Set<ConceptAnchor>															anchors						= new HashSet<ConceptAnchor>();

	private Set<String>																			surfaces					= new HashSet<String>();

	private Map<Pair<ConceptAnchor, ConceptAnchor>, Double>	mutualRelatedness	= new HashMap<Pair<ConceptAnchor, ConceptAnchor>, Double>();

	private Map<ConceptAnchor, Double>											weights						= new HashMap<ConceptAnchor, Double>();

	private double																					quality;

	public void add(ConceptAnchor anchor)
	{
		anchors.add(anchor);
		surfaces.add(anchor.getSurface());
	}

	public void add(String surface, String concept)
	{
		ConceptAnchor anchor = new ConceptAnchor(surface, concept);
		add(anchor);
	}

	public void addAll(Context other)
	{
		for (ConceptAnchor anchor : other.anchors)
		{
			add(anchor);
		}
	}

	public Set<ConceptAnchor> getAnchors()
	{
		return anchors;
	}

	public Set<String> getSurfaces()
	{
		return surfaces;
	}

	public int size()
	{
		return anchors.size();
	}

	public void init()
	{
		calcMutualRelatedness();
		calcWeights();
		calcQuality();
	}

	private void calcMutualRelatedness()
	{
		for (ConceptAnchor a1 : anchors)
		{
			for (ConceptAnchor a2 : anchors)
			{
				String c1 = a1.getConcept();
				String c2 = a2.getConcept();
				double rel = DatabaseUtils.get().queryRelatedness(c1, c2);
				mutualRelatedness.put(new Pair<ConceptAnchor, ConceptAnchor>(a1, a2), rel);
				mutualRelatedness.put(new Pair<ConceptAnchor, ConceptAnchor>(a2, a1), rel);
			}
		}
	}

	private void calcWeights()
	{
		for (ConceptAnchor anchor : anchors)
		{
			String surface = anchor.getSurface();
			double kp = DatabaseUtils.get().queryKeyphraseness(surface);
			double rel = 0;
			for (ConceptAnchor a : anchors)
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
		quality = CollectionUtils.sum(weights.values());
	}

	public double getWeight(ConceptAnchor anchor)
	{
		return weights.get(anchor);
	}

	public double getQuality()
	{
		return quality;
	}

	@Override
	public String toString()
	{
		return "Context: " + anchors.toString();
	}

}
