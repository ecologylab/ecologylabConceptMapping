package wikxplorer;

import java.io.IOException;
import java.net.InetAddress;

import wikxplorer.messages.RelatednessRequest;
import wikxplorer.messages.SuggestionRequest;
import wikxplorer.messages.SuggestionResponse;
import wikxplorer.messages.UpdateContextRequest;
import wikxplorer.messages.WikxplorerMessageTranslationScope;
import ecologylab.collections.Scope;
import ecologylab.oodss.distributed.client.NIOClient;
import ecologylab.oodss.distributed.exception.MessageTooLargeException;
import ecologylab.serialization.TranslationScope;

public class TestClient
{

	public static void main(String[] args) throws IOException, MessageTooLargeException, InterruptedException
	{
		TranslationScope tscope = WikxplorerMessageTranslationScope.get();

		Scope clientScope = new Scope();
		
		NIOClient client = new NIOClient("localhost", 11355, tscope, clientScope);
		client.allowCompression(true);
		client.useRequestCompression(true);
		client.connect();
		
		if (client.connected())
		{
			UpdateContextRequest ucr = new UpdateContextRequest();
			ucr.setAction(UpdateContextRequest.ACTION_ADD);
			ucr.setTitle("United States");
			client.sendMessage(ucr);
			Thread.sleep(2000);
			
			RelatednessRequest rr = new RelatednessRequest();
			rr.setSource("White House");
			client.sendMessage(rr);
			Thread.sleep(2000);
			
			SuggestionRequest sr = new SuggestionRequest();
			sr.setSource("Constitution");
			client.sendMessage(sr);
		}
	}
	
}
