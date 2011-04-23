package ecologylab.semantics.concept.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils
{

	/**
	 * A handy util method that does regex replacement on input string.
	 * 
	 * @param input
	 * @param pattern
	 * @param replace
	 * @return
	 */
	public static String regexReplace(String input, Pattern pattern, String replace)
	{
		StringBuffer sb = new StringBuffer();
		Matcher m = pattern.matcher(input);
		while (m.find())
		{
			m.appendReplacement(sb, replace);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * preprocessing a string for Matcher.appendReplacement(), or other methods related to regex
	 * replacement. the problem is that bare '\' or '$' will cause problem for these methods, since
	 * they are treated as regex notations. thus we have to escape them first.
	 * 
	 * @param s
	 * @return
	 */
	public static String regexReplaceEscape(String s)
	{
		return s.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$");
	}

	/**
	 * return the number of a given pattern (not regex) occurring in text.
	 * 
	 * @param text
	 * @param s
	 * @return
	 */
	public static int count(String text, String s)
	{
		int count = 0;

		if (text != null && s != null)
		{
			int p = 0;
			while (p < text.length())
			{
				int n = text.substring(p).indexOf(s);
				if (n < 0)
					break;
				p += n + 1;
				count++;
			}
		}

		return count;
	}

	/**
	 * load a whole text file into a string.
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static String loadTxtAsString(File f) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(f));
		while ((line = br.readLine()) != null)
		{
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * load a text file into a list, with each line as an item.
	 * 
	 * @param f
	 * @param sort
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadTxtAsList(File f, boolean sort) throws IOException
	{
		List<String> list = new ArrayList<String>();
		BufferedReader br;
		br = new BufferedReader(new FileReader(f));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			list.add(line.trim());
		}
		if (sort)
			Collections.sort(list);
		return list;
	}

	/**
	 * save a string as a text file.
	 * 
	 * @param string
	 * @param filePath
	 * @throws FileNotFoundException
	 */
	public static void saveStringToTxt(String string, String filePath) throws FileNotFoundException
	{
		PrintWriter pw = new PrintWriter(new File(filePath));
		pw.write(string);
		pw.close();
	}

}
