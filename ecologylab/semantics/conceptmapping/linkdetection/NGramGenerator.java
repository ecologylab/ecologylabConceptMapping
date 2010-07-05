package ecologylab.semantics.conceptmapping.linkdetection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NGramGenerator
{
	/**
	 * Regex pattern used to capture HTML tags.
	 */
	public static final Pattern	pHtmlTag	= Pattern.compile("<(\\w+)(.*?)>(.*?)</\\1>");

	static final int						DEFAULT_N	= 10;

	/**
	 * The parameter <i>n</i> in n-gram generating.
	 */
	int													N;

	/**
	 * The processed context. All the HTML tags are stripped. All whitespaces and non-word characters
	 * are replaced by a space.
	 */
	public String								context;

	/**
	 * Generated n-grams.
	 */
	public List<Gram>						ngrams;

	public NGramGenerator(String text)
	{
		this(text, DEFAULT_N);
	}

	public NGramGenerator(String text, int N)
	{
		this.N = N;
		generate(text);
	}

	protected void generate(String text)
	{
		context = stripHtmlTags(text);
		ngrams = new ArrayList<Gram>();
		
		List<String> grams = new ArrayList<String>();
		Matcher m = Pattern.compile("([A-Za-z]+)|[^A-Za-z]").matcher(context);
		while (m.find())
		{
			grams.add(m.group(0));
		}

		for (int l = 1; l <= N; ++l)
		{
			int startPos = 0;
			for (int i = 0; i < grams.size() - l + 1; ++i)
			{
				String s = getNGram(grams, i, l);

				Gram t = new Gram();
				t.context = context;
				t.text = s;
				t.startPos = startPos;
				t.length = s.length();
				ngrams.add(t);
				startPos += grams.get(i).length() + 1;
			}
		}
	}

	protected String stripHtmlTags(String text)
	{
		StringBuffer sb = new StringBuffer();

		Matcher m = pHtmlTag.matcher(text);
		while (m.find())
		{
			String inner = m.group(3);
			String strippedInner = stripHtmlTags(inner);

			m.appendReplacement(sb, strippedInner);
		}
		m.appendTail(sb);

		return sb.toString();
	}

	protected String getNGram(List<String> grams, int i, int count)
	{
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < count; ++k)
		{
			int j = i + k;
			if (k > 0)
				sb.append(" ");
			sb.append(grams.get(j));
		}
		return sb.toString();
	}

	public static void main(String[] args)
	{
		String text = "<b>Uncle Tupelo</b> was an <a href=\"../../../../articles/a/l/t/Alternative_country.html\" title=\"Alternative country\">alternative country</a> music group from <a href=\"../../../../articles/b/e/l/Belleville%2C_Illinois_adf8.html\" title=\"Belleville, Illinois\">Belleville</a>, <a href=\"../../../../articles/i/l/l/Illinois.html\" title=\"Illinois\">Illinois</a>, active between 1987 and 1994. <a href=\"../../../../articles/j/a/y/Jay_Farrar_227c.html\" title=\"Jay Farrar\">Jay Farrar</a>, <a href=\"../../../../articles/j/e/f/Jeff_Tweedy_ce20.html\" title=\"Jeff Tweedy\">Jeff Tweedy</a>, and <a href=\"../../../../articles/m/i/k/Mike_Heidorn_343c.html\" title=\"Mike Heidorn\">Mike Heidorn</a> formed the band after the lead singer of their previous band, The Primitives, left to attend college. The trio recorded three albums for <a href=\"../../../../articles/r/o/c/Rockville_Records_1a50.html\" class=\"mw-redirect\" title=\"Rockville Records\">Rockville Records</a>, before signing with <a href=\"../../../../articles/s/i/r/Sire_Records_8da1.html\" title=\"Sire Records\">Sire Records</a> and expanding to a five-piece. Shortly after the release of the band's major label debut album <i><a href=\"../../../../articles/a/n/o/Anodyne_%28album%29.html\" title=\"Anodyne (album)\">Anodyne</a></i>, Farrar announced his decision to leave the band due to a soured relationship with his co-songwriter Tweedy. Uncle Tupelo split on <a href=\"../../../../articles/m/a/y/May_1.html\" title=\"May 1\">May 1</a>, <a href=\"../../../../articles/1/9/9/1994.html\" title=\"1994\">1994</a>, after completing a farewell tour. Following the breakup, Farrar formed <a href=\"../../../../articles/s/o/n/Son_Volt_62fa.html\" title=\"Son Volt\">Son Volt</a> with Heidorn, while the remaining members continued as <a href=\"../../../../articles/w/i/l/Wilco.html\" title=\"Wilco\">Wilco</a>.";
		NGramGenerator t = new NGramGenerator(text);
		System.out.println(t.context);
		System.out.println();
		System.out.println(t.ngrams);
	}
	
}
