package wikxplorer.messages;

import ecologylab.oodss.messages.DefaultServicesTranslations;
import ecologylab.serialization.TranslationScope;

public class WikxplorerMessageTranslationScope
{

	public static final String	NAME		= "wikxplorer_message_translation_scope";

	private static Class[]			classes	= {
																			Concept.class,
																			ConceptGroup.class,
																			RelatednessRequest.class,
																			RelatednessResponse.class,
																			SuggestionRequest.class,
																			SuggestionResponse.class,
																			UpdateContextRequest.class,
																			};

	public static TranslationScope get()
	{
		return TranslationScope.get(NAME, DefaultServicesTranslations.get(), classes);
	}

}
