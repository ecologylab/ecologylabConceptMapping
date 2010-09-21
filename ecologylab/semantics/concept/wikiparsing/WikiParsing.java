package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ecologylab.net.ParsedURL;

public class WikiParsing
{
	
	public static final String	wikiUrlPrefix	= "http://achilles.cse.tamu.edu/mediawiki/index.php/";

	public final int	fullSize	= 2000;

	public void parse(WikiInfoCollector infoCollector, String urlListFilePath, int nDownloadThread)
	throws InterruptedException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(urlListFilePath));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.trim().isEmpty())
				break;

			ParsedURL purl = ParsedURL.getAbsolute(line);

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

	public static void main(String[] args)
	{
	}

}
