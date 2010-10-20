package ecologylab.semantics.concept.detect;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.learning.svm.SVMGaussianNormalization;
import ecologylab.semantics.concept.learning.svm.SVMPredicter;
import ecologylab.semantics.concept.learning.svm.Utils;

/**
 * Extract features for link detection. Including keyphraseness, contextual / average relatedness,
 * disambiguation confidence, frequency, first / last occurrence and spread in %.
 * <p>
 * The inputs are paragraphs of free text. The outputs are features. It first generates n-grams with
 * NGramGenerator, filters out non-surface n-grams, finds out non-ambiguous surfaces, carries out
 * link resolution and at last calculate features.
 * 
 * @author quyin
 * 
 */
public class Detector extends Debug
{

	public static interface DetectionListener
	{
		void conceptDetected(String surface, String concept, boolean prediction, double confidence,
				Instance inst);
	}

	private List<DetectionListener>	listeners	= new ArrayList<DetectionListener>();

	public void addDetectionListener(DetectionListener listener)
	{
		listeners.add(listener);
	}

	public void removeDetectionListener(DetectionListener listener)
	{
		listeners.remove(listener);
	}

	// members

	private Disambiguator							disambiguator;

	private Set<Concept>							detectedConcepts;

	private SVMGaussianNormalization	norm;

	private SVMPredicter							pred;

	public Detector() throws IOException
	{
		norm = new SVMGaussianNormalization(ConceptConstants.DETECT_PARAM_FILE_PATH);
		pred = new SVMPredicter(ConceptConstants.DETECT_MODEL_FILE_PATH);

	}

	public Set<Concept> detect(Doc doc) throws IOException
	{
		if (detectedConcepts == null)
		{
			disambiguator = new Disambiguator();
			Set<Instance> instances = disambiguator.disambiguate(doc);
			detectedConcepts = detectConcepts(instances);
		}
		return detectedConcepts;
	}

	protected Set<Concept> detectConcepts(Set<Instance> instances) throws IOException
	{
		Set<Concept> rst = new HashSet<Concept>();

		for (Instance inst : instances)
		{
			svm_node[] svmInst = Utils.constructSVMInstance(inst.keyphraseness,
					inst.contextualRelatedness,
					inst.disambiguationConfidence, inst.occurrence, inst.frequency);
			norm.normalize(svmInst);
			Map<Integer, Double> results = new HashMap<Integer, Double>();
			boolean prediction = pred.predict(svmInst, results) == ConceptConstants.POS_CLASS_INT_LABEL;
			inst.detectionConfidence = results.get(ConceptConstants.POS_CLASS_INT_LABEL);

			for (DetectionListener listener : listeners)
			{
				debug("calling listener: " + listener);
				listener.conceptDetected(
						inst.surface.word,
						inst.disambiguatedConcept.title,
						prediction,
						inst.detectionConfidence,
						inst
						);
			}

			if (inst.detectionConfidence > ConceptConstants.DETECT_THRESHOLD)
				rst.add(inst.disambiguatedConcept);
		}

		return rst;
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		TrieDict dict = TrieDict.load(new File("data/freq-surfaces.dict"));
		String text = "Senator Blanche Lincoln, an Arkansas Democrat and chairwoman of the Senate Agriculture Committee, has been seeking $1.5 billion in disaster relief for rice and cotton growers in Arkansas and other Southern states who were hurt by heavy rains. The White House seems all too eager to oblige an important Democrat who is in a very difficult re-election race. Arkansas cotton farmers suffered real crop losses, averaging 30 percent; rice farmers far less, averaging less than 4 percent. Unfortunately, Ms. Lincoln’s proposal makes no distinction, and in many cases the payments would overcompensate farmers. Relief payments would be based not on a farm’s actual loss but on the amount it received under the government’s direct payments program, a generous annual subsidy based on a farm’s size regardless of market conditions. Anyone with a loss of more than 5 percent would get a check amounting to 90 percent of the subsidy. This would be a big, unjustified windfall, especially for big farmers. According to an analysis by the Environmental Working Group, an advocacy group, a large chunk of the aid — some $210 million — would go to Arkansas, including 270 farms that would be eligible for $100,000 each. All in all, this looks to us like a save-Blanche-Lincoln program rather than a save-the-farmer program. Ms. Lincoln first sought the aid through normal legislative channels, as an amendment to a small-business bill. Democratic leaders said this would overburden an already expensive bill, so — according to numerous published accounts — the White House chief of staff, Rahm Emanuel, promised to find the money. Rob Nabors, deputy director of the Office of Management and Budget, followed up with a letter dated Aug. 6 offering assurances that the administration “is committed to providing assistance consistent with your legislative proposal.” The money has yet to be found, and neither the budget bureau nor the Agriculture Department seems to know where to get it. Meanwhile, Collin Peterson, chairman of the House Agriculture Committee, says “there is no way they can do this administratively,” and thinks authorizing legislation is required. Congress and the administration need to work together to come up with a rational aid program to help farmers who are in real trouble. Ms. Lincoln will have to find a better way to save her job. ";
		Detector detector = new Detector();
		Set<Concept> rst = detector.detect(new Doc(text, dict));
		for (Concept concept : rst)
		{
			System.out.println(concept.surface + " -> " + concept);
		}
		DatabaseFacade.get().close();
	}

}
