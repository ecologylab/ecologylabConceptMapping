package wikxplorer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wikxplorer.messages.Concept;
import wikxplorer.messages.Link;
import wikxplorer.messages.LinkGroup;

public class KMedoidsLinkGroupingStrategy implements LinkGroupingStrategy
{
	
	public static final int MAX_ITERATION_NUMBER = 100;

	@Override
	public ArrayList<LinkGroup> groupLinks(List<Link> links, int k, Map<String, Concept> context)
	{
		if (k > links.size())
			k = links.size();
		if (k == 0)
			k = (int) Math.round(Math.sqrt(links.size() * 0.5)); // simple heuristic

		// randomly initialize medoids
		Set<Link> medoids = new HashSet<Link>();
		while (medoids.size() < k)
		{
			int i = (int) Math.floor(links.size() * Math.random());
			medoids.add(links.get(i));
		}

		// k-medoids
		Map<Link, List<Link>> clusters = new HashMap<Link, List<Link>>();
		int num = 0;
		boolean changed = true;
		while (changed && num < MAX_ITERATION_NUMBER)
		{
			changed = false;
			num++;
			System.out.println("k-medoid iteration " + num);
			
			// init
			clusters.clear();
			for (Link medoid : medoids)
				clusters.put(medoid, new ArrayList<Link>());
			
			// for each link, find the best medoid and relatedness
			for (Link l : links)
			{
				if (clusters.containsKey(l))
					continue;

				Link bestM = null;
				double bestR = Double.MAX_VALUE; // relatedness is actually distance
				for (Link medoid : clusters.keySet())
				{
					double relatedness = l.wikiConcept.getRelatedness(medoid.wikiConcept);
					if (relatedness < bestR)
					{
						bestM = medoid;
						bestR = relatedness;
						changed = true;
					}
				}
				
				clusters.get(bestM).add(l);
			}
			
			if (!changed)
				break;
			
			// calculate new medoid
			medoids.clear();
			for (Link medoid : clusters.keySet())
			{
				List<Link> ll = clusters.get(medoid);
				Link newMedoid = getNewMedoid(medoid, ll);
				if (!newMedoid.equals(clusters))
					changed = true;
				medoids.add(newMedoid);
			}
			for (Link newMedoid : medoids)
				if (!clusters.keySet().contains(newMedoid))
					changed = true;
		}

		// form results
		ArrayList<LinkGroup> results = new ArrayList<LinkGroup>();
		for (Link medoid : clusters.keySet())
		{
			List<Link> ll = clusters.get(medoid);
			ll.add(medoid);
			double totalRelatedness = 0;
			for (Link l : ll)
				totalRelatedness += l.getRelatedness();
			Collections.sort(ll);
			
			LinkGroup lg = new LinkGroup();
			for (Link l : ll)
				lg.getLinks().put(l.getTitle(), l);
			lg.setTopTitle(medoid.getTitle());
			lg.setAverageRelatedness(totalRelatedness / ll.size());
			
			results.add(lg);
		}
		Collections.sort(results);
		
		return results;
	}

	/**
	 * ll can't contain medoid!
	 * 
	 * @param medoid
	 * @param ll
	 * @return
	 */
	private Link getNewMedoid(Link medoid, List<Link> ll)
	{
		Link newM = medoid;
		double newR = 0;
		for (Link l : ll)
		{
			newR += medoid.wikiConcept.getRelatedness(l.wikiConcept);
		}
		
		for (Link l : ll)
		{
			double currR = medoid.wikiConcept.getRelatedness(l.wikiConcept);
			for (Link l2 : ll)
				if (!l.equals(l2))
					currR += l.wikiConcept.getRelatedness(l2.wikiConcept);
			if (currR < newR)
			{
				newR = currR;
				newM = l;
			}
		}
		
		return newM;
	}

}
