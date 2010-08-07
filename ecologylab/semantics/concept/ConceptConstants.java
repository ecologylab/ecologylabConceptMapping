package ecologylab.semantics.concept;

public class ConceptConstants
{

	public static final String	METAMETADATA_REPOSITORY_LOCATION	= "../cf/config/semantics/metametadata";

	public static final String	DISAMBI_PARAM_FILE_PATH						= "model/disambi.guassian_normalization.params";

	public static final String	DISAMBI_MODEL_FILE_PATH						= "model/disambi.svm.prob.model";

	public static final String	DETECT_PARAM_FILE_PATH						= null;

	public static final String	DETECT_MODEL_FILE_PATH						= null;

	public static final int			POS_CLASS_INT_LABEL								= 1;

	public static final int			NEG_CLASS_INT_LABEL								= -1;

	public static final double	WEIGHT_KEYPHRASENESS							= 0.5;

	public static final double	WEIGHT_MUTUAL_RELATEDNESS					= 0.5;

	public static final double	DETECT_THRESHOLD									= 0.5;

}
