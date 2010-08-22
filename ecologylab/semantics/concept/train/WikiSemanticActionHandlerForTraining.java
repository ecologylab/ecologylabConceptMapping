package ecologylab.semantics.concept.train;

import java.io.IOException;

import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metametadata.example.MyContainer;

public class WikiSemanticActionHandlerForTraining extends
		SemanticActionHandler<MyContainer, WikiInfoCollectorForTraining>
{

	StringBuilder	textBuilder	= new StringBuilder();

	Context				context	= new Context();

	@Override
	public void postSemanticActionsHook(Metadata metadata)
	{
		TrainingSetPreparer tsp = TrainingSetPreparer.get(context);
		try
		{
			tsp.detect(textBuilder.toString());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
