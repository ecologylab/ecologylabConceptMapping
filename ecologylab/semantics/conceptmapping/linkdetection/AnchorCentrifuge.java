package ecologylab.semantics.conceptmapping.linkdetection;

public class AnchorCentrifuge
{
	public static String centrifugate(CharSequence textWithAnchors)
	{
		StringBuilder sb = new StringBuilder();
		boolean inWord = false;
		
		int i = 0;
		while (i < textWithAnchors.length())
		{
			char c = textWithAnchors.charAt(i);
			
			if (Character.isWhitespace(c))
			{
				continue; // skip white spaces
			}
			
			if ()
		}
		
		
		
		for (int i = 0; i < textWithAnchors.length(); ++i)
		{
			
			// "This is <a href="...">example one</a> that you've read
			
			if (Character.isWhitespace(c)) // whitespaces
			{
				continue; // skipping whitespaces
			}
			else if (Character.isLetterOrDigit(c)) // letters or digits
			{
				
			}
			else if (c == '<') // tag
			{
				
			}
			else // other punctuations
			{
				
			}
		}
		
		return null;
	}
}
