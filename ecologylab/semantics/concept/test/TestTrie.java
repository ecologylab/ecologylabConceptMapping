package ecologylab.semantics.concept.test;

import org.junit.Test;

import ecologylab.semantics.concept.utils.Trie;

public class TestTrie
{

	@Test
	public void testMatch()
	{
		Trie trie = new Trie();
		trie.add("ab");
		trie.add("abc");
		trie.add("bc");
		trie.add("def");
//		trie.print(System.out);
		for (String found : trie.match("abc", 1))
		{
			System.out.println(found);
		}
	}

}
