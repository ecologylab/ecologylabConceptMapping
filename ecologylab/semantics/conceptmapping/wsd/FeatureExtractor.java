package ecologylab.semantics.conceptmapping.wsd;

import java.util.List;

import ecologylab.semantics.conceptmapping.Token;

public interface FeatureExtractor
{
	List<String> extractFeatures(List<Token> context, int targetIndexBegin, int targetIndexEnd);
}
