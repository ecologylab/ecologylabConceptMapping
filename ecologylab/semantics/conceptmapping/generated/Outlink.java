package ecologylab.semantics.conceptmapping.generated;

import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metadata.scalar.MetadataString;
import ecologylab.semantics.metametadata.MetaMetadata;
import ecologylab.serialization.Hint;
import ecologylab.serialization.simpl_inherit;

@simpl_inherit

public class Outlink extends Metadata{


/**
	null
**/ 

	 @simpl_scalar @simpl_hints(Hint.XML_LEAF) private MetadataString	surface;

/**
	null
**/ 

	 @simpl_scalar @simpl_hints(Hint.XML_LEAF) private MetadataString	targetConcept;

/**
	Constructor
**/ 

public Outlink()
{
 super();
}

/**
	Constructor
**/ 

public Outlink(MetaMetadata metaMetadata)
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
	Lazy Evaluation for targetConcept
**/ 

public MetadataString	targetConcept()
{
MetadataString	result	=this.targetConcept;
if(result == null)
{
result = new MetadataString();
this.targetConcept	=	 result;
}
return result;
}

/**
	Gets the value of the field targetConcept
**/ 

public String getTargetConcept(){
return targetConcept().getValue();
}

/**
	Sets the value of the field targetConcept
**/ 

public void setTargetConcept( String targetConcept )
{
this.targetConcept().setValue(targetConcept);
}

/**
	The heavy weight setter method for field targetConcept
**/ 

public void hwSetTargetConcept( String targetConcept )
{
this.targetConcept().setValue(targetConcept);
rebuildCompositeTermVector();
 }
/**
	 Sets the targetConcept directly
**/ 

public void setTargetConceptMetadata(MetadataString targetConcept)
{	this.targetConcept = targetConcept;
}
/**
	Heavy Weight Direct setter method for targetConcept
**/ 

public void hwSetTargetConceptMetadata(MetadataString targetConcept)
{	 if(this.targetConcept!=null && this.targetConcept.getValue()!=null && hasTermVector())
		 termVector().remove(this.targetConcept.termVector());
	 this.targetConcept = targetConcept;
	rebuildCompositeTermVector();
}}
