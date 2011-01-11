package ecologylab.semantics.concept.test;

import ecologylab.semantics.concept.detect.Concept;

public class TestCache
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Concept usa = new Concept("United States");
		usa.getInlinkConceptTitles();
		usa.getInlinkConceptTitles();
		usa.getInlinkConceptTitles();
	}

}
