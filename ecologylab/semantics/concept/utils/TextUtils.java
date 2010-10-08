package ecologylab.semantics.concept.utils;

import junit.framework.Assert;

import org.junit.Test;

public class TextUtils
{

	public static String normalize(String text)
	{
		return text.toLowerCase().replaceAll("[^A-Za-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
	}
	
	@Test
	public void testNormalization()
	{
		String[] tests = {
				"This is a test case.",
				"There's some   th-th-th-thing...",
				"See @position.",
				"[<cite_link_label_group->]",
		};
		
		for (String test : tests)
		{
			System.out.format("|%s| -> |%s|\n", test, normalize(test));
		}
	}
	
	public static String removeWikiMessages(String wikiText)
	{
		return wikiText.replaceAll("\\[.*\\]", "");
	}
	
	@Test
	public void testRemoveWikiMessages()
	{
		String[] wikiTexts = {
				"The word dough is used as a slang term for money.[Ran out of custom link labels for group \"\".Define more in the [[MediaWiki:cite_link_label_group-]] message.]",
				"but are not roasted.[<cite_link_label_group->] Flat unleavened breads (flatbread) known",
		};
		
		for (String wikiText : wikiTexts)
		{
			System.out.println(removeWikiMessages(wikiText));
		}
	}

	public static String regexReplaceEscape(String s)
	{
		return s.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$");
	}
	
	@Test
	public void testRegexReplaceEscape()
	{
		String[] tests = {
				"abc\\def",
				"abc $5 def",
				"abc \\ def$5ghi",
				"abc$5def\\\\ghi",
		};
		
		String[] exps = {
				"abc\\\\def",
				"abc \\$5 def",
				"abc \\\\ def\\$5ghi",
				"abc\\$5def\\\\\\\\ghi",
		};
		
		for (int i = 0; i < tests.length; ++i)
		{
			Assert.assertEquals(exps[i], regexReplaceEscape(tests[i]));
		}
	}
	
	public static int nextWhitespaceIndex(String s, int offset)
	{
		while (offset < s.length() && !Character.isWhitespace(s.charAt(offset)))
			offset++;
		return offset;
	}
	
	public static int nextNonWhitespaceIndex(String s, int offset)
	{
		while (offset < s.length() && Character.isWhitespace(s.charAt(offset)))
			offset++;
		return offset;
	}
	
}
