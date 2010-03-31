package ecologylab.semantics.conceptmapping.wikipedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

		BufferedReader br = new BufferedReader(new FileReader("Z:\\wikipedia-en-html\\test2.lst"));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.trim().isEmpty())
				break;

			String url = "http://localhost/wiki" + line.substring(2);
			System.out.println(url);
			ParsedURL purl = ParsedURL.getAbsolute(url);

			infoCollector.getContainerDownloadIfNeeded(null, purl, null, false, false, false);
		}

		while (infoCollector.getDownloadMonitor().toDownloadSize() > 0)
		{
			System.out.println("waiting for all the tasks done ...");
			Thread.sleep(1000);
		}
		
		ConceptPool.get().save();

		infoCollector.getDownloadMonitor().stop();
	}

	public static void main(String[] args) throws InterruptedException, IOException
	{
		WikipediaPageParsing parsing = new WikipediaPageParsing();
		parsing.parse();
	}
}
