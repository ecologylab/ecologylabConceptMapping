package ecologylab.semantics.concept.utils;

import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Normalize texts.
 * 
 * Thread safe.
 * 
 * @author quyin
 * 
 */
public class TextNormalizer
{

	private static final Pattern	pBrace1	= Pattern.compile("\\[[^\\]]*\\]");

	private static final Pattern	pBrace2	= Pattern.compile("\\{[^}]*}");

	private static final Pattern	pBrace3	= Pattern.compile("\\{\\{[^}]*}}");

	/**
	 * Normalize texts.
	 * 
	 * @param text
	 * @return
	 */
	public String normalize(String text)
	{
		if (text == null || text.isEmpty())
			return text;

		// add heading and training spaces
		String t = " " + text + " ";

		// remove things in [], {}, {{}}
		t = TextUtils.regexReplace(t, pBrace1, " ");
		t = TextUtils.regexReplace(t, pBrace2, " ");
		t = TextUtils.regexReplace(t, pBrace3, " ");

		// fold case
		t = t.toLowerCase(); // t.length() >= 3

		StringBuilder sb = new StringBuilder(t);
		for (int i = 1; i < sb.length() - 1; ++i)
		{
			char c1 = sb.charAt(i - 1);
			char c2 = sb.charAt(i);
			char c3 = sb.charAt(i + 1);

			if (c2 >= 'a' && c2 <= 'z' || c2 >= 'A' && c2 <= 'Z' || c2 >= '0' && c2 <= '9')
//			if (Character.isLetterOrDigit(c2))
				continue;
			if (Character.isWhitespace(c2))
				continue;

			if (c2 == '.' && Character.isDigit(c1) && Character.isDigit(c3))
				continue;
			if (c2 == '.' && Character.isLetter(c1) && Character.isUpperCase(c1))
				continue;
			if (c2 == '%' && Character.isDigit(c1))
				continue;
			if (c2 == '\'' && Character.isLetter(c1) && Character.isLetter(c3))
				continue;
			if (c2 == ',' && Character.isDigit(c1) && Character.isDigit(c3))
				sb.deleteCharAt(i);
			if (c2 == '-' && Character.isLetter(c1) && Character.isLetter(c3))
				continue;

			sb.setCharAt(i, ' ');
		}
		t = sb.toString();

		// remove extra whitespaces
		t = t.replaceAll("\\s+", " ").trim();
		
		return t;
	}

	@Test
	public void test() throws IOException
	{
		String txt = TextUtils.loadTxtAsString("usa.txt");
		TextUtils.saveStringToTxt(normalize(txt), "usa1.txt");
	}
}
