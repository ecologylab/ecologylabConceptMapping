package wikxplorer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.util.Properties;

import ecologylab.collections.Scope;
import ecologylab.net.NetTools;
import ecologylab.oodss.distributed.server.DoubleThreadedNIOServer;
import ecologylab.serialization.TranslationScope;
import wikxplorer.messages.WikxplorerMessageTranslationScope;

public class Server
{

	public static final Properties	properties;

	static
	{
		properties = new Properties();
		try
		{
			properties.load(new FileInputStream("wikxplorer-server.prop"));
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws BindException, IOException
	{
		TranslationScope tscope = WikxplorerMessageTranslationScope.get();

		InetAddress[] locals = NetTools.getAllInetAddressesForLocalhost();

		DoubleThreadedNIOServer wikxplorerServer = DoubleThreadedNIOServer.getInstance(11355, locals,
				tscope, new Scope(), -1, 40000);

		wikxplorerServer.start();
	}

}
