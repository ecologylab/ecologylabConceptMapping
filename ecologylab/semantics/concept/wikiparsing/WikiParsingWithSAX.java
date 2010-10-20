package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.database.DatabaseFacade;

public class WikiParsingWithSAX
{

	private static int	count;

	private static long	ms;

	/**
	 * @param args
	 * @throws SAXException
	 * @throws IOException
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SAXException, IOException, SQLException
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
			primaryConceptList.add(line);
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
					if (Collections.binarySearch(primaryConceptList, title) >= 0)
						handleWikiText(title, wikiText);
					else
						Debug.warning(WikiParsingWithSAX.class, "not found in primary concept list: " + title);
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
		
		DatabaseFacade.get().close();
	}

}
