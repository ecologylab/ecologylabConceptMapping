package wikxplorer;

import java.io.IOException;

import wikxplorer.messages.RelatednessRequest;
import wikxplorer.messages.SessionShutdownMessage;
import wikxplorer.messages.SuggestionRequest;
import wikxplorer.messages.UpdateContextRequest;
import wikxplorer.messages.WikxplorerMessageTranslationScope;
import ecologylab.collections.Scope;
import ecologylab.oodss.distributed.client.NIOClient;
import ecologylab.oodss.distributed.exception.MessageTooLargeException;
import ecologylab.oodss.messages.CloseMessage;
import ecologylab.serialization.TranslationScope;

public class TestClient
{

	public static void main(String[] args) throws IOException, MessageTooLargeException, InterruptedException
	{
		TranslationScope tscope = WikxplorerMessageTranslationScope.get();

		Scope clientScope = new Scope();
		
		NIOClient client = new NIOClient("localhost", 11355, tscope, clientScope);
		client.allowCompression(false);
		client.useRequestCompression(false);
		client.connect();
		
		if (client.connected())
		{
			UpdateContextRequest ucr = new UpdateContextRequest();
			ucr.setAction(UpdateContextRequest.ACTION_ADD);
			ucr.setTitle("Creativity");
			client.sendMessage(ucr);
			
			RelatednessRequest rr = new RelatednessRequest();
			rr.setSource("Cognitive science");
			client.sendMessage(rr);
			Thread.sleep(2000);
			
			SuggestionRequest sr = new SuggestionRequest();
			sr.setSource("Information visualization");
			client.sendMessage(sr);
			
			SessionShutdownMessage ssm = new SessionShutdownMessage();
			client.sendMessage(ssm);
			
			CloseMessage cm = new CloseMessage();
			client.sendMessage(cm);
			
			client.disconnect();
		}
	}
	
}
