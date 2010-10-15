package ecologylab.semantics.concept.utils;

import junit.framework.Assert;

import org.junit.Test;

public class TextUtils
{

	/**
	 * normalize input text to lower case alpha-numeric forms separated by a single space.
	 * note that in treat-redirects-as-wikilinks.sql, there is a postgre-sql version of this method.
	 * using a postgre-sql version can improve performance significantly.
	 * @param text
	 * @return
	 */
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
	
	/**
	 * preprocessing a string for Matcher.appendReplacement(), or other methods related to regex
	 * replacement. the problem is that bare '\' or '$' will cause problem for these methods, since
	 * they are treated as regex notations. thus we have to escape them first.
	 * 
	 * @param s
	 * @return
	 */
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
	
	/**
	 * return the next position of a whitespace character in a given string, starting from a given
	 * offset.
	 * @param s
	 * @param offset
	 * @return the (absolute) position index of the next whitespace character.
	 */
	public static int nextWhitespaceIndex(String s, int offset)
	{
		while (offset < s.length() && !Character.isWhitespace(s.charAt(offset)))
			offset++;
		return offset;
	}
	
	/**
	 * return the next position of a non-whitespace character in a given string, starting from a given
	 * offset.
	 * @param s
	 * @param offset
	 * @return the (absolute) position index of the next non-whitespace character.
	 */
	public static int nextNonWhitespaceIndex(String s, int offset)
	{
		while (offset < s.length() && Character.isWhitespace(s.charAt(offset)))
			offset++;
		return offset;
	}

	/**
	 * return the number of a given pattern (not regex) occurring in text.
	 * 
	 * @param text
	 * @param s
	 * @return
	 */
	public static int count(String text, String s)
	{
		int count = 0;
		
		if (text != null && s != null)
		{
			int p = 0;
			while (p < text.length())
			{
				int n = s.substring(p).indexOf(s);
				if (n < 0)
					break;
				p += n;
			}
		}
		
		return count;
	}
	
}
