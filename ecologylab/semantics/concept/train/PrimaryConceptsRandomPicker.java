package ecologylab.semantics.concept.train;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionPool;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.utils.CollectionUtils;

public class PrimaryConceptsRandomPicker
{

	public static List<String> pickRandomPrimaryConcepts(int n) throws SQLException
	{
		List<String> picked = new ArrayList<String>();
		
		Session session = SessionPool.getSession();
		Criteria q = session.createCriteria(WikiConcept.class);
		ScrollableResults results = q.scroll();
		while (results.next())
		{
			WikiConcept concept = (WikiConcept) results.get(0);
			String title = concept.getTitle();
			picked.add(title);
		}
		results.close();
		session.close();
		
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
