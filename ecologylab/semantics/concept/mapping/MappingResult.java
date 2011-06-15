package ecologylab.semantics.concept.mapping;

import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;

public interface MappingResult
{

	WikiConcept getWikiConcept();
	
	WikiSurface getWikiSurface();
	
	double getConfidence();
	
}
