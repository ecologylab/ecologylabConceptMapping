package ecologylab.semantics.concept.text;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Context
{

	private Map<String, WikiAnchor>	anchors		= new HashMap<String, WikiAnchor>();

	private Set<String>							surfaces	= new HashSet<String>();

	public void addUniquely(String surface, String concept)
	{
		if (!anchors.containsKey(concept))
		{
			WikiAnchor anchor = new WikiAnchor(surface, concept);
			anchors.put(concept, anchor);
			surfaces.add(surface);
		}
	}

	public void addAll(Context other)
	{
		for (String concept : other.getConcepts())
		{
			if (!anchors.containsKey(concept))
			{
				WikiAnchor anchor = other.get(concept);
				anchors.put(concept, anchor);
				surfaces.add(anchor.surface);
			}
		}
	}

	public WikiAnchor get(String concept)
	{
		if (!anchors.containsKey(concept))
			return null;
		return anchors.get(concept);
	}

	public int size()
	{
		return anchors.size();
	}

	public Collection<WikiAnchor> getAnchors()
	{
		return anchors.values();
	}

	public Set<String> getSurfaces()
	{
		return surfaces;
	}

	public Set<String> getConcepts()
	{
		return anchors.keySet();
	}

	@Override
	public String toString()
	{
		return "Context: " + anchors.toString();
	}

}
