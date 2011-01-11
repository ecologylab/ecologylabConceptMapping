package ecologylab.semantics.concept.database;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import ecologylab.generic.Debug;

public class CachedTable<K, V> extends Debug implements CacheEntryFactory
{

	private SimpleTable<K, V>	table;

	private Ehcache						cache;

	public CachedTable(SimpleTable<K, V> table)
	{
		this.table = table;
		CacheConfiguration cacheConfig = CachedTables.getCacheConfig(table.getName());
		cache = new SelfPopulatingCache(new Cache(cacheConfig), this);
	}

	SimpleTable<K, V> getTable()
	{
		return table;
	}

	Ehcache getEhcache()
	{
		return cache;
	}

	public V get(K key)
	{
		Element element = cache.get(key);
		return element == null ? null : (V) element.getObjectValue();
	}

	@Override
	public Object createEntry(Object key) throws Exception
	{
		return table.read((K) key);
	}

}
