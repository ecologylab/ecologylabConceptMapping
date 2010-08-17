package ecologylab.semantics.concept.train;

import java.util.List;

import ecologylab.semantics.actions.CreateAndVisualizeTextSurrogateSemanticAction;
import ecologylab.semantics.actions.SemanticActionStandardMethods;
import ecologylab.semantics.generated.library.Paragraph;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit
@xml_tag(SemanticActionStandardMethods.CREATE_AND_VISUALIZE_TEXT_SURROGATE)
public class WikiParsingTextHandler
		extends
		CreateAndVisualizeTextSurrogateSemanticAction<WikiInfoCollectorForTraining, WikiSemanticActionHandlerForTraining>
{

	@Override
	public Object perform(Object obj)
	{
		List<Paragraph> paragraphs = (List<Paragraph>) getArgumentObject("wiki_text");
		for (Paragraph paragraph : paragraphs)
		{
			semanticActionHandler.textBuilder.append(paragraph.getParagraphText());
			semanticActionHandler.textBuilder.append(" ");
		}
		return null;
	}

}
