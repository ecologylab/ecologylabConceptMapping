package ecologylab.semantics.concept.preparation.postparsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.detect.SurfaceDictionary;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.PrefixTree;

public class KeyphrasenessCalculator
{

	List<String>				surfaces	= new ArrayList<String>();

	PrefixTree<Integer>	ptree			= new PrefixTree<Integer>();

	private void calculateKeyphraseness() throws FileNotFoundException
	{
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
		}
		results.close();
		session.close();

		// save in-memory counts to the database
		for (String surface : surfaces)
		{
			session = SessionManager.newSession();
			Transaction tx = session.beginTransaction();
			WikiSurface ws = WikiSurface.get(surface, session);
			Integer totalOccur = ptree.get(surface);
			if (ws != null && totalOccur != null && totalOccur > 0)
			{
				ws.setTotalOccurrence(totalOccur);
			}
			tx.commit();
			session.close();
		}
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		KeyphrasenessCalculator kc = new KeyphrasenessCalculator();
		kc.calculateKeyphraseness();
	}

}
