package ecologylab.semantics.concept.database.orm;

import java.io.Serializable;

public class TopRelatedLinks implements Serializable
{

	private int fromId;
	
	private int toId;
	
	private double relatedness;

	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
}
