package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import ecologylab.generic.Debug;
import ecologylab.net.ParsedURL;
import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.generated.library.GeneratedMetadataTranslationScope;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.concept.wikiparsing.WikiInfoCollector.SemanticActionHandlerFactory;

public class WikiParsing extends Debug
{

	public static final String	wikiUrlPrefix								= "http://achilles.cse.tamu.edu/mediawiki/index.php/";

	public static final String	primaryConceptListFilePath	= "data/primary-concepts.lst";

	public static final String	repositoryPath							= "../ecologylabSemantics/repository";

	/**
	 * Parse current local mirrored Wikipedia collection, using predefined InfoCollector and semantic
	 * actions.
	 * 
	 * @param infoCollector
	 *          The InfoCollector used for parsing.
	 * @param nDownloadThread
	 *          How many threads are used for downloading; larger value can improve performance but
	 *          will cost more resources.
	 * @param fullSize
	 *          The upper bound of the to-download buffer size. Note that this is different from the
	 *          number of threads used for downloading.
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void parse(WikiInfoCollector infoCollector, int nDownloadThread, int fullSize)
			throws InterruptedException, IOException, SQLException
	{
		BufferedReader br = new BufferedReader(new FileReader(primaryConceptListFilePath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String concept = line.trim();
			String url = wikiUrlPrefix + concept;
			debug("parsing " + url + " ...");
			ParsedURL purl = ParsedURL.getAbsolute(url);
			if (infoCollector.getDownloadMonitor().toDownloadSize() > fullSize)
			{
				while (infoCollector.getDownloadMonitor().toDownloadSize() > fullSize / 5)
					Thread.sleep(1000);
			}
			infoCollector.getContainerDownloadIfNeeded(null, purl, null, false, false, false);
		}
		br.close();

		while (infoCollector.getDownloadMonitor().toDownloadSize() > 0)
		{
			System.out.println("waiting for all the tasks done ...");
			Thread.sleep(1000);
		}

		infoCollector.getDownloadMonitor().stop();
	}

	public static void parsingPass1() throws InterruptedException, IOException, SQLException
	{
		SemanticAction.register(LinkHandler1.class);

		MetaMetadataRepository repository = MetaMetadataRepository.load(new File(repositoryPath));
		SemanticActionHandlerFactory sahFactory = new SemanticActionHandlerFactory()
		{
			@Override
			public SemanticActionHandler create()
			{
				return new SemanticActionHandler();
			}
		};
		WikiInfoCollector infoCollector = new WikiInfoCollector(repository,
				GeneratedMetadataTranslationScope.get(), sahFactory, 10);

		WikiParsing wp = new WikiParsing();
		wp.parse(infoCollector, 30, 300);
	}

	public static void parsingPass2()
	{

	}

	public static void main(String[] args) throws InterruptedException, IOException, SQLException
	{
		parsingPass1();
	}

}
