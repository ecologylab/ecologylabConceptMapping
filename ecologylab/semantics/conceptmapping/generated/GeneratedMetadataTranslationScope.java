package ecologylab.semantics.conceptmapping.generated;

/**
This is a generated code. DO NOT edit or modify it.
 @author MetadataCompiler 

**/ 



import ecologylab.generic.HashMapArrayList;
import ecologylab.net.ParsedURL;
import ecologylab.semantics.conceptmapping.generated.*;
import ecologylab.semantics.generated.library.*;
import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metadata.MetadataBuiltinsTranslationScope;
import ecologylab.semantics.metadata.builtins.*;
import ecologylab.semantics.metadata.builtins.DebugMetadata;
import ecologylab.semantics.metadata.builtins.Document;
import ecologylab.semantics.metadata.builtins.Entity;
import ecologylab.semantics.metadata.builtins.Image;
import ecologylab.semantics.metadata.builtins.Media;
import ecologylab.semantics.metadata.scalar.*;
import ecologylab.semantics.metametadata.MetaMetadata;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.xml.ElementState.xml_tag;
import ecologylab.xml.TranslationScope;
import ecologylab.xml.types.element.Mappable;
import ecologylab.xml.xml_inherit;
import java.util.*;


/**
	
This is the tranlation scope class for generated files
.
**/ 

public class GeneratedMetadataTranslationScope
{protected static final Class TRANSLATIONS[]=
	{Outlink.class,

Category.class,

WikipediaPage.class,


};
 
public static TranslationScope get()
{
return TranslationScope.get("generated_metadata_translations", MetadataBuiltinsTranslationScope.get(), TRANSLATIONS);
}
}