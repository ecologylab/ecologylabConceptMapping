package ecologylab.semantics.concept.mapping;

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
import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.learning.Constants;
import ecologylab.semantics.concept.learning.Normalizer;
import ecologylab.semantics.concept.learning.svm.old.NormalizerFactory;
import ecologylab.semantics.concept.learning.svm.old.PredicterFactory;
import ecologylab.semantics.concept.learning.svm.old.SVMPredictor;
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

	private static final double			DISAMBIGUATION_CONFIDENCE_THRESHOLD	= Configs.getDouble("feature_extraction.disambiguation_confidence_threshold");

	private static final double			RELATED_SURFACE_THRESHOLD						= Configs.getDouble("feature_extraction.related_surface_threshold");

	private static final double			DETECT_CONFIDENCE_THRESHOLD					= Configs.getDouble("detection.confidence_threshold");

	private Session									session;

	private final String						title;

	private final String						text;

	private final int								totalWords;

	private List<ExtractedSurface>	extractedSurfaces;

	private Map<String, Integer>		surfaceOccurrences;

	public Doc(Session session, String title, String text)
	{
		this.session = session;
		this.title = title;
		this.text = TextNormalizer.normalize(text);
		this.totalWords = TextUtils.count(text, " ") + 1;
		
		extractSurfaces();
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
	public List<ExtractedSurface> getInstances()
	{
		if (extractedSurfaces == null)
		{
			Session session = SessionManager.newSession();
			extractSurfaces();
			disambiguateSurfaces(session);
			detectConcepts();
			session.close();
		}
		return extractedSurfaces;
	}

	/**
	 * extract surfaces from the text.
	 * 
	 * @param session
	 */
	protected void extractSurfaces()
	{
		extractedSurfaces = new ArrayList<ExtractedSurface>();
		surfaceOccurrences = new HashMap<String, Integer>();
		List<String> surfaces = new ArrayList<String>();
		List<Integer> offsets = new ArrayList<Integer>();
		int n = SurfaceDictionary.get().extractSurfaces(text, surfaces, offsets);
		for (int i = 0; i < n; ++i)
		{
			String surface = surfaces.get(i);
			int offset = offsets.get(i);
			
			WikiSurface ws = WikiSurface.get(surface, session);
			ExtractedSurface instance = new ExtractedSurface(this, offset, ws);
			extractedSurfaces.add(instance);
			int occ = surfaceOccurrences.containsKey(surface) ? surfaceOccurrences.get(surface) : 0;
			surfaceOccurrences.put(surface, occ + 1);
		}
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
		Set<ExtractedSurface> unresolved = new HashSet<ExtractedSurface>();
		for (ExtractedSurface instance : extractedSurfaces)
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
			ExtractedSurface instance = findInstanceWithLargestCommonnessAndDisambiguate(unresolved);
			context.add(instance, session);
		}

		while (unresolved.size() > 0)
		{
			// find related surfaces
			Set<ExtractedSurface> related = new HashSet<ExtractedSurface>();
			ExtractedSurface bestRelatedOne = null;
			double bestRelatedness = 0;
			for (ExtractedSurface instance : unresolved)
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
			Set<ExtractedSurface> resolved = new HashSet<ExtractedSurface>();
			ExtractedSurface bestConfidentOne = null;
			double bestConfidence = 0;
			for (ExtractedSurface instance : related)
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

			for (ExtractedSurface instance : resolved)
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

	private ExtractedSurface findInstanceWithLargestCommonnessAndDisambiguate(Set<ExtractedSurface> unresolved)
	{
		ExtractedSurface best = null;
		double bestCommonness = 0;

		for (ExtractedSurface inst : unresolved)
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

	private double getAllSenseRelatedness(Context context, ExtractedSurface instance, Session session)
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
		for (ExtractedSurface inst : extractedSurfaces)
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
			for (ExtractedSurface inst : doc.getInstances())
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
