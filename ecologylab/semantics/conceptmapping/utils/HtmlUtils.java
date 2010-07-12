package ecologylab.semantics.conceptmapping.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils
{

	public static interface HtmlStrippingListener
	{
		void newHtmlTag(String tag, Map<String, String> attributes, String inner);
	}

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

	public static String stripHtmlTags(String text, HtmlStrippingListener... listeners)
	{
		StringBuffer sb = new StringBuffer();

		Matcher m = pHtmlTag.matcher(text);
		while (m.find())
		{
			String tag = m.group(1);
			String attr = m.group(2);
			String inner = m.group(3);

			if (listeners != null)
			{
				for (HtmlStrippingListener listener : listeners)
				{
					listener.newHtmlTag(tag, (attr == null) ? null : attributesAsMap(attr), inner);
				}
			}

			StringBuffer strippedInner = new StringBuffer(" ");
			strippedInner.append((inner == null) ? "" : stripHtmlTags(inner, listeners));
			strippedInner.append(" ");

			m.appendReplacement(sb, strippedInner.toString());
		}
		m.appendTail(sb);

		return sb.toString();
	}

	public static Map<String, String> attributesAsMap(String s)
	{
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

	public static void main(String[] args)
	{
		String test = "<p /><p><a href=\"../test.html\">test.</a></p><p/>test<p id=\"id\" />";
		System.out.println(HtmlUtils.stripHtmlTags(test, new HtmlStrippingListener()
		{

			@Override
			public void newHtmlTag(String tag, Map<String, String> attributes, String inner)
			{
				System.out.println("tag:\t" + tag);
				System.out.println("attr:\t" + attributes);
				System.out.println("inner:\t" + inner);
			}

		}));
	}
}
