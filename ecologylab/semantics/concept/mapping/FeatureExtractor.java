package ecologylab.semantics.concept.mapping;

import java.util.List;

public interface FeatureExtractor
{

	void extractFeatures(Doc doc);
	
	void processInstance(int targetLabel, double... features);
	
}
