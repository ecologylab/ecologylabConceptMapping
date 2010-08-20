package ecologylab.semantics.concept.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import libsvm.svm_node;

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
	
	public static <T1, T2> List<T2> convertList(List<T1> list)
	{
		List<T2> list2 = new ArrayList<T2>();
		for (T1 e : list)
		{
			list2.add((T2) e);
		}
		return list2;
	}
	
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
	
	public static <T> void randomPermute(List<T> list)
	{
		randomPermute(list, list.size());
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
