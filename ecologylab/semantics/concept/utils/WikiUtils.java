package ecologylab.semantics.concept.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

import ecologylab.generic.Debug;

public class WikiUtils extends Debug
{

	public static final Pattern	pWikiArea1	= Pattern.compile("\\{[^\\{\\}]*\\}");

	public static final Pattern	pWikiArea2	= Pattern.compile("\\{\\{[^\\{\\}]*\\}\\}");

	public static final Pattern	pWikiLink		= Pattern
																							.compile("\\[\\[([^:\\|\\[\\]]+)(?:\\|([^\\[\\]]+))?\\]\\]");

	private HtmlUtils						hu					= new HtmlUtils();

	/**
	 * filter wiki text. remove wiki markups (except links). remove html tags. extract wiki links.
	 * 
	 * @param wikiText
	 * @return filtered text.
	 */
	public String filter(String wikiText)
	{
		String text = StringEscapeUtils.unescapeXml(wikiText);
		String t1 = filterWikiMarkups(text);
		String t2 = hu.stripHtmlTags(t1, false);
		return extractWikiLinks(t2);
	}

	/**
	 * filtering out wiki markups.
	 * 
	 * @param wikiText
	 * @return
	 */
	public String filterWikiMarkups(String wikiText)
	{
		String s2 = filterWikiMarkupsHelper(pWikiArea2, wikiText);
		String s1 = filterWikiMarkupsHelper(pWikiArea1, s2);
		String t = s1.replaceAll("<br>", "");
		return t;
	}

	private String filterWikiMarkupsHelper(Pattern pattern, String text)
	{
		int len = text.length();
		String filtered = "";
		while (true)
		{
			filtered = pattern.matcher(text).replaceAll("");
			if (filtered.length() == len)
				break;
			len = filtered.length();
		}
		return filtered;
	}

	/**
	 * extract wiki links ([[destination]], [[destination|surface]]) from input wiki texts.
	 * newWikiLink() will be invoked when new wiki link is encountered.
	 * 
	 * @param wikiText
	 * @return text with wiki links converted to ordinary texts (e.g. [[a|b]] to b).
	 */
	public String extractWikiLinks(String wikiText)
	{
		StringBuffer sb = new StringBuffer();

		Matcher m = pWikiLink.matcher(wikiText);
		while (m.find())
		{
			String lit = m.group(0);
			String dest = m.group(1);
			String surface = m.group(2);
			if (surface == null)
				surface = dest;

			surface = TextUtils.normalize(surface);
			newWikiLink(lit, dest, surface);
			m.appendReplacement(sb, surface);
		}
		m.appendTail(sb);

		return sb.toString();
	}

	/**
	 * new wiki link hook. will be invoked every time new wiki link is parsed.
	 * 
	 * @param literal
	 * @param dest
	 * @param surface
	 */
	public void newWikiLink(String literal, String dest, String surface)
	{

	}
	
	@Test
	public void test1()
	{
		String wiki = "{{ something }} '''title''' body body <nowiki>body</nowiki> [[dest|surface]] [[again]].";
		String f = filter(wiki);
		Assert.assertEquals(" '''title''' body body  body  surface again.", f);
	}

	@Test
	public void test2() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(
				"ecologylab/semantics/concept/utils/test-anarchism.wiki"));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			sb.append(line);
			sb.append('\t');
		}
		String test = sb.toString();

		WikiUtils wu = new WikiUtils()
		{
			public void newWikiLink(String literal, String dest, String surface)
			{
				System.out.format("%s: %s -> %s\n", literal, surface, dest);
			}
		};

		long t0 = System.currentTimeMillis();
		for (int i = 0; i < 1000; ++i)
		{
			String after = wu.filter(test);
			System.out.println(after.replaceAll("\t", "\n"));
		}
		long t1 = System.currentTimeMillis();

		System.out.println("\nduration in ms: " + (t1 - t0));
	}

}
