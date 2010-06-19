package ecologylab.semantics.conceptmapping.model.tuning;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.json.JSONException;

import qmlt.ClassificationAccuracyEvaluator;
import qmlt.dataset.BasicDataSet;
import qmlt.dataset.DataSet;
import qmlt.dataset.filter.Filter;
import qmlt.dataset.filter.GaussianNormalizationFilter;
import qmlt.dataset.filter.MultiWayDiscretizingFilter;
import qmlt.dataset.filter.OutputConversionFilter;
import qmlt.decisiontree.DecisionTree;
import qmlt.decisiontree.control.DecisionTreeTrainingController;
import qmlt.decisiontree.prune.ReducedErrorPruner;
import qmlt.decisiontree.treebuild.GainRatioTreeBuildController;
import qmlt.decisiontree.treebuild.InfoGainTreeBuildController;
import qmlt.svm.SVM;
import qmlt.svm.SVMTrainingController;

public class Tuning
{

	private PrintStream out;
	
	private DataSet	trainSet;
	private DataSet	testSet;
	private DataSet	dTrainSet;
	private DataSet	dValidateSet;
	private DataSet	dTestSet;
	
	private DecisionTreeTrainingController[]	dtCtrls;

	private SVMTrainingController[]	svmCtrls;

	protected void generateDatasets() throws IOException, JSONException
	{
		DataSet ds = BasicDataSet.read("data/disambi.qmlt.ds", "data/disambi.qmlt.dat");
		System.out.println("" + ds.getInstances().size() + " instances read.");
		ds = ds.filter(new GaussianNormalizationFilter()).filter(new OutputConversionFilter("true", 1, "false", -1, 0));
		System.out.println("data preprocessed.");
			
		List<DataSet> dss = ds.randomSplit("training", 0.6, "testing");
		trainSet = dss.get(0);
		testSet = dss.get(1);
		
		List<DataSet> dss0 = trainSet.randomSplit("training", 0.8, "validation");
		Filter discretizer = new MultiWayDiscretizingFilter();
		dTrainSet = dss0.get(0).filter(discretizer);
		dValidateSet = dss0.get(1).filter(discretizer);
		dTestSet = testSet.filter(discretizer);
		System.out.println("datasets generated.");
	}
	
	protected void generateConfigurations()
	{
		dtCtrls = new DecisionTreeTrainingController[] {
				new DecisionTreeTrainingController(new InfoGainTreeBuildController(), new ReducedErrorPruner(dValidateSet)),
				// new DecisionTreeTrainingController(new InfoGainTreeBuildController(), new PessimisticPruner(dValidateSet)),
				new DecisionTreeTrainingController(new GainRatioTreeBuildController(), new ReducedErrorPruner(dValidateSet)),
				// new DecisionTreeTrainingController(new GainRatioTreeBuildController(), new PessimisticPruner(dValidateSet))
		};
		
		double[] Cs = {0.25, 0.5, 1, 2, 4, 8};
		double[] gammas = {0.125, 0.25, 0.5, 1, 2, 4};
		
		svmCtrls = new SVMTrainingController[Cs.length * gammas.length];
		for (int i = 0; i < Cs.length; ++i)
		{
			for (int j = 0; j < gammas.length; ++j)
			{
				svmCtrls[i*gammas.length + j] = new SVMTrainingController(Cs[i], gammas[j]);
			}
		}
	}
	
	protected float tuneOnDecisionTree(DecisionTreeTrainingController ctrl)
	{
		DecisionTree dt = new DecisionTree();
		dt.train(dTrainSet, ctrl);
		ClassificationAccuracyEvaluator<DecisionTree> eva = new ClassificationAccuracyEvaluator<DecisionTree>();
		float accuracy = eva.evaluate(dt, dTestSet);
		return accuracy;
	}
	
	protected float tuneOnSVM(SVMTrainingController ctrl)
	{
		SVM svm = new SVM();
		svm.train(trainSet, ctrl);
		ClassificationAccuracyEvaluator<SVM> eva = new ClassificationAccuracyEvaluator<SVM>();
		float accuracy = eva.evaluate(svm, testSet);
		return accuracy;
	}

	public void tune()
	{
		for (DecisionTreeTrainingController dtCtrl : dtCtrls)
		{
			float a = tuneOnDecisionTree(dtCtrl);
			out.println("tree[" + dtCtrl + "]:" + a);
		}
		for (SVMTrainingController svmCtrl : svmCtrls)
		{
			float a = tuneOnSVM(svmCtrl);
			out.println("svm[" + svmCtrl + "]:" + a);
		}
		out.flush();
	}
	
	public Tuning(String outputFilepath) throws IOException, JSONException
	{
		if (outputFilepath == null)
		{
			out = System.out;
		}
		else
		{
			out = new PrintStream(outputFilepath);
		}
		
		generateDatasets();
		generateConfigurations();
	}
	
	public static void main(String[] args) throws IOException, JSONException
	{
		int numPairs = 10;
		
		for (int i = 0; i < numPairs; ++i)
		{
			Tuning t = new Tuning("disambi-tuning.txt");
			// Tuning t = new Tuning("accuracy-pairs." + (i+1) + ".txt");
			t.tune();
		}
	}

}
