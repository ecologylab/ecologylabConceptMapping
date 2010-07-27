package ecologylab.semantics.concept.wikipedia;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionHandlerBase;
import ecologylab.semantics.documentparsers.DocumentParser;
import ecologylab.semantics.generated.library.WikipediaPageType;
import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metametadata.example.MyContainer;
import ecologylab.semantics.metametadata.example.MyInfoCollector;

public class WikiSemanticActionHandler extends
		SemanticActionHandlerBase<MyContainer, MyInfoCollector>
{

	@Override
	public void parseDocumentNow(SemanticAction action, DocumentParser docType,
			MyInfoCollector infoCollector)
	{
		System.out.println("parse_document_now called.");
		String category = (String) getArgumentValueByName(action, "category");
		if (category == null)
		{
			String surface = (String) getArgumentValueByName(action, "surface");
			String targetTitle = (String) getArgumentValueByName(action, "target_title");

			String linkS = String.format(
					"[esc:name \"%s\"] esc:linked_by [esc:name \"%s\"]; esc:surface \"%s\" .", targetTitle,
					ConceptPool.get().getCurrent().getName(), surface);
			StringPool.get("inlinks.n3").addLine(linkS);

			String surfaceS = String.format("[esc:words \"%s\"] esc:surface_of [esc:name \"%s\"] .",
					surface, targetTitle);
			StringPool.get("surfaces.n3").addLine(surfaceS);
		}
		else
		{
			String categoryName = (String) getArgumentValueByName(action, "category");
			ConceptPool.get().addCategory(categoryName);
		}
	}

	@Override
	public void preSemanticActionsHook(Metadata metadata)
	{
		System.out.println("pre_semantic_actions_hook called.");
		if (metadata instanceof WikipediaPageType)
		{
			WikipediaPageType page = (WikipediaPageType) metadata;
			String title = page.getTitle();
			ParsedURL purl = page.getLocation();
			ConceptPool.get().beginNewConcept(title, purl);
		}
	}

	@Override
	public void postSemanticActionsHook(Metadata metadata)
	{
		System.out.println("post_semantic_actions_hook called.");
		ConceptPool.get().endNewConcept();
	}

}
