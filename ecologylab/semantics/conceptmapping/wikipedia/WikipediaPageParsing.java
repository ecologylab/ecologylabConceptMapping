package ecologylab.semantics.conceptmapping.wikipedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.actions.NestedSemanticActionsTranslationScope;
import ecologylab.semantics.conceptmapping.generated.GeneratedMetadataTranslationScope;
import ecologylab.semantics.conceptmapping.generated.WikipediaPage;
import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.MetaMetadataTranslationScope;
import ecologylab.semantics.metametadata.example.MetadataCollector;
import ecologylab.semantics.metametadata.example.MyInfoCollector;
import ecologylab.xml.TranslationScope;

public class WikipediaPageParsing
{
	public void parse() throws InterruptedException, IOException
	{
		Preparation.addSemanticAction(CreateConceptOutlinkSemanticAction.class, AnalyzeParagraphSemanticAction.class);

		MyInfoCollector infoCollector = new MyInfoCollector(".", GeneratedMetadataTranslationScope
				.get());

		BufferedReader br = new BufferedReader(new FileReader("Z:\\wikipedia-en-html\\test.lst"));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.trim().isEmpty())
				break;
			
			String url = "http://localhost/wiki" + line.substring(2);
			System.out.println(url);
			ParsedURL purl = ParsedURL.getAbsolute(url);

			infoCollector.getContainerDownloadIfNeeded(null, purl, null, false, false, false);
			
			Thread.sleep(2000);
		}
		
		Thread.sleep(60000);
		
		infoCollector.getDownloadMonitor().stop();
	}

	public static void main(String[] args) throws InterruptedException, IOException
	{
		WikipediaPageParsing parsing = new WikipediaPageParsing();
		parsing.parse();
	}
}
