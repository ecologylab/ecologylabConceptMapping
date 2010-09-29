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
import ecologylab.semantics.metametadata.example.MyInfoCollector;
import ecologylab.semantics.metametadata.example.SemanticActionHandlerFactory;
import ecologylab.semantics.tools.SimpleTimer;

public class WikiParsing extends Debug
{

	public static final String	wikiUrlPrefix											= "http://achilles.cse.tamu.edu/mediawiki/index.php/";

	public static final String	defaultPrimaryConceptListFilePath	= "data/primary-concepts.lst";

	public static final String	defaultRepositoryPath							= "../ecologylabSemantics/repository";

	private MyInfoCollector			infoCollector;

	private int									maxToDownloadBufferSize;

	/**
	 * @param infoCollector
	 *          The InfoCollector used for parsing.
	 * @param numDownloadThread
	 *          How many threads are used for downloading; larger value can improve performance but
	 *          will cost more resources.
	 * @param maxToDownloadBufferSize
	 *          The upper bound of the to-download buffer size. Note that this is different from the
	 *          number of threads used for downloading.
	 */
	public WikiParsing(MyInfoCollector infoCollector, int maxToDownloadBufferSize)
	{
		this.infoCollector = infoCollector;
		this.maxToDownloadBufferSize = maxToDownloadBufferSize;
	}

	/**
	 * Parse current local mirrored Wikipedia collection, using predefined InfoCollector and semantic
	 * actions.
	 * 
	 * @param primaryConceptListFilePath
	 *          The path to a list file containing primary concept names.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void parse(String primaryConceptListFilePath) throws InterruptedException, IOException,
			SQLException
	{
		SimpleTimer.get();
		
		BufferedReader br = new BufferedReader(new FileReader(primaryConceptListFilePath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String concept = line.trim();
			String url = wikiUrlPrefix + concept;
			debug("parsing " + url + " ...");
			ParsedURL purl = ParsedURL.getAbsolute(url);
			if (infoCollector.getDownloadMonitor().toDownloadSize() > maxToDownloadBufferSize)
			{
				while (infoCollector.getDownloadMonitor().toDownloadSize() > maxToDownloadBufferSize / 5)
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
		
		SimpleTimer.closeAll();
	}

	public static void parsingPass1(String repositoryPath, String primaryConceptListFilePath,
			int numThreads, int maxDownloadBufferSize) throws InterruptedException, IOException,
			SQLException
	{
		SemanticAction.register(LinkHandler1.class);

		MetaMetadataRepository repository = MetaMetadataRepository.load(new File(repositoryPath));
		SemanticActionHandlerFactory semanticActionHandlerFactory = new SemanticActionHandlerFactory()
		{
			@Override
			public SemanticActionHandler create()
			{
				return new SemanticActionHandler();
			}
		};
		MyInfoCollector infoCollector = new MyInfoCollector(repository,
				GeneratedMetadataTranslationScope.get(), semanticActionHandlerFactory, numThreads);

		WikiParsing wp = new WikiParsing(infoCollector, maxDownloadBufferSize);
		wp.parse(primaryConceptListFilePath);
	}

	public static void parsingPass2()
	{

	}

	public static void main(String[] args) throws InterruptedException, IOException, SQLException
	{
		if (args.length == 0)
		{
			System.err
					.println("arguments: <repository-path> <concept-list-file-path> <num-threads> <max-download-buffer-size>");
			return;
		}

		String repositoryPath = args[0];
		String conceptListFilePath = args[1];
		int numThreads = Integer.parseInt(args[2]);
		int maxDownloadBufferSize = Integer.parseInt(args[3]);

		parsingPass1(repositoryPath, conceptListFilePath, numThreads, maxDownloadBufferSize);
	}

}
