package ecologylab.semantics.concept.learning;

import java.io.File;
import java.io.IOException;

import ecologylab.semantics.concept.learning.svm.SvmDataSet;

import libsvm.svm_node;

public interface Normalizer<InstanceType>
{

	/**
	 * calculate normalization parameters using a given dataset.
	 * 
	 * @param dataSet
	 */
	void calculateNormalizationParameters(SvmDataSet dataSet);

	/**
	 * Save calculated normalization parameters into a file.
	 * 
	 * @param paramsPath
	 */
	void save(File outFile) throws IOException;

	/**
	 * Load a saved normalization parameter file.
	 * 
	 * @param paramsPath
	 */
	void load(File paramsPath) throws IOException;

	/**
	 * Normalize a dataset.
	 * 
	 * @param dataSet
	 */
	void normalize(SvmDataSet dataSet);

	/**
	 * Normalize an instance.
	 * 
	 * @param instance
	 */
	void normalize(svm_node[] instance);

}
