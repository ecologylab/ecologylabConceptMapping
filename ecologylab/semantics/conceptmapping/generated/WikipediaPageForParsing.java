package ecologylab.semantics.conceptmapping.generated;

/**
This is a generated code. DO NOT edit or modify it.
 @author MetadataCompiler 

**/ 



import ecologylab.generic.HashMapArrayList;
import ecologylab.net.ParsedURL;
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
	An article on local wikipedia
**/ 

@xml_inherit

public class  WikipediaPageForParsing
extends  Document
{

	@xml_collection("outlink") private ArrayList<Outlink>	outlinks;
	@xml_collection("category") private ArrayList<Category>	categories;

/**
	Constructor
**/ 

public WikipediaPageForParsing()
{
 super();
}

/**
	Constructor
**/ 

public WikipediaPageForParsing(MetaMetadata metaMetadata)
{
super(metaMetadata);
}

/**
	Lazy Evaluation for outlinks
**/ 

public  ArrayList<Outlink>	outlinks()
{
 ArrayList<Outlink>	result	=this.outlinks;
if(result == null)
{
result = new  ArrayList<Outlink>();
this.outlinks	=	 result;
}
return result;
}

/**
	Set the value of field outlinks
**/ 

public void setOutlinks(  ArrayList<Outlink> outlinks )
{
this.outlinks = outlinks ;
}

/**
	Get the value of field outlinks
**/ 

public  ArrayList<Outlink> getOutlinks(){
return this.outlinks;
}

/**
	Lazy Evaluation for categories
**/ 

public  ArrayList<Category>	categories()
{
 ArrayList<Category>	result	=this.categories;
if(result == null)
{
result = new  ArrayList<Category>();
this.categories	=	 result;
}
return result;
}

/**
	Set the value of field categories
**/ 

public void setCategories(  ArrayList<Category> categories )
{
this.categories = categories ;
}

/**
	Get the value of field categories
**/ 

public  ArrayList<Category> getCategories(){
return this.categories;
}

}

