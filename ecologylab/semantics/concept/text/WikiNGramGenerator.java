package ecologylab.semantics.concept.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecologylab.semantics.concept.utils.HtmlUtils;
import ecologylab.semantics.concept.utils.HtmlUtils.HtmlStrippingListener;

public class WikiNGramGenerator extends NGramGenerator implements HtmlStrippingListener
{

	/**
	 * All the anchors in the input Wikipedia article fragment.
	 */
	public Map<String, WikiAnchor>	anchors;

	public WikiNGramGenerator(String text)
	{
		super(text);
	}

	public WikiNGramGenerator(String text, int N)
	{
		super(text, N);
	}

	@Override
	protected String stripHtmlTags(String text)
	{
		anchors = new HashMap<String, WikiAnchor>();
		return HtmlUtils.stripHtmlTags(text, this);
	}

	@Override
	public void newHtmlTag(String tag, Map<String, String> attributes, String inner)
	{
		if (tag.equals("a") && attributes.containsKey("title"))
		{
			if (anchors.containsKey(inner))
			{
				anchors.get(inner).count++;
			}
			else
			{
				WikiAnchor anchor = new WikiAnchor(new Gram(inner), attributes.get("title"));
				anchors.put(inner, anchor);
			}
		}
	}

	public static void main(String[] args)
	{
		String text = "<b>Uncle Tupelo</b> was an <a href=\"../../../../articles/a/l/t/Alternative_country.html\" title=\"Alternative country\">alternative country</a> music group from <a href=\"../../../../articles/b/e/l/Belleville%2C_Illinois_adf8.html\" title=\"Belleville, Illinois\">Belleville</a>, <a href=\"../../../../articles/i/l/l/Illinois.html\" title=\"Illinois\">Illinois</a>, active between 1987 and 1994. <a href=\"../../../../articles/j/a/y/Jay_Farrar_227c.html\" title=\"Jay Farrar\">Jay Farrar</a>, <a href=\"../../../../articles/j/e/f/Jeff_Tweedy_ce20.html\" title=\"Jeff Tweedy\">Jeff Tweedy</a>, and <a href=\"../../../../articles/m/i/k/Mike_Heidorn_343c.html\" title=\"Mike Heidorn\">Mike Heidorn</a> formed the band after the lead singer of their previous band, The Primitives, left to attend college. The trio recorded three albums for <a href=\"../../../../articles/r/o/c/Rockville_Records_1a50.html\" class=\"mw-redirect\" title=\"Rockville Records\">Rockville Records</a>, before signing with <a href=\"../../../../articles/s/i/r/Sire_Records_8da1.html\" title=\"Sire Records\">Sire Records</a> and expanding to a five-piece. Shortly after the release of the band's major label debut album <i><a href=\"../../../../articles/a/n/o/Anodyne_%28album%29.html\" title=\"Anodyne (album)\">Anodyne</a></i>, Farrar announced his decision to leave the band due to a soured relationship with his co-songwriter Tweedy. Uncle Tupelo split on <a href=\"../../../../articles/m/a/y/May_1.html\" title=\"May 1\">May 1</a>, <a href=\"../../../../articles/1/9/9/1994.html\" title=\"1994\">1994</a>, after completing a farewell tour. Following the breakup, Farrar formed <a href=\"../../../../articles/s/o/n/Son_Volt_62fa.html\" title=\"Son Volt\">Son Volt</a> with Heidorn, while the remaining members continued as <a href=\"../../../../articles/w/i/l/Wilco.html\" title=\"Wilco\">Wilco</a>.";
		WikiNGramGenerator t = new WikiNGramGenerator(text);
		System.out.println(t.anchors);
		System.out.println(t.ngrams);
	}

}
