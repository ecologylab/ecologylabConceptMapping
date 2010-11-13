package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import libsvm.svm_model;
import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.ConceptConstants;

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

		String paramsString = String.format("-C=%f_g=%f.model", C, gamma);
		String modelFilename = modelFilenamePrefix + paramsString + ".model";
		String precisionFilename = modelFilenamePrefix + paramsString + ".pre";

		svm_model model = trainer.train(C, gamma);
		SVMTrainer.saveModel(model, modelFilename);

		BufferedWriter bw = new BufferedWriter(new FileWriter(precisionFilename));
		bw.write("idx: precision, recall, average_precision");
		bw.newLine();

		SVMPredicter pred = new SVMPredicter(modelFilename);
		int size = testSet.getLabels().size();

		int tp = 0, tn = 0, fp = 0, fn = 0;
		double sum_precision = 0;
		double ap = 0;
		for (int i = 0; i < size; ++i)
		{
			int label = testSet.getLabels().get(i);
			svm_node[] inst = testSet.getFeatures().get(i);
			int p = pred.predict(inst, null);

			if (p == ConceptConstants.POS_CLASS_INT_LABEL
					&& label == ConceptConstants.POS_CLASS_INT_LABEL)
				tp++;
			else if (p == ConceptConstants.POS_CLASS_INT_LABEL
					&& label == ConceptConstants.NEG_CLASS_INT_LABEL)
				fp++;
			else if (p == ConceptConstants.NEG_CLASS_INT_LABEL
					&& label == ConceptConstants.POS_CLASS_INT_LABEL)
				fn++;
			else
				// given that there are only 2 types of labels
				tn++;

			if (p == label)
			{
				tp++;
				double precision = tp / (tp + fp);
				double recall = tp / (tp + fn);
				sum_precision += precision;
				ap = sum_precision / tp;
				bw.write(String.format("%d: %f, %f, %f", i, precision, recall, ap));
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

		DataSet trainSet = DataSet.load("data/disambi-trainset.dat");
		DataSet testSet = DataSet.load("data/disambi-testset.dat");

		SVMGaussianNormalization norm = new SVMGaussianNormalization(trainSet.getDimension());
		norm.generateParameters(trainSet);
		norm.save("model/disambi-norm-params.dat");
		norm.normalize(trainSet);
		norm.normalize(testSet);

		final BufferedWriter out = new BufferedWriter(
				new FileWriter("model/disambi-tuning-results.dat"));
		SVMTuner tuner = new SVMTuner(trainSet, testSet, "model/disambi-tuning-models")
		{
			public void report(double c, double gamma, double ap)
			{
				try
				{
					out.write(String.format("C=%f, gamma=%f, AP=%f\n", c, gamma, ap));
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
