package ecologylab.semantics.concept.wikipedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ecologylab.net.ParsedURL;
import ecologylab.serialization.ElementState;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit
@xml_tag("concepts")
public class ConceptPool extends ElementState
{
	// sync
	private Lock								lock				= new ReentrantLock();

	// pool
	public int									bufferSize	= 10000;

	@simpl_scalar
	private int									start				= 1;

	@simpl_scalar
	private int									end;

	@simpl_nowrap
	@simpl_collection("concept")
	private ArrayList<Concept>	pool				= new ArrayList<Concept>();

	public ArrayList<Concept> getPool()
	{
		return pool;
	}

	// current
	private Concept	current	= null;

	public Concept getCurrent()
	{
		return current;
	}

	// operations on current
	public void beginNewConcept(String name, ParsedURL purl)
	{
		lock.lock();
		System.out.println("\ncreating concept: " + name + "...");
		current = new Concept(name, purl);
	}

	public void endNewConcept()
	{
		if (current.getName() != null)
		{
			pool.add(current);
			current = null;

			// save buffered concepts
			if (pool.size() >= bufferSize)
			{
				save();
			}
		}

		System.out.println("finished.");
		lock.unlock();
	}

	public void addOutlink(String surface, String targetConceptName)
	{
		if (URLListFilter.isSpecialPage(targetConceptName))
			return;

		Concept.Outlink outlink = new Concept.Outlink(surface, targetConceptName);
		current.addOutlink(outlink);
	}

	public void addCategory(String category)
	{
		current.addCategoryName(category);
	}

	public void save()
	{
		if (pool.size() <= 0)
			return;

		end = start + pool.size() - 1;
		try
		{
			this.serialize(new File(String.valueOf(start) + ".xml"));
			start += pool.size();
			pool.clear();
		}
		catch (SIMPLTranslationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// for SIMPL
	public ConceptPool()
	{

	}

	private static ConceptPool	the	= null;

	public static ConceptPool get()
	{
		if (the == null)
			the = new ConceptPool();
		return the;
	}
}
