package ecologylab.semantics.concept.wikipedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.concept.wikipedia.metametadata.AddConceptCategorySemanticAction;
import ecologylab.semantics.concept.wikipedia.metametadata.AddConceptOutlinkSemanticAction;
import ecologylab.semantics.concept.wikipedia.metametadata.CreateConceptSemanticAction;
import ecologylab.semantics.concept.wikipedia.metametadata.FinishConceptSemanticAction;
import ecologylab.semantics.generated.library.GeneratedMetadataTranslationScope;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.example.MyContainer;
import ecologylab.semantics.metametadata.example.MyInfoCollector;

public class WikipediaPageParsing
{
	public final int fullSize = 2000;
	
	public void parse(String inputFilePath, int nDownloadThread) throws InterruptedException, IOException
	{
		SemanticAction.register(CreateConceptSemanticAction.class,
				AddConceptOutlinkSemanticAction.class, AddConceptCategorySemanticAction.class,
				FinishConceptSemanticAction.class);

		MyInfoCollector infoCollector = new MyInfoCollector(".", GeneratedMetadataTranslationScope.get());
		
		BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.trim().isEmpty())
				break;

			String url = "http://achilles/wiki" + line.substring(2);
			if (URLListFilter.isSpecialPage(url))
				continue;
			ParsedURL purl = ParsedURL.getAbsolute(url);

			if (infoCollector.getDownloadMonitor().toDownloadSize() > fullSize)
			{
				while (infoCollector.getDownloadMonitor().toDownloadSize() > fullSize / 5)
					;
			}
			infoCollector.getContainerDownloadIfNeeded(null, purl, null, false, false, false);
		}
		br.close();

		while (infoCollector.getDownloadMonitor().toDownloadSize() > 0)
		{
			System.out.println("waiting for all the tasks done ...");
			Thread.sleep(1000);
		}

		ConceptPool.get().save();
		StringPool.closeAll();

		infoCollector.getDownloadMonitor().stop();
	}

	public static void main(String[] args)
	{
		test2();
	}

	public static void test1(String[] args) throws InterruptedException, IOException
	{
		if (args.length != 2)
		{
			System.err.println("arguments: <wiki-page-list-file-path> <count-of-download-thread>");
			return;
		}
		
		String listFilePath = args[0];
		int nDownloadThread = Integer.valueOf(args[1]);
		
		WikipediaPageParsing parsing = new WikipediaPageParsing();
		parsing.parse(listFilePath, nDownloadThread);
	}
	
	public static void test2()
	{
		MetaMetadataRepository repo = MetaMetadataRepository.load(new File("../cf/config/semantics/metametadata"));
		WikiInfoCollector ic = new WikiInfoCollector(repo, GeneratedMetadataTranslationScope.get());
		ParsedURL purl = ParsedURL.getAbsolute("http://achellis.cse.tamu.edu/wiki/United_States");
		MyContainer c = ic.getContainerDownloadIfNeeded(null, purl, null, false, false, false);
		System.out.println(c);
	}
	
}
