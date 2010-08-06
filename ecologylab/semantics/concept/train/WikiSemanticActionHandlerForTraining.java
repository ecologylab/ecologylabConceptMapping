package ecologylab.semantics.concept.train;

import java.sql.SQLException;
import java.util.ArrayList;

import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionHandlerBase;
import ecologylab.semantics.concept.detect.Context;
import ecologylab.semantics.documentparsers.DocumentParser;
import ecologylab.semantics.generated.library.Paragraph;
import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metametadata.example.MyContainer;

public class WikiSemanticActionHandlerForTraining extends SemanticActionHandlerBase<MyContainer, WikiInfoCollectorForTraining>
{
	
	private StringBuilder textSb = new StringBuilder();
	
	private Context context = new Context();

	@Override
	public void createAndVisualizeTextSurrogateSemanticAction(SemanticAction action,
			DocumentParser documentType, WikiInfoCollectorForTraining infoCollector)
	{
		ArrayList<Paragraph> paragraphs = (ArrayList<Paragraph>) getArgumentValueByName(action, "wiki_text");
		for (Paragraph paragraph : paragraphs)
		{
			textSb.append(paragraph.getParagraphText());
			textSb.append(" ");
		}
	}

	@Override
	public void parseDocument(SemanticAction action, DocumentParser documentType,
			WikiInfoCollectorForTraining infoCollector)
	{
		String surface = (String) getArgumentValueByName(action, "surface");
		String concept = (String) getArgumentValueByName(action, "target_title");
		
		if (surface != null && concept != null)
			context.add(surface, concept);
	}
	
	@Override
	public void postSemanticActionsHook(Metadata metadata)
	{
		try
		{
			TrainingSetPreparer tsp = new TrainingSetPreparer(context);
			tsp.detect(textSb.toString());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

}
