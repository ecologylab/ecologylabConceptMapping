package ecologylab.semantics.conceptmapping.linkdetection.train;

import ecologylab.semantics.conceptmapping.linkdetection.FeatureExtractor;

public class WikiFeatureExtractor extends FeatureExtractor
{

	public WikiFeatureExtractor(String text)
	{
		super(text);
	}

	protected void init(String text)
	{
		ngGen = new WikiNGramGenerator(text);
	}

	@Override
	protected void addAnchors()
	{
		anchors = ((WikiNGramGenerator) ngGen).anchors;
	}
	
}
