package wikxplorer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wikxplorer.messages.Concept;
import wikxplorer.messages.Link;
import wikxplorer.messages.LinkGroup;

/**
 * The abstraction of grouping algorithm.
 * 
 * @author quyin
 * 
 */
public interface LinkGroupingStrategy
{

	/**
	 * Group links into clusters. This method should be stateless for re-use between different
	 * contexts.
	 * 
	 * @param links
	 *          all links.
	 * @param k
	 *          expected number of groups. if set to 0, the algorithm should determine its value.
	 * @param context
	 *          the context (a collection of concepts).
	 * @return link groups with relevance in a descending order.
	 */
	ArrayList<LinkGroup> groupLinks(List<Link> links, int k, Map<String, Concept> context);

}
