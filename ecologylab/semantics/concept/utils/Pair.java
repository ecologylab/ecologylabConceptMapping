package ecologylab.semantics.concept.utils;

public class Pair<T1, T2>
{
	
	private final T1 first;
	private final T2 second;

	public Pair(T1 first, T2 second)
	{
		super();
		this.first = first;
		this.second = second;
	}
	
	public T1 getFirst()
	{
		return first;
	}
	
	public T2 getSecond()
	{
		return second;
	}
	
	public int hashCode()
	{
		int hash1 = first != null ? first.hashCode() : 0;
		int hash2 = second != null ? second.hashCode() : 0;
		return (hash1 + hash2) * hash2 + hash1;
	}

	public boolean equals(Object other)
	{
		if (other instanceof Pair)
		{
			Pair otherPair = (Pair) other;
			return isBothNullOrEqual(first, otherPair.first) && isBothNullOrEqual(second, otherPair.second);
		}
		return false;
	}

	private boolean isBothNullOrEqual(Object o1, Object o2)
	{
		if (o1 == null && o2 == null)
		{
			return true;
		}
		else if (o1 != null && o2 != null)
		{
			return o1.equals(o2);
		}
		return false;
	}
	
	public String toString()
	{
		return "(" + first + ", " + second + ")";
	}
}
