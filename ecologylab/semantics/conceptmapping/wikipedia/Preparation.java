package ecologylab.semantics.conceptmapping.wikipedia;

import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.tools.MetadataCompiler;
import ecologylab.xml.TranslationScope;
import ecologylab.xml.XMLTranslationException;

public class Preparation
{

	/**
	 * @param args
	 * @throws XMLTranslationException 
	 */
	public static void main(String[] args) throws XMLTranslationException
	{
		MetadataCompiler compiler = new MetadataCompiler(args);
		TranslationScope mmdTS = MetaMetadataRepository.META_METADATA_TSCOPE;
		compiler.compile("mmdRepo.xml", mmdTS, ".");
	}

}
