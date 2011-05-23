package ecologylab.semantics.concept.utils;

import java.io.IOException;
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

	public static final char	DUMMY_HEAD_CHAR	= 0x1;

	private class Node
	{
		public char	ch;

		public T		value				= null;

		public Node	firstChild	= null;

		public Node	nextSibling	= null;

		public Node(char ch)
		{
			this(ch, null);
		}

		public Node(char ch, T value)
		{
			this.ch = ch;
			this.value = value;
		}
	}

	/**
	 * dummy head of the tree
	 */
	private Node	root				= new Node(DUMMY_HEAD_CHAR);

	private int		countNodes	= 0;
	
	private boolean warnDuplicateKey = true;

	public int getCountNodes()
	{
		return countNodes;
	}
	
	public void setWarnDuplicateKey(boolean warnDuplicateKey)
	{
		this.warnDuplicateKey = warnDuplicateKey;
	}

	/**
	 * traverse through the prefix tree using a given key (string), create nodes along the path when
	 * necessary, and return the destination node
	 * 
	 * @param key
	 * @param createNewNodes
	 * @return
	 */
	private Node traverse(String key, boolean createNewNodes)
	{
		Node current = root;
		for (int i = 0; i < key.length(); ++i)
		{
			char ch = key.charAt(i);

			Node p = findNode(current.firstChild, ch);
			if (p == null)
			{
				if (createNewNodes)
				{
					current.firstChild = insertNode(current.firstChild, ch);
					p = findNode(current.firstChild, ch);
					countNodes++;
				}
				else
				{
					return null;
				}
			}
			current = p;
		}
		return current;
	}

	/**
	 * find a specific node from a linked list
	 * 
	 * @param head
	 * @param ch
	 * @return
	 */
	private Node findNode(Node head, char ch)
	{
		while (head != null && head.ch < ch)
			head = head.nextSibling;
		if (head == null || head.ch > ch)
			return null;
		return head;
	}

	/**
	 * insert a new node into a (sorted) linked list, and return the new head
	 * 
	 * @param head
	 * @param ch
	 * @return
	 */
	private Node insertNode(Node head, char ch)
	{
		if (head == null)
			return new Node(ch);
		if (ch == head.ch)
			throw new RuntimeException("inserting duplicate nodes into children list: " + ch);
		if (ch < head.ch)
		{
			Node newHead = new Node(ch);
			newHead.nextSibling = head;
			return newHead;
		}
		Node p = head;
		Node q = head.nextSibling;
		while (q != null && q.ch < ch)
		{
			p = q;
			q = q.nextSibling;
		}
		if (q != null && q.ch == ch)
			throw new RuntimeException("inserting duplicate nodes into children list: " + ch);
		p.nextSibling = new Node(ch);
		p.nextSibling.nextSibling = q;
		return head;
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
		if (warnDuplicateKey && leaf.value != null)
		{
			warning("duplicate key found, overwrite: " + key + " = " + value.toString());
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
		return leaf.value;
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

			current = findNode(current.firstChild, ch);
			if (current == null)
				return;
			if (current.value != null)
				result.put(sb.toString(), current.value);
		}
	}

	/**
	 * print the tree structure into an Appendable
	 * 
	 * @param a
	 * @throws IOException
	 */
	public void printTree(Appendable a) throws IOException
	{
		printTreeHelper(root, a);
	}

	/**
	 * helper method for printTree()
	 * 
	 * @param node
	 * @param a
	 * @throws IOException
	 */
	private void printTreeHelper(Node node, Appendable a) throws IOException
	{
		if (node.ch == DUMMY_HEAD_CHAR)
			a.append('*');
		else
			a.append(node.ch);
		if (node.value != null)
		{
			a.append('[');
			a.append(node.value.toString());
			a.append(']');
		}
		a.append('(');
		Node p = node.firstChild;
		while (p != null)
		{
			if (p != node.firstChild)
				a.append(',');
			printTreeHelper(p, a);
			p = p.nextSibling;
		}
		a.append(')');
	}

	public static void main(String[] args) throws IOException
	{
		PrefixTree<Integer> pt = new PrefixTree<Integer>();
		pt.put("000 emergency", 4);
		pt.put("007 in new york", 5);
		pt.put("007 stage", 6);
		pt.put("00 gauge", 7);
		pt.put("00s", 8);

		pt.printTree(System.out);
		System.out.println();

		System.out.println(pt.get("000 emergency"));
		System.out.println(pt.get("007 in new york"));
		System.out.println(pt.get("007 stage"));
		System.out.println(pt.get("00 gauge"));
		System.out.println(pt.get("00s"));
		System.out.println(pt.get("xyz"));
	}

}
