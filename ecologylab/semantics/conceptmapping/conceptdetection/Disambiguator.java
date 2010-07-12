package ecologylab.semantics.conceptmapping.conceptdetection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.svm_node;

import ecologylab.semantics.conceptmapping.database.DatabaseUtils;
import ecologylab.semantics.conceptmapping.learning.svm.SVMPredicter;
import ecologylab.semantics.conceptmapping.text.WikiAnchor;

public class Disambiguator
{

	public static final String		parameterFilePath			= "model/disambi.guassian_normalization.params";

	public static final String		modelFilePath					= "model/disambi.svm.prob.model";

	public static final int				positiveIntegerLabel	= 1;

	protected DatabaseUtils				dbUtils								= new DatabaseUtils();

	public String									disambiguatedConcept;

	public double									confidence;

	public DisambiguationInstance	disambiguatedInstance;

	protected SVMPredicter				pred;

	public Disambiguator(List<WikiAnchor> context, String surface) throws IOException
	{
		disambiguatedConcept = null;
		confidence = Double.NEGATIVE_INFINITY;
		pred = new SVMPredicter(parameterFilePath, modelFilePath);

		try
		{
			List<String> concepts = dbUtils.querySenses(surface);
			for (String concept : concepts)
			{
				DisambiguationFeatureExtractor def = new DisambiguationFeatureExtractor();
				DisambiguationInstance inst = def.extract(context, surface, concept);
				svm_node[] instance = constructSVMInstance(inst);
				Map<Integer, Double> result = new HashMap<Integer, Double>();
				pred.predict(instance, result);
				double confid = result.get(positiveIntegerLabel);
				if (confid > confidence)
				{
					confidence = confid;
					disambiguatedConcept = concept;
					disambiguatedInstance = inst;
				}
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private svm_node[] constructSVMInstance(DisambiguationInstance inst)
	{
		svm_node[] instance = new svm_node[3];
		instance[0].index = 1;
		instance[0].value = inst.commonness;
		instance[1].index = 2;
		instance[1].value = inst.contextualRelatedness;
		instance[2].index = 3;
		instance[2].value = inst.contextQuality;
		return instance;
	}
}
