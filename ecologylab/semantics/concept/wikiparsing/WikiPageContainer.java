package ecologylab.semantics.concept.wikiparsing;

import java.io.IOException;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.connectors.ContentElement;
import ecologylab.semantics.documentparsers.DocumentParser;
import ecologylab.semantics.metametadata.example.MyContainer;

public class WikiPageContainer extends MyContainer
{
	
	public WikiPageContainer(ContentElement progenitor, WikiInfoCollector infoCollector,
			ParsedURL purl)
	{
		super(progenitor, infoCollector, purl);
	}
	
	@Override
	public void performDownload() throws IOException
	{
		if (infoCollector.isVisited(purl()))
			return;
		
		infoCollector.setVisited(this);
		DocumentParser parser = DocumentParser.connect(purl(), this, infoCollector,
				infoCollector.createSemanticActionHandler());
		if (parser != null)
			parser.parse();
	}

}
