package ecologylab.semantics.concept.utils;

import java.util.Collection;
import java.util.List;

public class CollectionUtils
{
	
	public static int sum(Collection<Integer> collection)
	{
		int s = 0;
		for (int v : collection)
			s += v;
		return s;
	}

	/**
	 * randomly pick n elements from a list and permute them. top n positions of the list is used to
	 * store these elements.
	 * 
	 * @param <T>
	 * @param list
	 * @param n
	 */
	public static <T> void randomPermute(List<T> list, int n)
	{
		int size = list.size();
		assert size >= n : "illegal n: n = " + n + " > list.size() = " + size;
		for (int i = 0; i < n; ++i)
		{
			int j = i + (int) (Math.random() * (size - i));
			T tmp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, tmp);
		}
	}
	
	/**
	 * randomly permute a list.
	 * 
	 * @param <T>
	 * @param list
	 */
	public static <T> void randomPermute(List<T> list)
	{
		randomPermute(list, list.size());
	}
	
}
