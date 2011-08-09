package ecologylab.semantics.concept.preparation.parsing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;
import org.w3c.tidy.Tidy;

import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.TextUtils;
import ecologylab.semantics.connectors.InfoCollector;
import ecologylab.semantics.documentparsers.ParserBase;
import ecologylab.semantics.generated.library.RepositoryMetadataTranslationScope;
import ecologylab.semantics.generated.library.WikipediaPageType;
import ecologylab.semantics.metadata.builtins.Document;
import ecologylab.semantics.metametadata.MetaMetadata;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.example.MyInfoCollector;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.TranslationScope;

/**
 * Parsing wiki html with meta-metadata.
 * 
 * Thread safe.
 * 
 * @author quyin
 * 
 */
public class WikiHtmlMmdParser implements WikiHtmlParser
{

	private static MetaMetadata		wikiMmd;

	private static InfoCollector	theInfoCollector;

	static
	{
		File repo = Configs.getFile("prep.mmd_repository");
		MetaMetadataRepository repository = MetaMetadataRepository.loadFromDir(repo);
		wikiMmd = repository.getMMByName("wikipedia_page_for_parsing");
		theInfoCollector = new MyInfoCollector(repository, RepositoryMetadataTranslationScope.get());
	}

	/**
	 * The actual class that parses HTML codes.
	 * 
	 * @author quyin
	 * 
	 */
	private static class MmdParser extends ParserBase
	{

		private Tidy	tidy	= new Tidy();

		public MmdParser()
		{
			super(theInfoCollector);
		}

		@Override
		public Document populateMetadata(SemanticActionHandler handler)
		{
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * The entry method for parsing.
		 * 
		 * @param wikiHtml
		 * @return
		 */
		public WikipediaPageType parse(String wikiHtml)
		{
			WikipediaPageType page = new WikipediaPageType(wikiMmd);
			byte[] buf = wikiHtml.getBytes(Charset.forName("UTF-8"));
			ByteArrayInputStream in = new ByteArrayInputStream(buf);
			org.w3c.dom.Document doc = tidy.parseDOM(in, null);
			if (recursiveExtraction(wikiMmd, page, doc, null, null))
				return page;
			return null;
		}

		/**
		 * This method is overridden in order to provide the generated metadata translation scope
		 * without presence of a container.
		 */
		@Override
		public TranslationScope getMetadataTranslationScope()
		{
			return RepositoryMetadataTranslationScope.get();
		}

	}

	@Override
	public WikipediaPageType parse(String wikiHtml)
	{
		MmdParser mmdParser = new MmdParser();
		return mmdParser.parse(wikiHtml);
	}

	@Test
	public void test() throws IOException, SIMPLTranslationException
	{
		String wikiHtml = TextUtils.loadTxtAsString(new File("usa1.html"));
		WikipediaPageType md = parse(wikiHtml);
		md.serialize(System.out);
	}

}
