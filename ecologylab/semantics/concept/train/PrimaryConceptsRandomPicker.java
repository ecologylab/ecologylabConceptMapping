package ecologylab.semantics.concept.train;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class PrimaryConceptsRandomPicker extends Debug
{

	public List<String> pick(int n) throws SQLException
	{
		List<String> picked = new ArrayList<String>();
	  ResultSet rs = DatabaseAdapter.get().executeQuerySql("SELECT name FROM dbp_primary_concepts;");
		while (rs.next())
		{
			String concept = rs.getString("name");
			picked.add(concept);
		}
		CollectionUtils.randomPermute(picked, n);
		return picked.subList(0, n);
	}

	public static void main(String[] args) throws FileNotFoundException, SQLException
	{
		if (args.length != 1)
		{
			System.err.println("args: n: how many concepts you want to pick.");
			return;
		}
		int n = Integer.parseInt(args[0]);
		
		PrimaryConceptsRandomPicker pcrp = new PrimaryConceptsRandomPicker();
		PrintWriter out = new PrintWriter(new File("data/primary-concepts-" + n + ".lst")); 
		for (String concept: pcrp.pick(n))
		{
			out.println(concept);
		}
		out.close();
	}

}
