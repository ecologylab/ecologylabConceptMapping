package ecologylab.semantics.concept.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecologylab.generic.Debug;

public class HtmlUtils extends Debug
{

	/**
	 * Regex pattern used to capture HTML tags.
	 */
	public static final Pattern	pHtmlTag
	// = Pattern .compile("(?:<(\\w+)(.*?)[^/]?>(.*?)</\\1>)|(?:<(\\w+)(.*?)\\s*/>)");
	= Pattern .compile("<(\\w+)(\\s+\\w+=\"[^\"]*\")*(?:(?:\\s*/>)|(?:\\s*>(.*?)</\\1>))");

	/**
	 * Regex pattern used to capture HTML tag attributes.
	 */
	public static final Pattern	pHtmlAttributes	= Pattern.compile("(\\w+)=\"(.*?)\"");

	/**
	 * convert an attributes string (e.g. <code>attr1="value1" attr2="value2"</code>) into a map.
	 * 
	 * @param s
	 * @return
	 */
	public static Map<String, String> attributesAsMap(String s)
	{
		if (s == null)
			return null;
		
		Map<String, String> map = new HashMap<String, String>();
		Matcher m = pHtmlAttributes.matcher(s);
		while (m.find())
		{
			String aName = m.group(1);
			String aValue = m.group(2);
			map.put(aName, aValue);
		}
		return map;
	}

	/**
	 * strip html tags from input text.
	 * 
	 * @param text
	 * @param parseAttributes true if you need those attributes. false if you don't need them.
	 * @return text without html tags.
	 */
	public String stripHtmlTags(String text, boolean parseAttributes)
	{
		StringBuffer sb = new StringBuffer();

		Matcher m = pHtmlTag.matcher(text);
		while (m.find())
		{
			String tag = m.group(1);
			String attr = m.group(2);
			String inner = m.group(3);

			if (parseAttributes)
			{
				newHtmlTag(tag, attributesAsMap(attr), inner);
			}
			else
			{
				newHtmlTag(tag, null, inner);
			}

			StringBuffer strippedInner = new StringBuffer(" ");
			strippedInner.append((inner == null) ? "" : stripHtmlTags(inner, parseAttributes));
			strippedInner.append(" ");

			m.appendReplacement(sb, TextUtils.regexReplaceEscape(strippedInner.toString()));
		}
		m.appendTail(sb);

		return sb.toString();
	}

	/**
	 * new html tag hook. invoked every time a new html tag is parsed.
	 * 
	 * @param tag
	 * @param attributes if parseAttributes is set to false, this will be null.
	 * @param inner null if no inner structures (e.g. <code>&lt;br /&gt;</code>).
	 */
	public void newHtmlTag(String tag, Map<String, String> attributes, String inner)
	{
		
	}
	
	public static void main(String[] args) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = new BufferedReader(new FileReader("ecologylab/semantics/concept/utils/test.wiki"));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			sb.append(line);
			sb.append(' ');
		}
		
		String test = sb.toString();
		HtmlUtils hu = new HtmlUtils();
		System.out.println(hu.stripHtmlTags(test, true));
	}
}
