package ecologylab.semantics.concept.preparation.parsing;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * The entry class to parsing Wikipedia dump.
 * 
 * @author quyin
 *
 */
public class WikiParsing
{

	private static int	count;

	private static long	ms;

	/**
	 * @param args
	 * @throws SAXException
	 * @throws IOException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws SAXException, IOException, SQLException,
			ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		if (args.length != 1)
		{
			System.err.println("args: <wikipedia-pages-article-dump-xml-file-path>");
			return;
		}

		// Debug.setLoggingFile("debug.log");

		String pargsArticleFilePath = args[0];

		count = 0;
		ms = System.currentTimeMillis();
		XMLReader xr = XMLReaderFactory.createXMLReader();
		WikiParsingSAXHandler wpsh = new WikiParsingSAXHandler()
		{
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
