package ecologylab.semantics.conceptmapping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecologylab.semantics.model.text.PorterStemmer;

public class TextPreprocessor
{
	private static Pattern pattern = Pattern.compile("[^a-zA-Z0-9]+");
	
	public static String filter(String s)
	{
		Matcher matcher = pattern.matcher(s);
		String result = matcher.replaceAll(" ");
		return result;
	}
	
	public static String stemEachWord(String s)
	{
		String[] terms = s.split("[ \t]+");
		String result = new String();
		
		for (String term : terms)
		{
			PorterStemmer porter = new PorterStemmer();
			porter.add(term.toCharArray(), term.length());
			porter.stem();
			result += porter.toString() + " ";
		}
		
		return result.trim();
	}
	
	public static String preprocess(String s)
	{
		return stemEachWord(filter(s.toLowerCase()));
	}
	
	public static void main(String[] args)
	{
		String line = "The chief value of money lies in the fact that one lives in a world in which it is overestimated.";
		System.out.println(stemEachWord(filter(line.toLowerCase())));
	}
}
