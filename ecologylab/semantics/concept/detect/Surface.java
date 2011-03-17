package ecologylab.semantics.concept.detect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ecologylab.semantics.concept.database.orm.Commonness;
import ecologylab.semantics.concept.database.orm.Keyphraseness;
import ecologylab.semantics.concept.database.orm.WikiConcept;

/**
 * Represents a surface (local to a Doc).
 * 
 * @author quyin
 * 
 */
public class Surface
{

	private final Doc										doc;

	private final String								word;

	private double											keyphraseness	= -1;

	private Map<WikiConcept, Instance>	senses				= new HashMap<WikiConcept, Instance>();

	private WikiConcept									disambiguatedConcept;

	/**
	 * Construct a Surface.
	 * 
	 * @param doc
	 *          The contextual Doc of this surface.
	 * @param word
	 *          The free text word of this surface.
	 */
	public Surface(Doc doc, String word)
	{
		this.doc = doc;
		assert word != null && !word.isEmpty() : "invalid word for surface: " + word;
		this.word = word;

		Keyphraseness kp = (Keyphraseness) this.doc.getSession().get(Keyphraseness.class, word);
		keyphraseness = kp.getKeyphraseness();

		List<Commonness> commonness = Commonness.get(word, this.doc.getSession());
		for (Commonness com : commonness)
		{
			int conceptId = com.getConceptId();
			WikiConcept concept = (WikiConcept) this.doc.getSession().get(WikiConcept.class, conceptId);
			assert concept != null : "weird: concept not found: " + conceptId;
			Instance inst = new Instance(this, concept);
			inst.commonness = com.getCommonness();
			senses.put(concept, inst);
		}

		if (senses.size() == 1)
		{
			Iterator<WikiConcept> it = senses.keySet().iterator();
			disambiguatedConcept = it.next();
		}
	}

	public String getWord()
	{
		return word;
	}

	public Map<WikiConcept, Instance> getSenses()
	{
		return senses;
	}

	public double getKeyphraseness()
	{
		return keyphraseness;
	}

	public WikiConcept getDisambiguatedConcept()
	{
		return disambiguatedConcept;
	}

	public Instance getDisambiguatedInstance()
	{
		return senses.get(disambiguatedConcept);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Surface)
		{
			Surface s = (Surface) obj;
			return word.equals(s.word);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return word.hashCode();
	}

	@Override
	public String toString()
	{
		return String.format("surface[%s]", word);
	}

}
