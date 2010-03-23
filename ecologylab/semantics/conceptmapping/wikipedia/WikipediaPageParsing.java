package ecologylab.semantics.conceptmapping.wikipedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.conceptmapping.generated.GeneratedMetadataTranslationScope;
import ecologylab.semantics.conceptmapping.generated.WikipediaPage;
import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.example.MetadataCollector;

public class WikipediaPageParsing
{
	public void parse() throws InterruptedException, IOException
	{
		WikiPageInfoCollector infoCollector = new WikiPageInfoCollector("mmdRepo.xml",
				MetaMetadataRepository.META_METADATA_TSCOPE, GeneratedMetadataTranslationScope.get());
		infoCollector.addListener(MetadataCollector.get());

		BufferedReader br = new BufferedReader(new FileReader("Z:\\wikipedia-en-html\\html.lst"));
		String line;
		while ((line = br.readLine()) != null)
		{
			String url = "http://localhost/wiki" + line.substring(2);
			System.out.println(url);
			ParsedURL purl = ParsedURL.getAbsolute(url);

			infoCollector.getContainerDownloadIfNeeded(null, purl, null, false, false, false);

			while (MetadataCollector.get().list().size() <= 0)
				; // wait

			for (Metadata metadata : MetadataCollector.get().list())
			{
				System.out.println(metadata.toString());
				if (metadata instanceof WikipediaPage)
				{
					WikipediaPage page = (WikipediaPage) metadata;
					System.out.println("title = " + page.getTitle());
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException
	{
		WikipediaPageParsing parsing = new WikipediaPageParsing();
		parsing.parse();
	}
}
