package ecologylab.semantics.concept.test;

import java.io.File;
import java.io.IOException;

import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.TextUtils;
import ecologylab.semantics.concept.wikiparsing.WikiHtmlMmdParser;
import ecologylab.semantics.connectors.InfoCollector;
import ecologylab.semantics.generated.library.GeneratedMetadataTranslationScope;
import ecologylab.semantics.generated.library.WikipediaPageType;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.metametadata.example.MyInfoCollector;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.TranslationScope;

public class TestWikiHtmlMmdParser
{

	WikiHtmlMmdParser	parser;

	public void test() throws SIMPLTranslationException, IOException
	{
		File repo = Configs.getFile("mmd_repository");
		MetaMetadataRepository repository = MetaMetadataRepository.load(repo);
		TranslationScope mdTScope = GeneratedMetadataTranslationScope.get();
		InfoCollector infoCollector = new MyInfoCollector(repository, mdTScope);
		parser = new WikiHtmlMmdParser(infoCollector);
		String wikiHtml = TextUtils.loadTxtAsString("usa1.html");
		WikipediaPageType md = parser.parse(wikiHtml);
		md.serialize(System.out);
	}

	public static void main(String[] args) throws SIMPLTranslationException, IOException
	{
		TestWikiHtmlMmdParser t = new TestWikiHtmlMmdParser();
		t.test();
	}

}
