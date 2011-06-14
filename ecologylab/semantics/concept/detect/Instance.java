package ecologylab.semantics.concept.detect;

import libsvm.svm_node;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;

/**
 * an instance is an occurrence of a surface in an article, which may be mapped to a concept for
 * disambiguation. it also contains features and classification results (confidences).
 * 
 * @author quyin
 * 
 */
public class Instance
{

	private Doc					doc;

	private int					textOffset;

	private WikiSurface	surface;

	private WikiConcept	concept;

	// features for disambiguation:

	/* commonness omitted */

	private double			windowedContextualRelatedness;

	private double			contextualRelatedness;

	private double			contextQuality;

	private double			disambiguationConfidence;

	// features for detection:

	/* keyphraseness omitted */

	private double			occurrence;										// number of occurrence

	private double			frequency;

	private double			detectionConfidence;

	// detection result
	private boolean			detected;

	// internal use

	private String			toString;

	public Instance(Doc doc, int textOffset, WikiSurface surface)
	{
		this.doc = doc;
		this.textOffset = textOffset;
		this.surface = surface;
	}

	@Override
	public String toString()
	{
		if (toString == null)
		{
			toString = String.format("instance[surface:%s, concept:%s, confidences:%f,%f]",
					surface,
					concept.getTitle(),
					disambiguationConfidence,
					detectionConfidence);
		}
		return toString;
	}

	public Doc getDoc()
	{
		return doc;
	}

	public int getTextOffset()
	{
		return textOffset;
	}

	public WikiSurface getWikiSurface()
	{
		return surface;
	}

	public WikiConcept getWikiConcept()
	{
		return concept;
	}

	void setWikiConcept(WikiConcept concept)
	{
		this.concept = concept;
	}

	public double getWindowedContextualRelatedness()
	{
		return windowedContextualRelatedness;
	}

	void setWindowedContextualRelatedness(double windowedContextualRelatedness)
	{
		this.windowedContextualRelatedness = windowedContextualRelatedness;
	}

	public double getContextualRelatedness()
	{
		return contextualRelatedness;
	}

	void setContextualRelatedness(double contextualRelatedness)
	{
		this.contextualRelatedness = contextualRelatedness;
	}

	public double getContextQuality()
	{
		return contextQuality;
	}

	void setContextQuality(double contextQuality)
	{
		this.contextQuality = contextQuality;
	}

	public double getDisambiguationConfidence()
	{
		return disambiguationConfidence;
	}

	void setDisambiguationConfidence(double disambiguationConfidence)
	{
		this.disambiguationConfidence = disambiguationConfidence;
	}

	public double getDetectionConfidence()
	{
		return detectionConfidence;
	}

	void setDetectionConfidence(double detectionConfidence)
	{
		this.detectionConfidence = detectionConfidence;
	}

	public double getOccurrence()
	{
		return occurrence;
	}

	void setOccurrence(double occurrence)
	{
		this.occurrence = occurrence;
	}

	public double getFrequency()
	{
		return frequency;
	}

	void setFrequency(double frequency)
	{
		this.frequency = frequency;
	}

	public boolean isDetected()
	{
		return detected;
	}

	void setDetected(boolean detected)
	{
		this.detected = detected;
	}

	public svm_node[] toSvmInstanceForDisambiguation()
	{
		svm_node[] svmInst = constructSVMInstance(
					this.getWikiSurface().getConcepts().get(this.getWikiConcept()),
					this.getWindowedContextualRelatedness(),
					this.getContextualRelatedness(),
					this.getContextQuality()
					);
		return svmInst;
	}

	public svm_node[] toSvmInstanceForDetection()
	{
		svm_node[] svmInst = constructSVMInstance(
					this.getWikiSurface().getConcepts().get(this.getWikiConcept()),
					this.getWindowedContextualRelatedness(),
					this.getContextualRelatedness(),
					this.getContextQuality(),
					this.getWikiSurface().getKeyphraseness(),
					this.getDisambiguationConfidence(),
					this.getOccurrence(),
					this.getFrequency()
					);
		return svmInst;
	}

	private static svm_node[] constructSVMInstance(double... features)
	{
		svm_node[] instance = new svm_node[features.length];
		for (int i = 0; i < instance.length; ++i)
		{
			instance[i] = new svm_node();
			instance[i].index = i + 1;
			instance[i].value = features[i];
		}
		return instance;
	}

}
