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

	DataSet	trainSet;

	DataSet	testSet;

	Writer	out;

	public SVMTuner(DataSet trainSet, DataSet testSet, Writer out)
	{
		this.trainSet = trainSet;
		this.testSet = testSet;
		this.out = out;
	}

	public void tune(double[] Cs, double[] gammas, String modelFilenamePrefix) throws IOException
	{
		SVMTrainer trainer = new SVMTrainer(trainSet);

		for (double C : Cs)
		{
			for (double gamma : gammas)
			{
				svm_model model = trainer.train(C, gamma);
				String modelFilename = modelFilenamePrefix + String.format("-C=%f_g=%f.model", C, gamma);
				SVMTrainer.saveModel(model, modelFilename);

				SVMPredicter pred = new SVMPredicter(modelFilename);
				int size = testSet.getLabels().size();
				int hit = 0;
				for (int i = 0; i < size; ++i)
				{
					int label = testSet.getLabels().get(i);
					svm_node[] inst = testSet.getFeatures().get(i);
					int p = pred.predict(inst, null);
					if (p == label)
						hit++;
				}
				double acc = (double) hit / size;

				out.write(String.format("C=%f, gamma=%f: accuracy=%f\n", C, gamma, acc));
				out.flush();
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
		DataSet trainSet = DataSet.load("data/disambi-trainset.dat");
		SVMGaussianNormalization norm = new SVMGaussianNormalization(trainSet.getDimension());
		norm.generateParameters(trainSet);
		norm.save("model/disambi-norm-params.dat");
		
		DataSet testSet = DataSet.load("data/disambi-testset.dat");
		norm.normalize(testSet);

		BufferedWriter out = new BufferedWriter(new FileWriter("data/disambi-tuning-results.dat"));
		SVMTuner tuner = new SVMTuner(trainSet, testSet, out);
		tuner.tune(
				new double[] { 0.125, 0.25, 0.5 },
				new double[] { 0.0625, 0.125, 0.25 },
				"data/disambi-tuning-models");
		out.close();
	}

}
