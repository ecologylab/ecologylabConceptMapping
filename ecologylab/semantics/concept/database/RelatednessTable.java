package ecologylab.semantics.concept.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import ecologylab.semantics.concept.exception.UnsupportedError;

class RelatednessTable implements SimpleTable<String, Double>
{

	public static final String NAME = "relatedness";
	
	public String getName()
	{
		return NAME;
	}

	@Override
	public void create(String key, Double value)
	{
		String[] titles = key.split("\t");
		String title1 = titles[0];
		String title2 = titles[1];
		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"INSERT INTO relatedness VALUES (?, ?, ?);");
		synchronized (pst)
		{
			try
			{
				pst.setString(1, title1);
				pst.setString(2, title2);
				pst.setDouble(3, value);
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
		String[] titles = key.split("\t");
		String title1 = titles[0];
		String title2 = titles[1];

		double rst = 0;

		PreparedStatement pst = DatabaseFacade.get().getPreparedStatement(
				"SELECT relatedness FROM relatedness WHERE title1=? AND title2=?;");
		synchronized (pst)
		{
			try
			{
				pst.setString(1, title1);
				pst.setString(2, title2);
				ResultSet rs = pst.executeQuery();
				if (rs.next())
				{
					rst = rs.getDouble(1);
				}
				else
				{
					// calc and store relatedness
					rst = calcRelatedness(title1, title2);
					create(key, rst);
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
	public int update(String key, Double value)
	{
		throw new UnsupportedError("relatedness.update not supported.");
	}

	@Override
	public int delete(String key)
	{
		throw new UnsupportedError("relatedness.delete not supported.");
	}

	private double calcRelatedness(String title1, String title2)
	{
		CachedTable inlinksTable = CachedTables.getCachedTable(InlinksTable.NAME);
		Set<String> inlinks1 = (Set<String>) inlinksTable.get(title1);
		Set<String> inlinks2 = (Set<String>) inlinksTable.get(title2);
		int s1 = inlinks1.size();
		int s2 = inlinks2.size();
		if (s1 > 0 && s2 > 0)
		{
			int smin = ((s1 > s2) ? s2 : s1);
			int smax = ((s1 > s2) ? s1 : s2);
			int s = 0;
			for (String str : inlinks1)
			{
				if (inlinks2.contains(str))
					s++;
			}
	
			if (s > 0)
				return (Math.log(smax) - Math.log(s)) / (Math.log(DatabaseFacade.get().getTotalConceptCount()) - Math.log(smin));
		}
		else
		{
			System.err.println("zero length inlink count: " + (s1 == 0 ? title1 : "") + "    " + (s2 == 0 ? title2 : ""));
		}
		
		return 0;
	}

}
