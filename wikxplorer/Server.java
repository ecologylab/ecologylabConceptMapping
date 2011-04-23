package wikxplorer;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.util.HashMap;

import ecologylab.collections.Scope;
import ecologylab.net.NetTools;
import ecologylab.oodss.distributed.server.DoubleThreadedNIOServer;
import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.serialization.TranslationScope;
import wikxplorer.messages.WikxplorerMessageTranslationScope;

public class Server
{

	public static void main(String[] args) throws BindException, IOException
	{
		TranslationScope tscope = WikxplorerMessageTranslationScope.get();

		Scope serverClientScope = new Scope();
		serverClientScope.put(ScopeKeys.SESSION, SessionManager.get().newSession());
		serverClientScope.put(ScopeKeys.CLIPPING_CONTEXT, new HashMap<String, WikiConcept>());

		InetAddress[] locals = NetTools.getAllInetAddressesForLocalhost();

		DoubleThreadedNIOServer wikxplorerServer = DoubleThreadedNIOServer.getInstance(11355, locals,
				tscope, serverClientScope, -1, 40000);
		
		wikxplorerServer.start();
	}

}
