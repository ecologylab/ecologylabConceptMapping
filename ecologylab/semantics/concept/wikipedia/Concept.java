package ecologylab.semantics.concept.wikipedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ecologylab.net.ParsedURL;
import ecologylab.serialization.ElementState;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.simpl_inherit;

@simpl_inherit
public class Concept extends ElementState
{
	@simpl_inherit
	public static class Outlink extends ElementState
	{
		@simpl_scalar
		private String surface;
		public String getSurface()
		{
			return surface;
		}
		
		@simpl_scalar
		private String targetConceptName;
		public String getTargetConceptName()
		{
			return targetConceptName;
		}
		
		public Outlink(String surface, String targetConceptName)
		{
			super();
			this.surface = surface;
			this.targetConceptName = targetConceptName;
		}
	}
	
	@simpl_scalar
	private String name;
	public String getName()
	{
		return name;
	}
	
	@simpl_scalar
	private ParsedURL purl;
	public ParsedURL getPurl()
	{
		return purl;
	}
	
	@simpl_collection("outlink")
	private ArrayList<Outlink> outlinks;
	public ArrayList<Outlink> getOutlinks()
	{
		return outlinks;
	}
	public void addOutlink(Outlink outlink)
	{
		outlinks.add(outlink);
	}
	
	@simpl_collection("category_name")
	private ArrayList<String> categoryNames;
	public ArrayList<String> getCategoryNames()
	{
		return categoryNames;
	}
	public void addCategoryName(String categoryName)
	{
		categoryNames.add(categoryName);
	}
	
	// empty constructor for SIMPL
	public Concept()
	{
		super();
	}
	
	public Concept(String name, ParsedURL purl)
	{
		super();
		this.name = name;
		this.purl = purl;
		this.outlinks = new ArrayList<Outlink>();
		this.categoryNames = new ArrayList<String>();
	}
	
	public static void main(String[] args) throws SIMPLTranslationException, IOException
	{
		Outlink o = new Outlink("surface", "target");
		Concept c = new Concept("concept", ParsedURL.getAbsolute("http://tempurl/"));
		c.addCategoryName("cat1");
		
		c.serialize(new File("output.xml"));
	}
}
