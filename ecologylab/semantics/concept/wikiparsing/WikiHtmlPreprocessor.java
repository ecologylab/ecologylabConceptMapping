package ecologylab.semantics.concept.wikiparsing;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import ecologylab.semantics.concept.utils.TextUtils;

/**
 * Preprocess HTML codes before sending it to the HTML parser. Mainly removing wiki functions in
 * {{ }} and language links in the end.
 * 
 * @author quyin
 *
 */
public class WikiHtmlPreprocessor
{
	
	private static final Pattern pWikiFunction = Pattern.compile("\\{\\{[^}]*}}");
	
	private static final Pattern pWikiLanguageLinks = Pattern.compile("<p>\\s*(<a href=\"http://[a-z-]+\\.wikipedia\\.org/wiki/[^\"]+\">[a-z-]+:[^<]+</a>\\s*)+</p>");
	
	/**
	 * Preprocess HTML codes. Removing wiki functions and language links.
	 * 
	 * @param html
	 * @return
	 */
	public String preprocess(String html)
	{
		StringBuffer sb = new StringBuffer();
		
		html = html.replaceAll("\\s+", " ");
		
		Matcher m = pWikiFunction.matcher(html);
		while (m.find())
		{
			m.appendReplacement(sb, " ");
		}
		m.appendTail(sb);
		html = sb.toString();
		sb.setLength(0);
		
		m = pWikiLanguageLinks.matcher(html);
		while (m.find())
		{
			m.appendReplacement(sb, " ");
		}
		m.appendTail(sb);
		html = sb.toString();
		sb.setLength(0);
		
		html = html.replace("<div", "<span").replace("</div>", "</span>");
		
		return html;
	}
	
	@Test
	public void test() throws IOException
	{
		String html = TextUtils.loadTxtAsString("usa.html");
		String pp = preprocess(html);
		TextUtils.saveStringToTxt(pp, "usa1.html");
	}

}
