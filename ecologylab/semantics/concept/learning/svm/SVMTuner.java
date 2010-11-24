package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import libsvm.svm_model;
import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;
import ecologylab.semantics.concept.learning.svm.SVMPredicter.Prediction;

/**
 * Tune a SVM model over a given range of parameters (C and gamma), using grid search. The criteria
 * of selecting a model is average precision.
 * 
 * @author quyin
 * 
 */
public class SVMTuner extends Debug
{

	DataSet	trainSet;

	DataSet	testSet;

	String	modelFilenamePrefix;

	public SVMTuner(DataSet trainSet, DataSet testSet, String modelFilenamePrefix)
	{
		this.trainSet = trainSet;
		this.testSet = testSet;
		this.modelFilenamePrefix = modelFilenamePrefix;
	}

	public void tune(double[] Cs, double[] gammas, int numberOfThreads) throws IOException,
			InterruptedException
	{
		ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);

		for (final double C : Cs)
		{
			for (final double gamma : gammas)
			{
				pool.submit(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							trainFor(C, gamma);
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		}

		pool.shutdown();
		pool.awaitTermination(7, TimeUnit.DAYS);
	}

	private void trainFor(double C, double gamma) throws IOException
	{
		SVMTrainer trainer = new SVMTrainer(trainSet);

		String paramsString = String.format("-C=%f_g=%f", C, gamma);
		String modelFilename = modelFilenamePrefix + paramsString + ".model";
		String precisionFilename = modelFilenamePrefix + paramsString + ".pre";

		svm_model model = trainer.train(C, gamma);
		SVMTrainer.saveModel(model, modelFilename);

		BufferedWriter bw = new BufferedWriter(new FileWriter(precisionFilename));
		bw.write("idx: confidence, precision, recall, average_precision");
		bw.newLine();

		SVMPredicter pred = new SVMPredicter(modelFilename);
		int size = testSet.getLabels().size();
		
		int nPosSamples = 0;
		List<Prediction> preds = new ArrayList<Prediction>();
		double[] kvalueBuffer = new double[model.getTotalNumOfSVs()];
		for (int i = 0; i < size; ++i)
		{
			int label = testSet.getLabels().get(i);
			if (label == ConceptConstants.POS_CLASS_INT_LABEL)
				nPosSamples++;
			svm_node[] inst = testSet.getFeatures().get(i);
			Map<Integer, Double> rst = new HashMap<Integer, Double>();
			pred.predict(inst, rst, kvalueBuffer);
			Prediction p = new Prediction(label, inst, rst);
			preds.add(p);
		}
		
		Collections.sort(preds, new Comparator<Prediction>() {
			@Override
			public int compare(Prediction p1, Prediction p2)
			{
				double conf1 = p1.result.get(ConceptConstants.POS_CLASS_INT_LABEL);
				double conf2 = p2.result.get(ConceptConstants.POS_CLASS_INT_LABEL);
				return Double.compare(conf2, conf1);
			}
		});

		int tp = 0, fp = 0;
		double sum_precision = 0;
		double ap = 0;
		for (int i = 0; i < size; ++i)
		{
			Prediction p = preds.get(i);
			if (p.trueLabel == ConceptConstants.POS_CLASS_INT_LABEL)
				tp++;
			else
				fp++;

			if (p.trueLabel == ConceptConstants.POS_CLASS_INT_LABEL)
			{
				double precision = (double) tp / (i+1);
				double recall = (double) tp / nPosSamples;
				sum_precision += precision;
				ap = sum_precision / tp;
				
				double conf = p.result.get(ConceptConstants.POS_CLASS_INT_LABEL);
				bw.write(String.format("%d: %.2f%%, %.2f%%, %.2f%%, %.2f%%",
						i, conf * 100, precision * 100, recall * 100, ap * 100));
				bw.newLine();
			}
		}
		bw.close();

		report(C, gamma, ap);
	}

	public void report(double c, double gamma, double ap)
	{
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) throws IOException, InterruptedException
	{
		if (args.length != 1)
		{
			System.err.println("args: <#-of-threads>");
			System.exit(-1);
		}

		int numberOfThreads = Integer.parseInt(args[0]);

		DataSet trainSet = DataSet.load("data/detect-trainset.dat");
		DataSet testSet = DataSet.load("data/detect-testset.dat");

		SVMGaussianNormalization norm = new SVMGaussianNormalization(trainSet.getDimension());
		norm.generateParameters(trainSet);
		norm.save("model/detect-norm-params.dat");
		norm.normalize(trainSet);
		norm.normalize(testSet);

		final BufferedWriter out = new BufferedWriter(
				new FileWriter("model/detect-tuning-results.dat"));
		SVMTuner tuner = new SVMTuner(trainSet, testSet, "model/detect-tuning-models")
		{
			public void report(double c, double gamma, double ap)
			{
				try
				{
					out.write(String.format("C=%f, gamma=%f, AP=%.2f%%\n", c, gamma, ap * 100));
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		tuner.tune(
				new double[] { 0.125, 0.25, 0.5, 1, 2, 4, 8 },
				new double[] { 0.0625, 0.125, 0.25, 0.5, 1, 2, 4 },
				numberOfThreads
				);
		out.close();
	}

}
