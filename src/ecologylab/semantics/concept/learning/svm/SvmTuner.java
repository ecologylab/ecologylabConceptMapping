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

import libsvm.svm_model;
import libsvm.svm_node;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.learning.Constants;
import ecologylab.semantics.concept.service.Configs;

/**
 * Tune a SVM model over a given range of parameters (C and gamma), using grid search. The criteria
 * of selecting a model is average precision. us AP to observe the trade-off between precision /
 * recall and decide the best threshold.
 * 
 * @author quyin
 * 
 */
public class SvmTuner extends Debug
{

	private static class Prediction
	{

		public int									trueLabel;

		public Map<Integer, Double>	results;

		public Prediction(int trueLabel, Map<Integer, Double> results)
		{
			this.trueLabel = trueLabel;
			this.results = results;
		}

	}

	private SvmDataSet	trainSet;

	private SvmDataSet	testSet;

	private String			modelFilenamePrefix;

	public void tune() throws IOException, InterruptedException
	{
		trainSet = SvmDataSet.load(Configs.getFile("learning.tuning.training_set_file"));
		testSet = SvmDataSet.load(Configs.getFile("learning.tuning.testing_set_file"));
		SvmGaussianNormalizer normalizer = new SvmGaussianNormalizer();
		normalizer.calculateNormalizationParameters(trainSet);
		normalizer.save(Configs.getFile("learning.normalization_parameter_file"));
		normalizer.normalize(trainSet);
		normalizer.normalize(testSet);

		String resultDir = Configs.getString("learning.tuning.result_directory");
		String resultPrefix = Configs.getString("learning.tuning.result_prefix");
		File prefix = new File(resultDir, resultPrefix);
		modelFilenamePrefix = prefix.getAbsolutePath();

		String CsStr = Configs.getString("learning.tuning.C_values");
		double[] Cs = parseValues(CsStr);
		String gammasStr = Configs.getString("learning.tuning.gamma_values");
		double[] gammas = parseValues(gammasStr);

		int numberOfThreads = Configs.getInt("learning.tuning.number_of_threads");
		ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);

		for (final double C : Cs)
		{
			for (final double gamma : gammas)
			{
				final SvmParameter parameters = new SvmParameter();
				parameters.C = C;
				parameters.gamma = gamma;
				pool.submit(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							trainFor(parameters);
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

	private void trainFor(SvmParameter parameters) throws IOException
	{
		double C = parameters.C;
		double gamma = parameters.gamma;

		String paramsString = String.format("-C=%f_g=%f", C, gamma);
		String modelPath = modelFilenamePrefix + paramsString + ".model";
		String apPath = modelFilenamePrefix + paramsString + ".ap";

		SvmClassifier classifier = new SvmClassifier();
		svm_model model = classifier.trainModel(parameters);
		SvmModelUtils.save(model, new File(modelPath));

		BufferedWriter apWriter = new BufferedWriter(new FileWriter(apPath));
		apWriter.write("idx: confidence, precision, recall, average_precision");
		apWriter.newLine();

		// calculate ap @ each instance
		int size = testSet.getSize();
		int nPosSamples = 0;
		List<Prediction> preds = new ArrayList<Prediction>();
		for (int i = 0; i < size; ++i)
		{
			int trueLabel = testSet.getLabels().get(i);
			if (trueLabel == Constants.POS_CLASS_INT_LABEL)
				nPosSamples++;
			svm_node[] inst = testSet.getInstances().get(i);
			Map<Integer, Double> rst = new HashMap<Integer, Double>();
			classifier.classify(null, inst, rst); // testSet should have been normalized
			Prediction p = new Prediction(trueLabel, rst);
			preds.add(p);
		}
		Collections.sort(preds, new Comparator<Prediction>()
		{
			@Override
			public int compare(Prediction p1, Prediction p2)
			{
				double conf1 = p1.results.get(Constants.POS_CLASS_INT_LABEL);
				double conf2 = p2.results.get(Constants.POS_CLASS_INT_LABEL);
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

				double conf = p.results.get(Constants.POS_CLASS_INT_LABEL);
				apWriter.write(String.format("%d: %.2f%%, %.2f%%, %.2f%%, %.2f%%",
						i, conf * 100, precision * 100, recall * 100, ap * 100));
				apWriter.newLine();
			}
		}
		apWriter.close();

		report(C, gamma, ap);
	}

	public void report(double c, double gamma, double ap)
	{
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) throws IOException, InterruptedException
	{
		File resultFile = Configs.getFile("learning.tuning.dashboard_file");
		final BufferedWriter out = new BufferedWriter(new FileWriter(resultFile));
		SvmTuner tuner = new SvmTuner()
		{
			public void report(double c, double gamma, double ap)
			{
				try
				{
					out.write(String.format("C=%f, gamma=%f, AP=%.2f%%\n", c, gamma, ap * 100));
					out.flush();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		tuner.tune();
		out.close();
	}

}
