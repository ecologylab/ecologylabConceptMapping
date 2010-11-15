package ecologylab.semantics.concept;

public class ConceptConstants
{

	public static final String	METAMETADATA_REPOSITORY_LOCATION	= "../ecologylabSemantics/repository";

	public static final String	DICTIONARY_PATH										= "data/freq-surfaces.dict";

	public static final int			DICTIONARY_LONGEST_IN_WORD				= 21;

	public static final String	DISAMBI_PARAM_FILE_PATH						= "model/disambi-norm-params.dat";

	public static final String	DISAMBI_MODEL_FILE_PATH						= "model/disambi-tuning-models-C=0.125000_g=0.062500.model";

	public static final String	DETECT_PARAM_FILE_PATH						= "model/detect-norm-params.dat";

	public static final String	DETECT_MODEL_FILE_PATH						= "model/detect-tuning-models-C=8.000000_g=1.000000.model";

	public static final int			POS_CLASS_INT_LABEL								= 1;

	public static final int			NEG_CLASS_INT_LABEL								= -1;

	public static final double	WEIGHT_KEYPHRASENESS							= 0.5;

	public static final double	WEIGHT_MUTUAL_RELATEDNESS					= 0.5;

	public static final double	DETECT_THRESHOLD									= 0.3;

	// TODO
	public static final double	threshold1												= 0;

	// TODO
	public static final double	threshold2												= 0;

}
