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
				"[<cite_link_label_group->]",
		};
		
		for (String test : tests)
		{
			System.out.format("|%s| -> |%s|\n", test, normalize(test));
		}
	}
	
	public static String removeWikiMessages(String wikiText)
	{
		return wikiText.replaceAll("\\[.*\\]", "");
	}
	
	@Test
	public void testRemoveWikiMessages()
	{
		String[] wikiTexts = {
				"The word dough is used as a slang term for money.[Ran out of custom link labels for group \"\".Define more in the [[MediaWiki:cite_link_label_group-]] message.]",
				"but are not roasted.[<cite_link_label_group->] Flat unleavened breads (flatbread) known",
		};
		
		for (String wikiText : wikiTexts)
		{
			System.out.println(removeWikiMessages(wikiText));
		}
	}
	
}
