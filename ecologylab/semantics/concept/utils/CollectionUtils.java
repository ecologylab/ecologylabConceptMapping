package ecologylab.semantics.concept.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollectionUtils
{
	
	public static <T extends Number> double sum(Collection<T> collection)
	{
		double s = 0;
		for (T v : collection)
			s += v.doubleValue();
		return s;
	}

	/**
	 * return the common sublist of 2 lists, assuming that they are ordered ascendingly.
	 * 
	 * @param <T>
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static <T extends Comparable> List<T> commonSublist(List<T> list1, List<T> list2)
	{
		int p = 0;
		int q = 0;

		List<T> rst = new ArrayList<T>();

		while (p < list1.size() && q < list2.size())
		{
			T o1 = list1.get(p);
			T o2 = list2.get(q);

			if (o1.compareTo(o2) == 0)
			{
				rst.add(o1);
				p++;
				q++;
			}
			else if (o1.compareTo(o2) < 0)
			{
				p++;
			}
			else if (o1.compareTo(o2) > 0)
			{
				q++;
			}
		}
		
		return rst;
	}
	
	public static void main(String[] args)
	{
		String[] a1 = new String[] {"aaa", "aab", "aad"};
		String[] a2 = new String[] {"aaa", "aac", "aad", "aae"};
		List<String> l1 = Arrays.asList(a1);
		List<String> l2 = Arrays.asList(a2);
		
		System.out.println(commonSublist(l1, l2));
	}
	
}
