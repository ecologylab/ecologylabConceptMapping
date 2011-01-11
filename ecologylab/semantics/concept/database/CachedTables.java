package ecologylab.semantics.concept.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;

public class CachedTables
{

	public static final String							INLINKS_TABLE_NAME				= InlinksTable.NAME;

	public static final String							KEYPHRASENESS_TABLE_NAME	= KeyphrasenessTable.NAME;

	public static final String							RELATEDNESS_TABLE_NAME		= RelatednessTable.NAME;

	public static final String							SENSES_TABLE_NAME					= SensesTable.NAME;

	private static final String							CONFIG_PATH								= "cache_configs.xml";

	// central registry for cached tables
	private static Map<String, CachedTable>	cachedTables							= new HashMap<String, CachedTable>();

	private static Configuration						cacheConfigs							= null;

	private static CacheManager							cacheManager							= new CacheManager();

	static
	{
		try
		{
			cacheConfigs = ConfigurationFactory.parseConfiguration(new FileInputStream(
					"cache_configs.xml"));
		}
		catch (CacheException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				cacheManager.shutdown();
			}
		}));

		register(new CachedTable(new InlinksTable()));
		register(new CachedTable(new RelatednessTable()));
		register(new CachedTable(new SensesTable()));
		register(new CachedTable(new KeyphrasenessTable()));
	}

	public static void register(CachedTable cachedTable)
	{
		cacheManager.addCache(cachedTable.getEhcache());
		cachedTables.put(cachedTable.getTable().getName(), cachedTable);
	}

	public static CachedTable getCachedTable(String tableName)
	{
		return cachedTables.get(tableName);
	}

	static CacheConfiguration getCacheConfig(String name)
	{
		Map<String, CacheConfiguration> configs = cacheConfigs.getCacheConfigurations();
		if (configs.containsKey(name))
		{
			return configs.get(name);
		}
		else
		{
			CacheConfiguration defaultConfig = cacheConfigs.getDefaultCacheConfiguration();
			CacheConfiguration conf = defaultConfig.clone();
			conf.setName(name);
			return conf;
		}
	}
}
