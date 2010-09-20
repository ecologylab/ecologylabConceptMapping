package ecologylab.semantics.concept.utils;

import org.junit.Test;

public class TextUtils
{

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
		};
		
		for (String test : tests)
		{
			System.out.format("|%s| -> |%s|\n", test, normalize(test));
		}
	}
}
