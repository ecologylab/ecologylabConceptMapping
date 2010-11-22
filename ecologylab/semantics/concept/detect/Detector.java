package ecologylab.semantics.concept.detect;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import libsvm.svm_node;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.learning.svm.NormalizerFactory;
import ecologylab.semantics.concept.learning.svm.PredicterFactory;
import ecologylab.semantics.concept.learning.svm.SVMPredicter;
import ecologylab.semantics.concept.learning.svm.LearningUtils;

/**
 * internal class used for concept detection.
 * 
 * @author quyin
 * 
 */
class Detector
{
	
	private Doc doc;
	
	public Detector(Doc doc)
	{
		this.doc = doc;
	}

	/**
	 * detect concepts given disambiguated instances (surfaces + features + disambiguation results).
	 * 
	 * @param instances instances with disambiguation results and detection features
	 * @return detected concepts in the form of instances
	 * @throws IOException
	 */
	public Set<Instance> detect(Set<Instance> instances) throws IOException
	{
		Set<Instance> rst = new HashSet<Instance>();
		
		double[] kvalueBuffer = null;
		for (Instance inst : instances)
		{
			inst.keyphraseness = inst.surface.getKeyphraseness();
			inst.occurrence = doc.getNumberOfOccurrences(inst.surface);
			inst.frequency = ((double) inst.occurrence) / doc.getTotalWords();
			
			svm_node[] svmInst = LearningUtils.constructSVMInstance(
					inst.keyphraseness,
					inst.contextualRelatedness,
					inst.disambiguationConfidence,
					inst.occurrence,
					inst.frequency
					);
			
			NormalizerFactory.get(ConceptConstants.DETECT_PARAM_FILE_PATH).normalize(svmInst);
			
			Map<Integer, Double> results = new HashMap<Integer, Double>();
			SVMPredicter pred = PredicterFactory.get(ConceptConstants.DETECT_MODEL_FILE_PATH);
			if (kvalueBuffer == null)
				kvalueBuffer = new double[pred.getNumOfSVs()];
			pred.predict(svmInst, results, kvalueBuffer);
			inst.detectionConfidence = results.get(ConceptConstants.POS_CLASS_INT_LABEL);
			if (inst.detectionConfidence > ConceptConstants.DETECT_THRESHOLD)
				rst.add(inst);
		}
		
		return rst;
	}

}
