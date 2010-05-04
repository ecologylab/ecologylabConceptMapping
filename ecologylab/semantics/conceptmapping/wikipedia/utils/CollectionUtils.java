package ecologylab.semantics.conceptmapping.wikipedia.utils;

import java.util.Collection;

public class CollectionUtils
{
	public static int sum(Collection<Integer> collection)
	{
		int s = 0;
		for (int v : collection)
			s += v;
		return s;
	}
}
