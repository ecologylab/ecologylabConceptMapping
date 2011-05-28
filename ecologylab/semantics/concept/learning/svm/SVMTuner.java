package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedWriter;
import java.io.File;
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

import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.Constants;
import ecologylab.semantics.concept.learning.svm.SVMPredicter.Prediction;
import ecologylab.semantics.concept.service.Configs;

/**
 * Tune a SVM model over a given range of parameters (C and gamma), using grid search. The criteria
 * of selecting a model is average precision.
 * 
 * @author quyin
 * 
 */
public class SVMTuner extends Debug
{

	SVMTrainer	trainer;

	DataSet			testSet;

	String			modelFilenamePrefix;

	public SVMTuner(DataSet trainSet, DataSet testSet, String modelFilenamePrefix)
	{
		this.trainer = new SVMTrainer(trainSet);
		this.testSet = testSet;
		this.modelFilenamePrefix = modelFilenamePrefix;
	}

	/**
	 * Tune parameters on this tuner, which is created with training / testing set and result location
	 * information.
	 * 
	 * @param Cs
	 * @param gammas
	 * @param numberOfThreads
	 * @throws IOException
	 * @throws InterruptedException
	 */
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
		String paramsString = String.format("-C=%f_g=%f", C, gamma);
		String modelPath = modelFilenamePrefix + paramsString + ".model";
		String normParamsPath = modelFilenamePrefix + paramsString + ".norm";
		String precisionPath = modelFilenamePrefix + paramsString + ".pre";

		trainer.train(C, gamma);
		trainer.saveTrainingResults(modelPath, normParamsPath);

		SVMPredicter pred = new SVMPredicter(trainer.getModel(), trainer.getNormalizer());

		BufferedWriter bw = new BufferedWriter(new FileWriter(precisionPath));
		bw.write("idx: confidence, precision, recall, average_precision");
		bw.newLine();

		int size = testSet.getLabels().size();

		int nPosSamples = 0;
		List<Prediction> preds = new ArrayList<Prediction>();
		double[] kvalueBuffer = new double[trainer.getModel().getTotalNumOfSVs()];
		for (int i = 0; i < size; ++i)
		{
			int label = testSet.getLabels().get(i);
			if (label == Constants.POS_CLASS_INT_LABEL)
				nPosSamples++;
			svm_node[] inst = testSet.getFeatures().get(i);
			Map<Integer, Double> rst = new HashMap<Integer, Double>();
			pred.predict(inst, rst, kvalueBuffer);
			Prediction p = new Prediction(label, inst, rst);
			preds.add(p);
		}

		Collections.sort(preds, new Comparator<Prediction>()
		{
			@Override
			public int compare(Prediction p1, Prediction p2)
			{
				double conf1 = p1.result.get(Constants.POS_CLASS_INT_LABEL);
				double conf2 = p2.result.get(Constants.POS_CLASS_INT_LABEL);
				return Double.compare(conf2, conf1);
			}
		});

		int tp = 0, fp = 0;
		double sum_precision = 0;
		double ap = 0;
		for (int i = 0; i < size; ++i)
		{
			Prediction p = preds.get(i);
			if (p.trueLabel == Constants.POS_CLASS_INT_LABEL)
				tp++;
			else
				fp++;

			if (p.trueLabel == Constants.POS_CLASS_INT_LABEL)
			{
				double precision = (double) tp / (i + 1);
				double recall = (double) tp / nPosSamples;
				sum_precision += precision;
				ap = sum_precision / tp;

				double conf = p.result.get(Constants.POS_CLASS_INT_LABEL);
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
		int numberOfThreads = Configs.getInt("learning.tuning.number_of_threads");
		DataSet trainSet = DataSet.load(Configs.getFile("learning.tuning.training_set_file"));
		DataSet testSet = DataSet.load(Configs.getFile("learning.tuning.testing_set_file"));
		File resultFile = Configs.getFile("learning.tuning.dashboard_file");
		String resultDir = Configs.getString("learning.tuning.result_directory");
		String resultPrefix = Configs.getString("learning.tuning.result_prefix");

		String cs = Configs.getString("learning.tuning.C_values");
		double[] cvalues = parseValues(cs);
		String gs = Configs.getString("learning.tuning.gamma_values");
		double[] gvalues = parseValues(gs);
		// new double[] { 0.125, 0.25, 0.5, 1, 2, 4, 8 };
		// new double[] { 0.0625, 0.125, 0.25, 0.5, 1, 2, 4 };

		final BufferedWriter out = new BufferedWriter(new FileWriter(resultFile));
		File prefix = new File(resultDir, resultPrefix);
		SVMTuner tuner = new SVMTuner(trainSet, testSet, prefix.getAbsolutePath())
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
		tuner.tune(cvalues, gvalues, numberOfThreads);
		out.close();
	}

	private static double[] parseValues(String s)
	{
		String[] l = s.trim().split(",");
		double[] values = new double[l.length];
		for (int i = 0; i < l.length; ++i)
		{
			values[i] = Double.parseDouble(l[i].trim());
		}
		return values;
	}

}
