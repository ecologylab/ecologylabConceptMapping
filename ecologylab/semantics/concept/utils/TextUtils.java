package ecologylab.semantics.concept.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

public class TextUtils
{

	/**
	 * A handy util method that does regex replacement on input string.
	 * 
	 * @param input
	 * @param pattern
	 * @param replace
	 * @return
	 */
	public static String regexReplace(String input, Pattern pattern, String replace)
	{
		StringBuffer sb = new StringBuffer();
		Matcher m = pattern.matcher(input);
		while (m.find())
		{
			m.appendReplacement(sb, replace);
		}
		m.appendTail(sb);
		return sb.toString();
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

	/**
	 * return the next position of a whitespace character in a given string, starting from a given
	 * offset.
	 * 
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
	 * 
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
				int n = text.substring(p).indexOf(s);
				if (n < 0)
					break;
				p += n + 1;
				count++;
			}
		}
	
		return count;
	}

	/**
	 * load a whole text file into a string.
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static String loadTxtAsString(File f) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(f));
		while ((line = br.readLine()) != null)
		{
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}

	public static List<String> loadTxtAsList(File f, boolean sort) throws IOException
	{
		List<String> list = new ArrayList<String>();
		BufferedReader br;
		br = new BufferedReader(new FileReader(f));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			list.add(line.trim());
		}
		if (sort)
			Collections.sort(list);
		return list;
	}

	public static void saveStringToTxt(String string, String filePath) throws FileNotFoundException
	{
		PrintWriter pw = new PrintWriter(new File(filePath));
		pw.write(string);
		pw.close();
	}

	public static String getWords(String text, int offset, int wordCount)
	{
		StringBuilder sb = new StringBuilder();
		int n = 0;
		for (int i = offset; i < text.length(); ++i)
		{
			char c = text.charAt(i);
			if (c == ' ')
				n++;
			if (n == wordCount)
				return sb.toString();
			else
				sb.append(c);
		}
		return sb.toString();
	}

	@Test
	public void testRegexReplaceEscape()
	{
		String[] tests =
		{
				"abc\\def",
				"abc $5 def",
				"abc \\ def$5ghi",
				"abc$5def\\\\ghi",
		};

		String[] exps =
		{
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

	@Test
	public void testCount()
	{
		String[] tests =
		{
				"a bcd efg abba",
				" ahaha this is another one",
				"and another another one ",
				" and at last ... ",
		};
		int[] results =
		{ 3, 5, 4, 5 };
		for (int i = 0; i < tests.length; ++i)
		{
			int r = count(tests[i], " ");
			Assert.assertEquals(results[i], r);
		}
	}

	@Test
	public void testGetWords()
	{
		String test1 = "this is an example";
		String test2 = "and";
		String test3 = "another one ";

		Assert.assertEquals("this is an example", getWords(test1, 0, 4));
		Assert.assertEquals("this is an", getWords(test1, 0, 3));
		Assert.assertEquals("this is", getWords(test1, 0, 2));
		Assert.assertEquals("this", getWords(test1, 0, 1));
		Assert.assertEquals("is an example", getWords(test1, 5, 3));
		Assert.assertEquals("an example", getWords(test1, 8, 2));
		Assert.assertEquals("an example", getWords(test1, 8, 3));
		Assert.assertEquals("", getWords(test1, 100, 1));
		Assert.assertEquals("and", getWords(test2, 0, 1));
		Assert.assertEquals("", getWords(test2, 0, 0));
		Assert.assertEquals("another", getWords(test3, 0, 1));
		Assert.assertEquals("another one", getWords(test3, 0, 2));
	}

}
