package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class WikiParsingWithSAX
{

	private static int	count;
	
	private static long ms;

	/**
	 * @param args
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SAXException, IOException
	{
		if (args.length != 2)
		{
			System.err
					.println("args: <primary-concept-list-file-path> <wikipedia-pages-article-dump-xml-file-path>");
			return;
		}
		
		Debug.setLoggingFile("debug.log");

		String primaryConceptListFilePath = args[0];
		String pargsArticleFilePath = args[1];

		final List<String> primaryConceptList = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(primaryConceptListFilePath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			primaryConceptList.add(line.trim());
		}
		br.close();

		System.out.println("primary concept list loaded.");

		count = 0;
		ms = System.currentTimeMillis();
		XMLReader xr = XMLReaderFactory.createXMLReader();
		WikiParsingSAXHandler wpsh = new WikiParsingSAXHandler()
		{
			@Override
			protected void handleWikiText(String title, String wikiText)
			{
				try
				{
					if (CollectionUtils.binarySearch(title, primaryConceptList))
						super.handleWikiText(title, wikiText);
				}
				catch (Exception e)
				{
					Debug.warning(WikiParsingWithSAX.class, "error when processing " + title
							+ ", error message: " + e.getMessage());
				}
			}

			@Override
			protected void tick(String title)
			{
				count++;
				if (count % 1000 == 0)
				{
					long d = System.currentTimeMillis() - ms;
					System.out.println(count + " concepts processed ... time in ms: " + d);
					ms += d;
				}
			}
		};
		xr.setContentHandler(wpsh);

		xr.parse(new InputSource(new FileInputStream(pargsArticleFilePath)));
	}

}
