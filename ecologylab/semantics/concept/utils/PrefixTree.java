package ecologylab.semantics.concept.utils;

import java.util.HashMap;
import java.util.Map;

import ecologylab.generic.Debug;

/**
 * A prefix tree implemented as a map. Used to hold frequent surfaces with number of senses.
 * 
 * @author quyin
 * 
 * @param <T>
 */
public class PrefixTree<T> extends Debug
{

	public static final String									allowedCharactersStr	= " abcdefghijklmnopqrstuvwxyz0123456789.%'-";

	public static final Map<Character, Integer>	allowedCharacters;

	static
	{
		allowedCharacters = new HashMap<Character, Integer>();
		for (int i = 0; i < allowedCharactersStr.length(); ++i)
		{
			allowedCharacters.put(allowedCharactersStr.charAt(i), i);
		}
	}

	private static class Node
	{
		public Object	value;

		public Node[]	children;
		
	}

	private Node	root				= new Node();

	private int		countNodes	= 0;

	public int getCountNodes()
	{
		return countNodes;
	}

	private Node traverse(String key, boolean createNewNodes)
	{
		Node current = root;
		for (int i = 0; i < key.length(); ++i)
		{
			char ch = key.charAt(i);
			Integer p = allowedCharacters.get(ch);
			if (p == null)
			{
				warning("unallowed character (will be replaced by a whitespace): " + ch);
				p = 0;
			}
			
			if (createNewNodes)
			{
				if (current.children == null)
					current.children = new Node[allowedCharacters.size()];
				if (current.children[p] == null)
				{
					Node newNode = new Node();
					countNodes++;
					current.children[p] = newNode;
				}
			}
			else
			{
				if (current.children == null || current.children[p] == null)
					return null;
			}
			
			current = current.children[p];
		}
		return current;
	}

	/**
	 * Put a key-value pair into the prefix tree. Not thread-safe.
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, T value)
	{
		Node leaf = traverse(key, true);
		if (leaf.value != null)
		{
			warning("duplicate key found, overwrite: " + key);
		}
		leaf.value = value;
	}

	/**
	 * Get the value given the key. Thread-safe.
	 * 
	 * @param key
	 * @return
	 */
	public T get(String key)
	{
		Node leaf = traverse(key, false);
		if (leaf == null)
			return null;
		return (T) leaf.value;
	}

	/**
	 * Match prefixes of input against keys in this prefix tree. Multiple hits will be collected into
	 * result. Thread-safe (given that result is owned by the caller).
	 * 
	 * @param input
	 * @param result
	 */
	public void prefixMatch(String input, int offset, Map<String, T> result)
	{
		Node current = root;
		StringBuilder sb = new StringBuilder();
		for (int i = offset; i < input.length(); ++i)
		{
			char ch = input.charAt(i);
			sb.append(ch);

			int p = allowedCharacters.get(ch);
			current = (current.children == null) ? null : current.children[p];
			if (current == null)
				return;
			if (current.value != null)
				result.put(sb.toString(), (T) current.value);
		}
	}

}
