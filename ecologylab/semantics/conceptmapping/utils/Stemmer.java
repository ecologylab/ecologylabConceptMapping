/**
 * 
 */
package ecologylab.semantics.conceptmapping.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ecologylab.semantics.model.text.PorterStemmer;
/**
 * @author quyin
 *
 */
public class Stemmer
{
	public String stemEachWord(String s)
	{
		String[] terms = s.split("[ \t]+");
		System.out.println(terms.length + " terms found.");
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

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String line = "The chief value of money lies in the fact that one lives in a world in which it is overestimated.";
		Stemmer stm = new Stemmer();
		System.out.println(stm.stemEachWord(line));
	}
}
