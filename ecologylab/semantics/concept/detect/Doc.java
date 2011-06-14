package ecologylab.semantics.concept.detect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import libsvm.svm_node;

import org.hibernate.Session;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.Constants;
import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.detect.SurfaceDictionary.SurfaceExtractedCallback;
import ecologylab.semantics.concept.learning.svm.Normalizer;
import ecologylab.semantics.concept.learning.svm.NormalizerFactory;
import ecologylab.semantics.concept.learning.svm.PredicterFactory;
import ecologylab.semantics.concept.learning.svm.SVMPredictor;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.TextNormalizer;
import ecologylab.semantics.concept.utils.TextUtils;

/**
 * The central class for concept mapping. Not thread-safe (an instance of this class is supposed to
 * be used in a single thread).
 * 
 * @author quyin
 * 
 */
public class Doc extends Debug
{

	private static final double		DISAMBIGUATION_CONFIDENCE_THRESHOLD	= Configs.getDouble("feature_extraction.disambiguation_confidence_threshold");

	private static final double		RELATED_SURFACE_THRESHOLD						= Configs.getDouble("feature_extraction.related_surface_threshold");

	private static final double		DETECT_CONFIDENCE_THRESHOLD					= Configs.getDouble("detection.confidence_threshold");

	private final String					title;

	private final String					text;

	private final int							totalWords;

	private Map<String, Integer>	surfaceOccurrences;

	private List<Instance>				instances;

	public Doc(String title, String text)
	{
		this.title = title;
		this.text = TextNormalizer.normalize(text);
		this.totalWords = TextUtils.count(text, " ") + 1;
	}

	public String getTitle()
	{
		return title;
	}

	public String getText()
	{
		return text;
	}

	public int getTotalWords()
	{
		return totalWords;
	}

	/**
	 * entry of concept mapping.
	 * 
	 * @return a list of (detected) concept Instances.
	 */
	public List<Instance> getInstances()
	{
		if (instances == null)
		{
			Session session = SessionManager.newSession();
			extractSurfaces(session);
			disambiguateSurfaces(session);
			detectConcepts();
			session.close();
		}
		return instances;
	}

	/**
	 * extract surfaces from the text.
	 * 
	 * @param session
	 */
	protected void extractSurfaces(final Session session)
	{
		instances = new ArrayList<Instance>();
		surfaceOccurrences = new HashMap<String, Integer>();
		final Doc doc = this;
		SurfaceDictionary.get().extractSurfaces(text, new SurfaceExtractedCallback()
		{
			@Override
			public void newSurface(String surface, int textOffset)
			{
				WikiSurface ws = WikiSurface.get(surface, session);
				Instance instance = new Instance(doc, textOffset, ws);
				instances.add(instance);
				int occ = surfaceOccurrences.containsKey(surface) ? surfaceOccurrences.get(surface) : 0;
				surfaceOccurrences.put(surface, occ + 1);
			}
		});
	}

	/**
	 * disambiguate all surfaces. for unambiguous surfaces, use the unique concept it is associated
	 * with. for ambiguous surfaces, disambiguate them. disambiguation results stored in Instance
	 * objects directly.
	 * 
	 * @param session
	 */
	protected void disambiguateSurfaces(Session session)
	{
		Context context = createContext();

		// set up initial context
		Set<Instance> unresolved = new HashSet<Instance>();
		for (Instance instance : instances)
		{
			if (SurfaceDictionary.get().getSenseCount(instance.getWikiSurface().getSurface()) == 1)
			{
				context.add(instance, session);
			}
			else
			{
				unresolved.add(instance);
			}
		}
		if (context.size() == 0)
		{
			// no unambiguous surfaces? do a best guess ...
			Instance instance = findInstanceWithLargestCommonnessAndDisambiguate(unresolved);
			context.add(instance, session);
		}

		while (unresolved.size() > 0)
		{
			// find related surfaces
			Set<Instance> related = new HashSet<Instance>();
			Instance bestRelatedOne = null;
			double bestRelatedness = 0;
			for (Instance instance : unresolved)
			{
				double relatedness = getAllSenseRelatedness(context, instance, session);
				if (relatedness > RELATED_SURFACE_THRESHOLD)
				{
					related.add(instance);
				}
				if (bestRelatedOne == null || bestRelatedness < relatedness)
				{
					bestRelatedOne = instance;
					bestRelatedness = relatedness;
				}
			}
			if (related.size() == 0)
			{
				// no related surfaces? find the most related one ...
				related.add(bestRelatedOne);
			}

			// resolve still ambiguous surfaces
			Set<Instance> resolved = new HashSet<Instance>();
			Instance bestConfidentOne = null;
			double bestConfidence = 0;
			for (Instance instance : related)
			{
				context.disambiguate(instance, session);
				double confidence = instance.getDisambiguationConfidence();
				if (confidence > DISAMBIGUATION_CONFIDENCE_THRESHOLD)
				{
					resolved.add(instance);
				}
				if (bestConfidentOne == null || bestConfidence < confidence)
				{
					bestConfidentOne = instance;
					bestConfidence = confidence;
				}
			}
			if (resolved.size() == 0)
			{
				// no surfaces are disambiguated confidently enough? find the most confident one ...
				resolved.add(bestConfidentOne);
			}

			for (Instance instance : resolved)
			{
				context.add(instance, session);
				unresolved.remove(instance);
			}
		}
	}

	protected Context createContext()
	{
		return new Context(this);
	}

	private Instance findInstanceWithLargestCommonnessAndDisambiguate(Set<Instance> unresolved)
	{
		Instance best = null;
		double bestCommonness = 0;

		for (Instance inst : unresolved)
		{
			Map<WikiConcept, Double> concepts = inst.getWikiSurface().getConcepts();
			for (WikiConcept c : concepts.keySet())
			{
				double commonness = concepts.get(c);
				if (best == null || bestCommonness < commonness)
				{
					best = inst;
					bestCommonness = commonness;
				}
			}
		}

		return best;
	}

	private double getAllSenseRelatedness(Context context, Instance instance, Session session)
	{
		double rst = 0;
		Set<WikiConcept> concepts = instance.getWikiSurface().getConcepts().keySet();
		for (WikiConcept concept : concepts)
		{
			double rel = context.getContextualRelatedness(concept, session);
			if (rel > rst)
				rst = rel;
		}
		return rst;
	}

	/**
	 * detect concepts. detection results stored in Instance objects directly.
	 * 
	 * prerequisites: surfaces extracted and disambiguated.
	 * 
	 */
	protected void detectConcepts()
	{
		double[] kvalueBuffer = null;
		for (Instance inst : instances)
		{
			inst.setOccurrence(surfaceOccurrences.get(inst.getWikiSurface().getSurface()));
			inst.setFrequency(inst.getOccurrence() * 1.0 / getTotalWords());

			svm_node[] svmInst = inst.toSvmInstanceForDetection();

			Normalizer normalizer = NormalizerFactory.get(Configs.getFile("detection.normalization"));
			SVMPredictor predictor = PredicterFactory.get(Configs.getFile("detection.model"), normalizer);

			Map<Integer, Double> results = new HashMap<Integer, Double>();
			if (kvalueBuffer == null)
				kvalueBuffer = new double[predictor.getNumOfSVs()];
			predictor.predict(svmInst, results, kvalueBuffer);
			inst.setDetectionConfidence(results.get(Constants.POS_CLASS_INT_LABEL));
			if (inst.getDetectionConfidence() > DETECT_CONFIDENCE_THRESHOLD)
				inst.setDetected(true);
		}
	}

	public static void main(String[] args) throws IOException
	{
		// String text = TextUtils.readString("usa.wiki");
		String text = "we know that united states census 2000 is famous in united states";
		Doc doc = new Doc("USA", text);
		if (doc != null)
		{
			for (Instance inst : doc.getInstances())
			{
				if (!inst.isDetected())
					continue;
				System.out.format("%s -> %s (%.4f)",
						inst.getWikiSurface().getSurface(),
						inst.getWikiConcept().getTitle(),
						inst.getDetectionConfidence());
				System.out.println();
			}
		}
	}

}
