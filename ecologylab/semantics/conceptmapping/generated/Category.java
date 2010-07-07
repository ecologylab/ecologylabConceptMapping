package ecologylab.semantics.conceptmapping.generated;

import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metadata.scalar.MetadataString;
import ecologylab.semantics.metametadata.MetaMetadata;
import ecologylab.serialization.Hint;
import ecologylab.serialization.simpl_inherit;

@simpl_inherit

public class Category extends Metadata{


/**
	null
**/ 

	 @simpl_scalar @simpl_hints(Hint.XML_LEAF) private MetadataString	category;

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
