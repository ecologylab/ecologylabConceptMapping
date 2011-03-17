package ecologylab.semantics.concept.learning.svm;

import java.io.File;
import java.io.IOException;

import libsvm.svm_node;

/**
 * The interface of normalizers.
 * 
 * @author quyin
 * 
 */
public interface Normalizer
{

	/**
	 * Initialize a normalizer with a given dataset. Will calculate normalization parameters.
	 * 
	 * @param dataSet
	 */
	void initialize(DataSet dataSet);

	/**
	 * Load a saved file containing normalization parameters.
	 * 
	 * @param paramsPath
	 */
	void load(File paramsPath) throws IOException;

	/**
	 * Normalize an instance.
	 * 
	 * @param instance
	 */
	void normalize(svm_node[] instance);

	/**
	 * Normalize a dataset.
	 * 
	 * @param dataSet
	 */
	void normalize(DataSet dataSet);

	/**
	 * Save calcuated normalization parameters into a file.
	 * 
	 * @param paramsPath
	 */
	void save(File paramsPath) throws IOException;

}
