package wikxplorer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wikxplorer.messages.Concept;
import wikxplorer.messages.Link;
import wikxplorer.messages.LinkGroup;

public class NaiveLinkGroupingStrategy implements LinkGroupingStrategy
{

	/**
	 * A naive grouping strategy: place each link in a single group.
	 */
	@Override
	public ArrayList<LinkGroup> groupLinks(List<Link> links, int k, Map<String, Concept> context)
	{
		ArrayList<LinkGroup> groups = new ArrayList<LinkGroup>();

		for (Link l : links)
		{
			LinkGroup g = new LinkGroup();
			g.getLinks().put(l.getTitle(), l);
			g.setTopTitle(l.getTitle());
			g.setAverageRelatedness(l.getRelatedness());
			groups.add(g);
		}

		return groups;
	}

}
