package ecologylab.semantics.concept.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ecologylab.semantics.concept.exception.UnsupportedError;

class KeyphrasenessTable implements SimpleTable<String, Double>
{

	public static final String NAME = "keyphraseness";
	
	public String getName()
	{
		return NAME;
	}

	@Override
	public void create(String key, Double value)
	{
		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"INSERT INTO keyphraseness VALUES (?, ?);");
		synchronized (pst)
		{
			try
			{
				pst.setString(1, key);
				pst.setDouble(2, value);
				pst.executeUpdate();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Double read(String key)
	{
		double kp = 0;
		
		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"SELECT keyphraseness FROM keyphraseness WHERE surface=?;");
		synchronized (pst)
		{
			try
			{
				pst.setString(1, key);
				ResultSet rs = pst.executeQuery();
				if (rs.next())
				{
					kp = rs.getDouble(1);
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return kp;
	}

	@Override
	public int update(String key, Double value)
	{
		throw new UnsupportedError("keyphraseness.update not supported.");
	}

	@Override
	public int delete(String key)
	{
		throw new UnsupportedError("keyphraseness.delete not supported.");
	}

}
