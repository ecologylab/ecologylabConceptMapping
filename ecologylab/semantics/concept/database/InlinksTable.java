package ecologylab.semantics.concept.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import ecologylab.semantics.concept.exception.UnsupportedError;

class InlinksTable implements SimpleTable<String, Set<String>>
{
	
	public static final String NAME = "inlinks_table";
	
	public String getName()
	{
		return NAME;
	}

	@Override
	public void create(String key, Set<String> value)
	{
		throw new UnsupportedError("inlinks.create not supported.");
	}

	@Override
	public Set<String> read(String key)
	{
		Set<String> rst = new HashSet<String>();
		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"SELECT from_title FROM wikilinks WHERE to_title=?;");
		synchronized (pst)
		{
			try
			{
				pst.setString(1, key);
				ResultSet rs = pst.executeQuery();
				while (rs.next())
				{
					String inlinkConceptTitle = rs.getString(1);
					rst.add(inlinkConceptTitle);
				}
				rs.close();
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
	public int update(String key, Set<String> value)
	{
		throw new UnsupportedError("inlinks.update not supported.");
	}

	@Override
	public int delete(String key)
	{
		throw new UnsupportedError("inlinks.delete not supported.");
	}

}
