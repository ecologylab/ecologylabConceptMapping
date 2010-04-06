package ecologylab.semantics.conceptmapping.wikipedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLListFilter
{
	private static String[]	specialPageBeginnings	=
																								{ "index.html", "Image~", "User~", "User_talk~",
			"Talk~", "Category~", "Category_talk~", "Template~", "Template_talk~", "Image_talk~",
			"Wikipedia_talk~", "Wikipedia~"					};

	private static String[]	specialPageEndings		=
																								{ "(disambiguation).html" };

	private static Pattern	specialPagePattern		= Pattern
																										.compile("[A-Za-z0-9'_]*[Ll]ist_of_[A-Za-z0-9'_]+~");

	public static boolean isSpecialPage(String url)
	{
		int i = url.lastIndexOf('/');
		String pageName = url.substring(i + 1);

		for (String beginning : specialPageBeginnings)
		{
			if (pageName.startsWith(beginning))
				return true;
		}

		for (String ending : specialPageEndings)
		{
			if (pageName.endsWith(ending))
				return true;
		}

		Matcher m = specialPagePattern.matcher(url);
		return m.matches();
	}

	public static void main(String[] args) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter("filtered.lst"));

		BufferedReader br = new BufferedReader(new FileReader("Z:\\wikipedia-en-html\\html.lst"));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.trim().isEmpty())
				break;

			if (isSpecialPage(line))
				continue;

			bw.write(line);
			bw.newLine();
		}
		br.close();

		bw.close();
	}

}
