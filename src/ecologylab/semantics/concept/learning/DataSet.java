package ecologylab.semantics.concept.learning;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface DataSet<InstanceType>
{

	int getDimension();

	int getSize();

	List<Integer> getLabels();

	List<InstanceType> getInstances();

	List<String> getComments();

	int getNumberOfSamplesWithLabel(int label);

	/**
	 * add a new instance, with label and comment.
	 * 
	 * @param label
	 * @param instance
	 * @param comment
	 */
	void add(int label, InstanceType instance, String comment);

	/**
	 * randomly split a DataSet into several ones, according to the given ratios.
	 * 
	 * @param ratios
	 * @return
	 */
	DataSet<InstanceType>[] randomSplit(double... ratios);

	void save(File outFile) throws IOException;

}
