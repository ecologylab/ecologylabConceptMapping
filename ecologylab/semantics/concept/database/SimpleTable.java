package ecologylab.semantics.concept.database;

/**
 * object for a simple key-value pair table supporing CRUD.
 * 
 * @author quyin
 *
 * @param <K>
 * @param <V>
 */
interface SimpleTable<K, V>
{
	
	String getName();
	
	void create(K key, V value);

	V read(K key);

	int update(K key, V value);

	int delete(K key);
	
}
