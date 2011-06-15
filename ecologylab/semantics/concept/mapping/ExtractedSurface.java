package ecologylab.semantics.concept.mapping;

import libsvm.svm_node;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;

/**
 * an occurrence of a surface in an article, which may be eventually mapped to a concept and
 * returned.
 * 
 * @author quyin
 * 
 */
public class ExtractedSurface
{

	private Doc					doc;

	private int					textOffset;

	private WikiSurface	surface;

	private WikiConcept	concept;

	// features:

	/* commonness omitted */

	private double			contextualRelatedness;

	private double			contextQuality;

	private double			disambiguationConfidence;

	/* keyphraseness omitted */

	private double			occurrence;							// number of occurrence

	private double			frequency;

	private double			detectionConfidence;

	// internal use

	private String			toString;

	public ExtractedSurface(Doc doc, int textOffset, WikiSurface surface)
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
					surface.getSurface(),
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

	public svm_node[] toSvmInstanceForDisambiguation()
	{
		svm_node[] svmInst = constructSVMInstance(
					this.getWikiSurface().getConcepts().get(this.getWikiConcept()),
					this.getContextualRelatedness(),
					this.getContextQuality()
					);
		return svmInst;
	}

	public svm_node[] toSvmInstanceForDetection()
	{
		svm_node[] svmInst = constructSVMInstance(
					this.getWikiSurface().getConcepts().get(this.getWikiConcept()),
					this.getContextualRelatedness(),
					this.getContextQuality(),
					this.getDisambiguationConfidence(),
					this.getWikiSurface().getKeyphraseness(),
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
