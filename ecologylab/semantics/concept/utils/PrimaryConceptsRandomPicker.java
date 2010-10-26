package ecologylab.semantics.concept.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ecologylab.semantics.concept.database.DatabaseFacade;

public class PrimaryConceptsRandomPicker
{

	public static List<String> pickRandomPrimaryConcepts(int n) throws SQLException
	{
		List<String> picked = new ArrayList<String>();
		
		String sql = "SELECT title FROM dbp_primary_concepts, dbp_titles WHERE dbp_primary_concepts.name = dbp_titles.name;";
		Statement st = DatabaseFacade.get().getStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next())
		{
			String concept = rs.getString("title");
			picked.add(concept);
		}
		rs.close();
		st.close();
		
		CollectionUtils.randomPermute(picked, n);
		return picked.subList(0, n);
	}
	
	public static void main(String[] args) throws FileNotFoundException, SQLException
	{
		if (args.length != 1)
		{
			System.err.println("args: <n>\n  n: how many concepts you want to pick.");
			return;
		}
		int n = Integer.parseInt(args[0]);

		PrintWriter out = new PrintWriter(new File("data/primary-concepts-" + n + ".lst"));
		for (String concept : pickRandomPrimaryConcepts(n))
		{
			out.println(concept);
		}
		out.close();
	}

}
