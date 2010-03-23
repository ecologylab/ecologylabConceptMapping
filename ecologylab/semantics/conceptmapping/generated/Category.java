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

	 @xml_nested private MetadataString	catName;

/**
	null
**/ 

	 @xml_nested private MetadataParsedURL	catLink;

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
	Lazy Evaluation for catName
**/ 

public MetadataString	catName()
{
MetadataString	result	=this.catName;
if(result == null)
{
result = new MetadataString();
this.catName	=	 result;
}
return result;
}

/**
	Gets the value of the field catName
**/ 

public String getCatName(){
return catName().getValue();
}

/**
	Sets the value of the field catName
**/ 

public void setCatName( String catName )
{
this.catName().setValue(catName);
}

/**
	The heavy weight setter method for field catName
**/ 

public void hwSetCatName( String catName )
{
this.catName().setValue(catName);
rebuildCompositeTermVector();
 }
/**
	 Sets the catName directly
**/ 

public void setCatNameMetadata(MetadataString catName)
{	this.catName = catName;
}
/**
	Heavy Weight Direct setter method for catName
**/ 

public void hwSetCatNameMetadata(MetadataString catName)
{	 if(this.catName!=null && this.catName.getValue()!=null && hasTermVector())
		 termVector().remove(this.catName.termVector());
	 this.catName = catName;
	rebuildCompositeTermVector();
}
/**
	Lazy Evaluation for catLink
**/ 

public MetadataParsedURL	catLink()
{
MetadataParsedURL	result	=this.catLink;
if(result == null)
{
result = new MetadataParsedURL();
this.catLink	=	 result;
}
return result;
}

/**
	Gets the value of the field catLink
**/ 

public ParsedURL getCatLink(){
return catLink().getValue();
}

/**
	Sets the value of the field catLink
**/ 

public void setCatLink( ParsedURL catLink )
{
this.catLink().setValue(catLink);
}

/**
	The heavy weight setter method for field catLink
**/ 

public void hwSetCatLink( ParsedURL catLink )
{
this.catLink().setValue(catLink);
rebuildCompositeTermVector();
 }
/**
	 Sets the catLink directly
**/ 

public void setCatLinkMetadata(MetadataParsedURL catLink)
{	this.catLink = catLink;
}
/**
	Heavy Weight Direct setter method for catLink
**/ 

public void hwSetCatLinkMetadata(MetadataParsedURL catLink)
{	 if(this.catLink!=null && this.catLink.getValue()!=null && hasTermVector())
		 termVector().remove(this.catLink.termVector());
	 this.catLink = catLink;
	rebuildCompositeTermVector();
}}
