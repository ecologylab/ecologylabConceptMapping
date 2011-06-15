package ecologylab.semantics.concept.learning;

import java.util.Map;

public interface Classifier<InstanceType, ModelType, ParameterType>
{

	/**
	 * (separating training set from parameters reduces overhead when training multiple models on the
	 * same training set with different parameters, which is useful for parameter tuning)
	 * 
	 * @param trainSet
	 */
	void setTrainingDataSet(DataSet<InstanceType> trainSet);

	/**
	 * train and return a model, with given parameters.
	 * 
	 * prerequisite: training set has been indicated using setTrainingDataSet().
	 * 
	 * @param parameters
	 * @return
	 */
	ModelType trainModel(ParameterType parameters);

	/**
	 * change the underlying model. can be used to prepare classification.
	 * 
	 * @param model
	 */
	void useModel(ModelType model);

	/**
	 * classify a given instance using the underlying model (either trained or indicated using
	 * useModel()).
	 * 
	 * @param normalizer
	 *          used to normalize the instance before classification. ignored if == null.
	 * @param instance
	 * @param results
	 *          output buffer receiving a map from label to confidence.
	 * @return the label with maximum confidence.
	 */
	int classify(Normalizer normalizer, InstanceType instance, Map<Integer, Double> results);

}
