package ecologylab.semantics.concept.learning.svm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import libsvm.svm_model;
import libsvm.svm_node;

import ecologylab.generic.Debug;

public class SVMTuner extends Debug
{

	int					numAttributes;

	String			outputParameterFilePath;

	Writer			out;

	SVMTrainer	trainer;

	public SVMTuner(int numAttributes, String outputParameterFilePath, Writer out)
	{
		this.numAttributes = numAttributes;
		this.outputParameterFilePath = outputParameterFilePath;
		this.out = out;
	}

	public void tune(double[] Cs, double[] gammas, DataSet trainSet, DataSet validateSet,
			String outputModelFilenamePrefix) throws IOException
	{
		trainer = new SVMTrainer(numAttributes, trainSet, outputParameterFilePath);

		for (double C : Cs)
		{
			for (double gamma : gammas)
			{
				svm_model model = trainer.train(C, gamma);
				String modelFilename = outputModelFilenamePrefix
						+ String.format("-C=%f_g=%f.svm", C, gamma);
				SVMTrainer.saveModel(model, modelFilename);

				SVMPredicter pred = new SVMPredicter(outputParameterFilePath, modelFilename);
				int size = validateSet.getLabels().size();
				int hit = 0;
				for (int i = 0; i < size; ++i)
				{
					int label = validateSet.getLabels().get(i);
					svm_node[] inst = validateSet.getFeatures().get(i);
					int p = pred.predict(inst, null);
					if (p == label)
						hit++;
				}
				double acc = (double) hit / size;

				out.write(String.format("C=%f, gamma=%f: accuracy=%f\n", C, gamma, acc));
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
		DataSet trainSet = DataSet.read("data/disambi-trainset.dat");
		DataSet validateSet = DataSet.read("data/disambi-validateset.dat");

		BufferedWriter out = new BufferedWriter(new FileWriter("data/disambi-tuning-results.dat"));
		SVMTuner tuner = new SVMTuner(3, "data/disambi-tuning-params.dat", out);
		tuner.tune(new double[]
		{ 0.25, 0.5, 1, 2, 4, 8 }, new double[]
		{ 0.125, 0.25, 0.5, 1, 2, 4 }, trainSet, validateSet, "data/disambi-tuning-models");
		out.close();
	}

}
