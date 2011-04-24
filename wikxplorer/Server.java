package wikxplorer;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;

import ecologylab.collections.Scope;
import ecologylab.net.NetTools;
import ecologylab.oodss.distributed.server.DoubleThreadedNIOServer;
import ecologylab.serialization.TranslationScope;
import wikxplorer.messages.WikxplorerMessageTranslationScope;

public class Server
{

	public static void main(String[] args) throws BindException, IOException
	{
		TranslationScope tscope = WikxplorerMessageTranslationScope.get();

		InetAddress[] locals = NetTools.getAllInetAddressesForLocalhost();

		DoubleThreadedNIOServer wikxplorerServer = DoubleThreadedNIOServer.getInstance(11355, locals,
				tscope, new Scope(), -1, 40000);

		wikxplorerServer.start();
	}

}
