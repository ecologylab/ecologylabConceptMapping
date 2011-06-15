package ecologylab.semantics.concept.learning.svm;

import java.io.File;
import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;

public class SvmModelUtils
{

	public static void save(svm_model model, File outFile) throws IOException
	{
		svm.svm_save_model(outFile.getPath(), model);
	}

	public static svm_model load(File inFile) throws IOException
	{
		return svm.svm_load_model(inFile.getPath());
	}

}
