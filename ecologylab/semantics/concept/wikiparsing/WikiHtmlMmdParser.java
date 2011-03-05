package ecologylab.semantics.concept.wikiparsing;

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
import ecologylab.semantics.generated.library.GeneratedMetadataTranslationScope;
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
 * @author quyin
 * 
 */
public class WikiHtmlMmdParser implements WikiHtmlParser
{

	private static class MmdParser extends ParserBase
	{

		private MetaMetadataRepository	repository;

		private MetaMetadata						wikiMmd;

		private Tidy										tidy	= new Tidy();

		public MmdParser(InfoCollector infoCollector)
		{
			super(infoCollector);
			// TODO Auto-generated constructor stub

			repository = infoCollector.metaMetaDataRepository();
			wikiMmd = repository.getByTagName("wikipedia_page_for_parsing");
		}

		@Override
		public Document populateMetadata(SemanticActionHandler handler)
		{
			// TODO Auto-generated method stub
			return null;
		}

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

		@Override
		public TranslationScope getMetadataTranslationScope()
		{
			return GeneratedMetadataTranslationScope.get();
		}

	}

	private MmdParser	mmdParser;

	public WikiHtmlMmdParser()
	{
		File repo = Configs.getFile("prep.mmd_repository");
		MetaMetadataRepository repository = MetaMetadataRepository.load(repo);
		TranslationScope mdTScope = GeneratedMetadataTranslationScope.get();
		InfoCollector infoCollector = new MyInfoCollector(repository, mdTScope);
		mmdParser = new MmdParser(infoCollector);
	}

	@Override
	public WikipediaPageType parse(String wikiHtml)
	{
		return mmdParser.parse(wikiHtml);
	}

	@Test
	public void test() throws IOException, SIMPLTranslationException
	{
		String wikiHtml = TextUtils.loadTxtAsString("usa1.html");
		WikipediaPageType md = parse(wikiHtml);
		md.serialize(System.out);
	}

}
