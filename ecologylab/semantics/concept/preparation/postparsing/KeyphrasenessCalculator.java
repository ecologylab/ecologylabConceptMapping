package ecologylab.semantics.concept.preparation.postparsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.detect.SurfaceDictionary;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.PrefixTree;
import ecologylab.semantics.concept.utils.TextUtils;

public class KeyphrasenessCalculator
{

	List<String>				surfaces	= new ArrayList<String>();

	PrefixTree<Integer>	ptree			= new PrefixTree<Integer>();

	private void calculateKeyphraseness(String outPath) throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(outPath));

		// read surfaces from dictionary file
		File dictFile = Configs.getFile(SurfaceDictionary.SURFACE_DICTIONARY_PATH);
		BufferedReader br = new BufferedReader(new FileReader(dictFile));
		String line = null;
		while (true)
		{
			try
			{
				line = br.readLine();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			if (line == null)
				break;

			String[] parts = line.trim().split(SurfaceDictionary.DELIM_REGEX);
			if (parts.length == 2)
			{
				surfaces.add(parts[0]);
				ptree.put(parts[0], 0);
			}
		}

		// process concepts to count occurrences (in memory)
		ptree.setWarnDuplicateKey(false);
		int n = 1;
		Session session = SessionManager.newSession();
		Criteria q = session.createCriteria(WikiConcept.class);
		q.setCacheable(false);
		q.setFetchSize(100);
		ScrollableResults results = q.scroll(ScrollMode.FORWARD_ONLY);
		while (results.next())
		{
			WikiConcept concept = (WikiConcept) results.get(0);
			System.out.println(n + ": processing " + concept.getId());
			String text = concept.getText();
			List<String> extractedSurfaces = SurfaceDictionary.get().extractSurfaces(text);
			for (String surface : extractedSurfaces)
			{
				int prevTotalCount = ptree.get(surface);
				ptree.put(surface, prevTotalCount + 1);
			}
			n++;
			session.evict(concept);
		}
		results.close();
		session.close();

		// save in-memory counts to a script
		String sqlTemplate = "UPDATE wiki_surfaces SET total_occurrence=%d WHERE surface='%s';";
		for (String surface : surfaces)
		{
			Integer totalOccur = ptree.get(surface);
			if (totalOccur != null && totalOccur > 0)
			{
				String sql = String.format(sqlTemplate, totalOccur, TextUtils.sqlEscape(surface));
				out.write(sql);
				out.write("\n");
			}
		}

		out.close();
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			System.err.println("args: <output-script-path>");
			System.exit(-1);
		}

		String outPath = args[0];
		KeyphrasenessCalculator kc = new KeyphrasenessCalculator();
		kc.calculateKeyphraseness(outPath);
	}

}
