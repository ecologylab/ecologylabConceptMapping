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

	public static void main(String[] args) throws IOException, MessageTooLargeException,
			InterruptedException
	{
		TranslationScope tscope = WikxplorerMessageTranslationScope.get();

		Scope clientScope = new Scope();

		int port = Integer.valueOf(Server.properties.getProperty("server.port"));
		
		NIOClient client = new NIOClient("achilles.cse.tamu.edu", port, tscope, clientScope);
		client.allowCompression(false);
		client.useRequestCompression(false);
		client.connect();

		if (client.connected())
		{
			UpdateContextRequest ucr1 = new UpdateContextRequest(UpdateContextRequest.ACTION_ADD,
					"creativity");
			client.sendMessage(ucr1);
			Thread.sleep(1000);

			UpdateContextRequest ucr2 = new UpdateContextRequest(UpdateContextRequest.ACTION_ADD,
					"cognitive science");
			client.sendMessage(ucr2);
			Thread.sleep(1000);

			UpdateContextRequest ucr3 = new UpdateContextRequest(UpdateContextRequest.ACTION_ADD,
					"information visualization");
			client.sendMessage(ucr3);
			Thread.sleep(1000);

			RelatednessRequest rr = new RelatednessRequest("cognitive science");
			client.sendMessage(rr);
			Thread.sleep(1000);

			SuggestionRequest sr = new SuggestionRequest("information visualization");
			client.sendMessage(sr);
			Thread.sleep(1000);

			SuggestionRequest sr2 = new SuggestionRequest("united states");
			client.sendMessage(sr2);
			Thread.sleep(1000);

			SuggestionRequest sr3 = new SuggestionRequest("blabla messy things");
			client.sendMessage(sr3);
			Thread.sleep(1000);

			SessionShutdownMessage ssm = new SessionShutdownMessage();
			client.sendMessage(ssm);
			Thread.sleep(1000);

			/*
			// it seems that this will shutdown the server!
			CloseMessage cm = new CloseMessage();
			client.sendMessage(cm);
			Thread.sleep(1000);
			*/

			client.disconnect();
		}
	}

}
