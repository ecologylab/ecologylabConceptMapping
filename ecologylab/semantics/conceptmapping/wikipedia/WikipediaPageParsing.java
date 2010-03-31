package ecologylab.semantics.conceptmapping.wikipedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.conceptmapping.generated.GeneratedMetadataTranslationScope;
import ecologylab.semantics.metametadata.example.MyInfoCollector;

public class WikipediaPageParsing
{
	public void parse() throws InterruptedException, IOException
	{
		Preparation.addSemanticAction(CreateConceptSemanticAction.class,
				AddConceptOutlinkSemanticAction.class, AddConceptCategorySemanticAction.class,
				FinishConceptSemanticAction.class);

		MyInfoCollector infoCollector = new MyInfoCollector(".", GeneratedMetadataTranslationScope
				.get());

		BufferedReader br = new BufferedReader(new FileReader("Z:\\wikipedia-en-html\\html.lst"));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.trim().isEmpty())
				break;

			String url = "http://achilles/wiki" + line.substring(2);
			if (isSpecialPage(url))
				continue;
			ParsedURL purl = ParsedURL.getAbsolute(url);

			infoCollector.getContainerDownloadIfNeeded(null, purl, null, false, false, false);

			Thread.sleep(1000);
		}

		while (infoCollector.getDownloadMonitor().toDownloadSize() > 0)
		{
			System.out.println("waiting for all the tasks done ...");
			Thread.sleep(1000);
		}

		ConceptPool.get().save();
		StringPool.closeAll();

		infoCollector.getDownloadMonitor().stop();
	}

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

	public static void main(String[] args) throws InterruptedException, IOException
	{
		WikipediaPageParsing parsing = new WikipediaPageParsing();
		parsing.parse();
	}
}
