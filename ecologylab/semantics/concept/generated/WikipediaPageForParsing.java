package ecologylab.semantics.concept.generated;

/**
This is a generated code. DO NOT edit or modify it.
 @author MetadataCompiler 

**/ 



import java.util.ArrayList;

import ecologylab.semantics.metadata.builtins.Document;
import ecologylab.semantics.metametadata.MetaMetadata;
import ecologylab.serialization.simpl_inherit;


/**
	An article on local wikipedia
**/ 

@simpl_inherit

public class  WikipediaPageForParsing
extends  Document
{

	@simpl_collection("outlink") private ArrayList<Outlink>	outlinks;
	@simpl_collection("category") private ArrayList<Category>	categories;

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

