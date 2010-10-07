package ecologylab.semantics.concept.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

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
	
	/**
	 * convert List<T1> to List<T2>.
	 * 
	 * @param <T1>
	 * @param <T2>
	 * @param list
	 * @return
	 */
	public static <T1, T2> List<T2> convertList(List<T1> list)
	{
		List<T2> list2 = new ArrayList<T2>();
		for (T1 e : list)
		{
			list2.add((T2) e);
		}
		return list2;
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
	
	/**
	 * do a binary search for an element. the array should be sorted first.
	 * 
	 * @param <T>
	 * @param element
	 * @param array
	 * @return
	 */
	public static <T extends Comparable<T>> boolean binarySearch(T element, T[] array)
	{
		return binarySearch(element, array, 0, array.length);
	}
	
	/**
	 * do a binary search for an element in a range of an array. this part of array should be sorted
	 * first.
	 * 
	 * @param <T>
	 * @param element
	 * @param array
	 * @param begin
	 * @param end
	 * @return
	 */
	public static <T extends Comparable<T>> boolean binarySearch(T element, T[] array, int begin, int end)
	{
		if (end <= begin)
			return false;
			
		if (end - begin == 1)
			return array[begin].equals(element);
		
		int p = begin + (end - begin) / 2;
		T m = array[p];
		if (element.compareTo(m) < 0)
			return binarySearch(element, array, begin, p);
		else if (element.compareTo(m) > 0)
			return binarySearch(element, array, p + 1, end);
		else // element.compareTo(m) == 0
			return true;
	}

	@Test
	public void testCommonSublist()
	{
		String[] a1 = new String[] {"aaa", "aab", "aad"};
		String[] a2 = new String[] {"aaa", "aac", "aad", "aae"};
		List<String> l1 = Arrays.asList(a1);
		List<String> l2 = Arrays.asList(a2);
		
		System.out.println(commonSublist(l1, l2));
	}
	
	@Test
	public void testBinarySearch()
	{
		String[] a = { "1", "2", "3", "5", "6", "7", "9" };
		assertTrue(binarySearch("1", a));
		assertFalse(binarySearch("4", a));
		assertTrue(binarySearch("7", a));
		assertFalse(binarySearch("8", a));
	}

	/**
	 * 
	 * @param <T>
	 * @param element
	 * @param list
	 * @return true if found, false if not.
	 */
	public static <T extends Comparable<T>> boolean binarySearch(T element, List<T> list)
	{
		return binarySearch(element, list, 0, list.size());
	}

	/**
	 * 
	 * @param <T>
	 * @param element
	 * @param list
	 * @param begin
	 * @param end
	 * @return true if found, false if not.
	 */
	public static <T extends Comparable<T>> boolean binarySearch(T element, List<T> list, int begin, int end)
	{
		if (end <= begin)
			return false;
			
		if (end - begin == 1)
			return list.get(begin).equals(element);
		
		int p = begin + (end - begin) / 2;
		T m = list.get(p);
		if (element.compareTo(m) < 0)
			return binarySearch(element, list, begin, p);
		else if (element.compareTo(m) > 0)
			return binarySearch(element, list, p + 1, end);
		else // element.compareTo(m) == 0
			return true;
	}

}
