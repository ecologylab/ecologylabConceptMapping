package ecologylab.semantics.conceptmapping.linkdetection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NGramGenerator
{
	/**
	 * Regex pattern used to capture HTML tags.
	 */
	static Pattern		pHtmlTag				= Pattern.compile("<(\\w+)(.*?)>(.*?)</\\1>");

	/**
	 * Regex pattern used to capture HTML tag attributes.
	 */
	static Pattern		pHtmlAttributes	= Pattern.compile("(\\w+)=\"(.*?)\"");

	/**
	 * The parameter <i>n</i> in n-gram generating.
	 */
	static int				N								= 5;

	/**
	 * The processed context. All the HTML tags are stripped. All whitespaces and non-word characters
	 * are replaced by a space.
	 */
	String						context;

	/**
	 * All the anchors in the input Wikipedia article fragment.
	 */
	List<WikiAnchor>	anchors;

	/**
	 * Generated n-grams.
	 */
	List<Gram>				ngrams;

	/**
	 * Process the input Wikipedia article fragment, find anchors and generate n-grams.
	 * 
	 * @param text
	 */
	public void generate(String text)
	{
		context = stripAnchors(text);
		generateHelper(context);
	}

	/**
	 * Preprocess the text before generating n-grams. The input string should have been stripped of
	 * all HTML tags. By default it just converts every whitespace and non-word character to a space
	 * for convenience -- word boundaries are easily located in this case.
	 * 
	 * @param s
	 * @return
	 */
	public String preprocess(String s)
	{
		return s.replaceAll("\\s|\\W", " ");
	}

	/**
	 * Helper method which actually generates n-grams.
	 * 
	 * @param text
	 */
	protected void generateHelper(String text)
	{
		ngrams = new ArrayList<Gram>();
		String[] grams = text.split(" ");

		for (int l = 1; l <= 5; ++l)
		{
			int startPos = 0;
			for (int i = 0; i < grams.length - l + 1; ++i)
			{
				String s = getNGram(grams, i, l);

				Gram t = new Gram();
				t.context = context;
				t.text = s;
				t.startPos = startPos;
				t.length = s.length();
				ngrams.add(t);
				startPos += grams[i].length() + 1;
			}
		}
	}

	protected String getNGram(String[] grams, int i, int count)
	{
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < count; ++k)
		{
			int j = i + k;
			if (k > 0)
				sb.append(" ");
			sb.append(grams[j]);
		}
		return sb.toString();
	}

	/**
	 * Find all Wikipedia anchors in a Wikipedia article fragment, and remove HTML tags. It will
	 * simply ignore and remove non-anchor (not &lt;a&gt;) HTML tags.
	 * 
	 * @param text
	 * @return
	 */
	protected String stripAnchors(String text)
	{
		text = stripNonAnchorHtmlTags(text);
		anchors = new ArrayList<WikiAnchor>();
		StringBuffer sb = new StringBuffer();

		Matcher m = pHtmlTag.matcher(text);
		while (m.find())
		{
			String tagName = m.group(1);
			String attributes = m.group(2);
			String inner = m.group(3);
			assert tagName.equals("a") : "non-anchor HTML tags found, check stripNonAnchorHtmlTags()!";
			m.appendReplacement(sb, inner);

			Map<String, String> attrMap = attributesAsMap(attributes);
			if (!attrMap.containsKey("title") || !attrMap.containsKey("href"))
			{
				System.err.println("non-wiki style anchor not processed: " + m.group(0));
				continue;
			}

			int length = inner.length();
			int start = sb.length() - length;
			WikiAnchor anchor = new WikiAnchor();
			anchor.text = inner;
			anchor.startPos = start;
			anchor.length = length;
			anchor.title = attrMap.get("title");
			anchor.href = attrMap.get("href");
			anchors.add(anchor);

		}
		m.appendTail(sb);

		String context = preprocess(sb.toString());
		for (WikiAnchor anchor : anchors)
		{
			anchor.context = context;
		}
		return context;
	}

	protected Map<String, String> attributesAsMap(String s)
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

	/**
	 * Remove all non-anchor (not &lt;a&gt;) HTML tags, recursively.
	 * 
	 * @param text
	 * @return
	 */
	protected String stripNonAnchorHtmlTags(String text)
	{
		StringBuffer sb = new StringBuffer();

		Matcher m = pHtmlTag.matcher(text);
		while (m.find())
		{
			String tagName = m.group(1);
			String attributes = m.group(2);
			String inner = m.group(3);
			String strippedInner = stripNonAnchorHtmlTags(inner);

			if (tagName.equals("a"))
			{
				m.appendReplacement(sb, "<a" + attributes + ">" + strippedInner + "</a>");
			}
			else
			{
				m.appendReplacement(sb, strippedInner);
			}
		}
		m.appendTail(sb);

		return sb.toString();
	}

	public static void main(String[] args)
	{
		NGramGenerator t = new NGramGenerator();
		t
				.generate("<b>Uncle Tupelo</b> was an <a href=\"../../../../articles/a/l/t/Alternative_country.html\" title=\"Alternative country\">alternative country</a> music group from <a href=\"../../../../articles/b/e/l/Belleville%2C_Illinois_adf8.html\" title=\"Belleville, Illinois\">Belleville</a>, <a href=\"../../../../articles/i/l/l/Illinois.html\" title=\"Illinois\">Illinois</a>, active between 1987 and 1994. <a href=\"../../../../articles/j/a/y/Jay_Farrar_227c.html\" title=\"Jay Farrar\">Jay Farrar</a>, <a href=\"../../../../articles/j/e/f/Jeff_Tweedy_ce20.html\" title=\"Jeff Tweedy\">Jeff Tweedy</a>, and <a href=\"../../../../articles/m/i/k/Mike_Heidorn_343c.html\" title=\"Mike Heidorn\">Mike Heidorn</a> formed the band after the lead singer of their previous band, The Primitives, left to attend college. The trio recorded three albums for <a href=\"../../../../articles/r/o/c/Rockville_Records_1a50.html\" class=\"mw-redirect\" title=\"Rockville Records\">Rockville Records</a>, before signing with <a href=\"../../../../articles/s/i/r/Sire_Records_8da1.html\" title=\"Sire Records\">Sire Records</a> and expanding to a five-piece. Shortly after the release of the band's major label debut album <i><a href=\"../../../../articles/a/n/o/Anodyne_%28album%29.html\" title=\"Anodyne (album)\">Anodyne</a></i>, Farrar announced his decision to leave the band due to a soured relationship with his co-songwriter Tweedy. Uncle Tupelo split on <a href=\"../../../../articles/m/a/y/May_1.html\" title=\"May 1\">May 1</a>, <a href=\"../../../../articles/1/9/9/1994.html\" title=\"1994\">1994</a>, after completing a farewell tour. Following the breakup, Farrar formed <a href=\"../../../../articles/s/o/n/Son_Volt_62fa.html\" title=\"Son Volt\">Son Volt</a> with Heidorn, while the remaining members continued as <a href=\"../../../../articles/w/i/l/Wilco.html\" title=\"Wilco\">Wilco</a>.");
		System.out.println(t.context + "\n");
		System.out.println(t.anchors + "\n");
		System.out.println(t.ngrams + "\n");
	}
}
