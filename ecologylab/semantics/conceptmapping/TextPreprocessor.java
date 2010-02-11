package ecologylab.semantics.conceptmapping;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecologylab.semantics.model.text.PorterStemmer;

public class TextPreprocessor
{
	private static Pattern	pattern	= Pattern.compile("[^a-zA-Z0-9]+");

	public static String filter(String s)
	{
		Matcher matcher = pattern.matcher(s);
		String result = matcher.replaceAll(" ");
		return result;
	}

	public static String stem(String s)
	{

		PorterStemmer porter = new PorterStemmer();
		porter.add(s.toCharArray(), s.length());
		porter.stem();
		return porter.toString();
	}

	public static List<Token> preprocess(String s)
	{
		List<Token> tokens = new ArrayList<Token>();
		for (String term : filter(s).split("\\s+"))
		{
			Token tk = new Token();
			tk.term.surface = term;
			tk.term.normForm = stem(term.toLowerCase());
			tokens.add(tk);
		}
		return tokens;
	}
	
	public static String joinTokenNormForms(List<Token> tokens)
	{
		String result = new String();
		for (Token tk : tokens)
		{
			result += tk.term.normForm + " ";
		}
		return result.trim();
	}
	
	public static String joinTokenSurfaceForms(List<Token> tokens)
	{
		String result = new String();
		for (Token tk : tokens)
		{
			result += tk.term.surface + " ";
		}
		return result.trim();
	}

	public static void main(String[] args)
	{
		String line = "Millions long for immortality who don't know what to do with themselves on a rainy Sunday afternoon.";
		List<Token> tokens = preprocess(line);
		System.out.println(tokens);
	}
}
