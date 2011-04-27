package wikxplorer.model;

import java.util.List;
import java.util.Map;

import wikxplorer.messages.Concept;
import wikxplorer.messages.Link;

/**
 * Abstraction of link suggestion algorithm.
 * 
 * @author quyin
 * 
 */
public interface LinkSuggestionStrategy
{

	/**
	 * Given a concept and the context, suggest links. Generally, suggested links should not contain
	 * concepts already in the context. Implementations can specify orders of suggestions.
	 * 
	 * @param concept
	 *          The concept whose links are to be suggested.
	 * @param context
	 *          The context (in the form of a collection of concepts) that could be used in
	 *          suggestion.
	 * @return
	 */
	List<Link> suggestLinks(Concept concept, Map<String, Concept> context);

}
