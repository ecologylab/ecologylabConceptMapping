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

public class Anchor extends Metadata{


/**
	null
**/ 

	 @xml_nested private MetadataString	surface;

/**
	null
**/ 

	 @xml_nested private MetadataString	concept;

/**
	null
**/ 

	 @xml_nested private MetadataParsedURL	link;

/**
	Constructor
**/ 

public Anchor()
{
 super();
}

/**
	Constructor
**/ 

public Anchor(MetaMetadata metaMetadata)
{
super(metaMetadata);
}

/**
	Lazy Evaluation for surface
**/ 

public MetadataString	surface()
{
MetadataString	result	=this.surface;
if(result == null)
{
result = new MetadataString();
this.surface	=	 result;
}
return result;
}

/**
	Gets the value of the field surface
**/ 

public String getSurface(){
return surface().getValue();
}

/**
	Sets the value of the field surface
**/ 

public void setSurface( String surface )
{
this.surface().setValue(surface);
}

/**
	The heavy weight setter method for field surface
**/ 

public void hwSetSurface( String surface )
{
this.surface().setValue(surface);
rebuildCompositeTermVector();
 }
/**
	 Sets the surface directly
**/ 

public void setSurfaceMetadata(MetadataString surface)
{	this.surface = surface;
}
/**
	Heavy Weight Direct setter method for surface
**/ 

public void hwSetSurfaceMetadata(MetadataString surface)
{	 if(this.surface!=null && this.surface.getValue()!=null && hasTermVector())
		 termVector().remove(this.surface.termVector());
	 this.surface = surface;
	rebuildCompositeTermVector();
}
/**
	Lazy Evaluation for concept
**/ 

public MetadataString	concept()
{
MetadataString	result	=this.concept;
if(result == null)
{
result = new MetadataString();
this.concept	=	 result;
}
return result;
}

/**
	Gets the value of the field concept
**/ 

public String getConcept(){
return concept().getValue();
}

/**
	Sets the value of the field concept
**/ 

public void setConcept( String concept )
{
this.concept().setValue(concept);
}

/**
	The heavy weight setter method for field concept
**/ 

public void hwSetConcept( String concept )
{
this.concept().setValue(concept);
rebuildCompositeTermVector();
 }
/**
	 Sets the concept directly
**/ 

public void setConceptMetadata(MetadataString concept)
{	this.concept = concept;
}
/**
	Heavy Weight Direct setter method for concept
**/ 

public void hwSetConceptMetadata(MetadataString concept)
{	 if(this.concept!=null && this.concept.getValue()!=null && hasTermVector())
		 termVector().remove(this.concept.termVector());
	 this.concept = concept;
	rebuildCompositeTermVector();
}
/**
	Lazy Evaluation for link
**/ 

public MetadataParsedURL	link()
{
MetadataParsedURL	result	=this.link;
if(result == null)
{
result = new MetadataParsedURL();
this.link	=	 result;
}
return result;
}

/**
	Gets the value of the field link
**/ 

public ParsedURL getLink(){
return link().getValue();
}

/**
	Sets the value of the field link
**/ 

public void setLink( ParsedURL link )
{
this.link().setValue(link);
}

/**
	The heavy weight setter method for field link
**/ 

public void hwSetLink( ParsedURL link )
{
this.link().setValue(link);
rebuildCompositeTermVector();
 }
/**
	 Sets the link directly
**/ 

public void setLinkMetadata(MetadataParsedURL link)
{	this.link = link;
}
/**
	Heavy Weight Direct setter method for link
**/ 

public void hwSetLinkMetadata(MetadataParsedURL link)
{	 if(this.link!=null && this.link.getValue()!=null && hasTermVector())
		 termVector().remove(this.link.termVector());
	 this.link = link;
	rebuildCompositeTermVector();
}}
