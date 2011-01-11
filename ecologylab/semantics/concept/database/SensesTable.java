package ecologylab.semantics.concept.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ecologylab.semantics.concept.detect.Concept;
import ecologylab.semantics.concept.exception.UnsupportedError;

class SensesTable implements SimpleTable<String, Map<Concept, Double>>
{

	public static final String NAME = "senses";
	
	public String getName()
	{
		return NAME;
	}

	@Override
	public void create(String key, Map<Concept, Double> value)
	{
		String surface = key;
		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"INSERT INTO commonness VALUES (?, ?, ?);");
		synchronized (pst)
		{
			for (Concept concept : value.keySet())
			{
				double commonness = value.get(concept);
				try
				{
					pst.setString(1, surface);
					pst.setString(2, concept.title);
					pst.setDouble(3, commonness);
					pst.executeUpdate();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public Map<Concept, Double> read(String key)
	{
		Map<Concept, Double> rst = new HashMap<Concept, Double>();
		
		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"SELECT concept, commonness FROM commonnness WHERE surface=?;");
		synchronized(pst)
		{
			try
			{
				pst.setString(1, key);
				ResultSet rs = pst.executeQuery();
				while (rs.next())
				{
					String concept = rs.getString(1);
					double commonness = rs.getDouble(2);
					rst.put(new Concept(concept), commonness);
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return rst;
	}

	@Override
	public int update(String key, Map<Concept, Double> value)
	{
		throw new UnsupportedError("senses.update not supported.");
	}

	@Override
	public int delete(String key)
	{
		throw new UnsupportedError("senses.delete not supported.");
	}

}
