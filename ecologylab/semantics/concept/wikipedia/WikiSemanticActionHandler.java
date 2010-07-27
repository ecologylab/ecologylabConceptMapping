package ecologylab.semantics.concept.wikipedia;

import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionHandlerBase;
import ecologylab.semantics.documentparsers.DocumentParser;
import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metametadata.example.MyContainer;
import ecologylab.semantics.metametadata.example.MyInfoCollector;

public class WikiSemanticActionHandler extends SemanticActionHandlerBase<MyContainer, MyInfoCollector>
{

	@Override
	public void parseDocumentNow(SemanticAction action, DocumentParser docType,
			MyInfoCollector infoCollector)
	{
		System.out.println("parse_document_now called.");
		// TODO Auto-generated method stub
		super.parseDocumentNow(action, docType, infoCollector);
	}

	@Override
	public void preSemanticActionsHook(Metadata metadata)
	{
		System.out.println("pre_semantic_actions_hook called.");
		// TODO Auto-generated method stub
		super.preSemanticActionsHook(metadata);
	}

	@Override
	public void postSemanticActionsHook(Metadata metadata)
	{
		System.out.println("post_semantic_actions_hook called.");
		// TODO Auto-generated method stub
		super.postSemanticActionsHook(metadata);
	}

}
