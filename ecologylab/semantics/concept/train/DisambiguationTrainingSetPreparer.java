package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.ConceptTrainingConstants;
import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.detect.Concept;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.Doc;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.detect.Surface;
import ecologylab.semantics.concept.detect.TrieDict;

public class DisambiguationTrainingSetPreparer extends TrainingSetPreparer
{

	public DisambiguationTrainingSetPreparer(TrieDict dict) throws IOException, SQLException
	{
		super(dict);
	}

	/**
	 * find those ambiguous, linked surfaces from a wikipedia article, extract features for them
	 * (using the context consists of unambiguous and linked surfaces), convert them into instances
	 * and report.
	 * 
	 * @param doc
	 * @param linkedConcepts
	 * @throws SQLException
	 */
	@Override
	protected void prepare(Doc doc, Map<Concept, Surface> linkedConcepts) throws SQLException
	{
		// build the context
		Context context = new Context();
		for (Concept concept : linkedConcepts.keySet())
		{
			context.addConcept(concept, linkedConcepts.get(concept));
		}
		Set<Surface> unambiSurfaces = doc.getUnambiSurfaces();
		for (Surface surface : unambiSurfaces)
		{
			Concept concept = (Concept) surface.getSenses().toArray()[0];
			context.addConcept(concept, surface);
		}

		// feature extraction
		Set<Surface> targetSurfaces = doc.getAmbiSurfaces();
		targetSurfaces.retainAll(linkedConcepts.values());
		for (Surface surface : targetSurfaces)
		{
			for (Concept concept : surface.getSenses())
			{
				Instance inst = Instance.get(doc, context, surface, concept);
				reportInstance(inst);
			}
		}
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		// prepare output
		File outf = new File(ConceptTrainingConstants.DISAMBI_TRAINING_SET_FILE_PATH);
		if (outf.exists())
		{
			System.err
					.println("training set data file already exists! if you want to regenerate it please delete the old one first.");
			System.exit(-1);
		}
		final BufferedWriter out = new BufferedWriter(new FileWriter(outf));

		// read in title list for generating training set
		List<String> titleList = new ArrayList<String>();

		// prepare
		TrieDict dict = TrieDict.load(new File(ConceptConstants.DICTIONARY_PATH));
		DisambiguationTrainingSetPreparer dtsp = new DisambiguationTrainingSetPreparer(dict) {

			@Override
			public void reportArticle(String title)
			{
				super.reportArticle(title);
			}

			@Override
			public void reportInstance(Instance inst)
			{
				super.reportInstance(inst);
				try
				{
					// TODO
					out.write(inst.toString());
					out.newLine();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};
		dtsp.prepareOnArticles(titleList);
		
		DatabaseFacade.get().close();
	}
}
