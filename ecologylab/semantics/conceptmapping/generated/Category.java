package ecologylab.semantics.conceptmapping.generated;

import ecologylab.semantics.metadata.scalar.MetadataString;
import ecologylab.semantics.metadata.scalar.MetadataParsedURL;
import ecologylab.semantics.metadata.scalar.MetadataStringBuilder;
import ecologylab.semantics.metadata.DebugMetadata;
import ecologylab.semantics.metadata.scalar.MetadataInteger;
import ecologylab.semantics.metadata.scalar.MetadataString;
import ecologylab.semantics.metadata.builtins.Document;
import ecologylab.semantics.metadata.builtins.Media;
import ecologylab.semantics.metadata.scalar.MetadataParsedURL;
import ecologylab.semantics.metadata.DebugMetadata;
import ecologylab.semantics.metadata.scalar.MetadataStringBuilder;
import ecologylab.semantics.metadata.builtins.Image;
import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metadata.scalar.MetadataInteger;
import ecologylab.semantics.metadata.builtins.Entity;

 import java.util.*;
import ecologylab.semantics.metametadata.MetaMetadata;
import ecologylab.net.ParsedURL;
import ecologylab.generic.HashMapArrayList;
import ecologylab.xml.xml_inherit;
import ecologylab.xml.types.element.Mappable;
import ecologylab.semantics.metadata.DefaultMetadataTranslationSpace;
import ecologylab.xml.TranslationScope;
import ecologylab.xml.ElementState.xml_tag;
 import ecologylab.semantics.metadata.builtins.Document;
import ecologylab.semantics.metadata.builtins.Media;
 import ecologylab.semantics.metadata.builtins.Image;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.generated.library.*;
import ecologylab.semantics.conceptmapping.generated.*;

@xml_inherit

public class Category extends Metadata{


/**
	null
**/ 

	 @xml_nested private MetadataString	category;

/**
	Constructor
**/ 

public Category()
{
 super();
}

/**
	Constructor
**/ 

public Category(MetaMetadata metaMetadata)
{
super(metaMetadata);
}

/**
	Lazy Evaluation for category
**/ 

public MetadataString	category()
{
MetadataString	result	=this.category;
if(result == null)
{
result = new MetadataString();
this.category	=	 result;
}
return result;
}

/**
	Gets the value of the field category
**/ 

public String getCategory(){
return category().getValue();
}

/**
	Sets the value of the field category
**/ 

public void setCategory( String category )
{
this.category().setValue(category);
}

/**
	The heavy weight setter method for field category
**/ 

public void hwSetCategory( String category )
{
this.category().setValue(category);
rebuildCompositeTermVector();
 }
/**
	 Sets the category directly
**/ 

public void setCategoryMetadata(MetadataString category)
{	this.category = category;
}
/**
	Heavy Weight Direct setter method for category
**/ 

public void hwSetCategoryMetadata(MetadataString category)
{	 if(this.category!=null && this.category.getValue()!=null && hasTermVector())
		 termVector().remove(this.category.termVector());
	 this.category = category;
	rebuildCompositeTermVector();
}}
