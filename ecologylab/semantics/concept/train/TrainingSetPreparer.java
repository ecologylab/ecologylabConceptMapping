package ecologylab.semantics.concept.train;

import java.io.BufferedWriter;

import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.concept.detect.Detector;

public abstract class TrainingSetPreparer extends Detector
{

	protected Context					presetContext;

	protected BufferedWriter	out;

	public TrainingSetPreparer(Context presetContext)
	{
		this.presetContext = presetContext;
	}

	/**
	 * overridden to enlarge the context with the preset context.
	 */
	@Override
	protected void findSurfacesAndGenerateContext()
	{
		super.findSurfacesAndGenerateContext();
		context.addAll(presetContext);
	}

	public static final int	DISAMBIGUTION_PHASE	= 1;

	public static final int	DETECTION_PHASE			= 2;

	public static int				phase;

	public static TrainingSetPreparer get(Context presetContext)
	{
		if (phase == DISAMBIGUTION_PHASE)
			return new DisambiguationTrainingSetPreparer(presetContext);
		else if (phase == DETECTION_PHASE)
			return new DetectionTrainingSetPreparer(presetContext);
		else
			return null;
	}

}
