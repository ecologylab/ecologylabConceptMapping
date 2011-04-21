package ecologylab.semantics.concept.preparation.postparsing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.detect.SurfaceDictionary;
import ecologylab.semantics.concept.utils.TextUtils;

public class KeyphrasenessCalculator
{

	public static void main(String[] args) throws IOException
	{
		if (args.length == 0)
		{
			System.err
					.println("note that you need the configuration file, surface dictionary (w/ or w/o occurrence count) and a concept list file to run this program!");
			System.err.println("opts:");
			System.err
					.println("  -g --gen-concept-list <concept-list-file-path>: generate concept list file (for distributed computing);");
			System.err
					.println("  -l --concept-list <concept-list-file-path>: the path to a concept list;");
			System.err
					.println("  -o --offset <offset>: the offset of starting concept in the list file;");
			System.err
					.println("  -n --number <number>: the number of concepts you want to handle with this process.");
			System.exit(-1);
		}

		KeyphrasenessCalculator kc = new KeyphrasenessCalculator();

		File conceptListF = null;
		int offset = -1;
		int number = -1;

		int i = 0;
		while (i < args.length)
		{
			if (args[i].equals("-g") || args[i].equals("--gen-concept-list"))
			{
				if (i + 1 >= args.length)
				{
					System.err.println("concept list file path unspecified.");
					System.exit(-2);
				}
				String conceptListPath = args[i + 1];
				OutputStream conceptListOut = new FileOutputStream(conceptListPath);
				kc.generateConceptList(conceptListOut);
				conceptListOut.close();
				return;
			}
			else if (args[i].equals("-l") || args[i].equals("--concept-list"))
			{
				if (i + 1 >= args.length)
				{
					System.err.println("concept list file path unspecified.");
					System.exit(-2);
				}
				String conceptListPath = args[i + 1];
				conceptListF = new File(conceptListPath);
			}
			else if (args[i].equals("-o") || args[i].equals("--offset"))
			{
				if (i + 1 >= args.length)
				{
					System.err.println("offset unspecified.");
					System.exit(-2);
				}
				offset = Integer.parseInt(args[i + 1]);
			}
			else if (args[i].equals("-n") || args[i].equals("--number"))
			{
				if (i + 1 >= args.length)
				{
					System.err.println("number unspecified.");
					System.exit(-2);
				}
				number = Integer.parseInt(args[i + 1]);
			}
		}

		if (conceptListF != null && offset >= 0 && number >= 0)
		{
			kc.calculateKeyphraseness(conceptListF, offset, number);
		}
		else
		{
			System.err.println("not enough information! run the program w/o arguments to see help.");
			System.exit(-3);
		}
	}

	private void calculateKeyphraseness(File conceptListF, int offset, int number) throws IOException
	{
		List<String> titles = TextUtils.loadTxtAsList(conceptListF, false);
		titles = titles.subList(offset, offset + number);

		Session session = SessionManager.newSession();

		for (String title : titles)
		{
			session.beginTransaction();

			WikiConcept wc = WikiConcept.getByTitle(title, session);
			if (wc != null)
			{
				String text = wc.getText();
				List<String> surfaces = SurfaceDictionary.get().extractSurfaces(text);
				for (String surface : surfaces)
				{
					WikiSurface ws = WikiSurface.get(surface, session);
					if (ws != null)
					{
						ws.setTotalOccurrence(ws.getTotalOccurrence() + 1);
						session.update(ws);
					}
				}
			}

			session.getTransaction().commit();
		}
		
		Criteria q = session.createCriteria(WikiLink.class);
		q.setProjection(Projections.projectionList()
				.add(Projections.rowCount())
				.add(Projections.groupProperty("surface"))
				);
		ScrollableResults results = q.scroll();
		while (results.next())
		{
			session.beginTransaction();
			
			// TODO the type and structure of results need testing!
			int count = results.getInteger(0);
			String surface = results.getString(1);
			
			WikiSurface ws = WikiSurface.get(surface, session);
			if (ws != null)
			{
				ws.setLinkedOccurrence(count);
				session.update(ws);
			}
			
			session.getTransaction().commit();
		}

		session.close();
	}

	private void generateConceptList(OutputStream out)
	{
		Session session = SessionManager.newSession();

		Criteria q = session.createCriteria(WikiConcept.class);
		q.addOrder(Order.asc("id"));
		ScrollableResults results = q.scroll();
		while (results.next())
		{
			WikiConcept wc = (WikiConcept) results.get(0);
			try
			{
				out.write(wc.getTitle().getBytes("UTF-8"));
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		results.close();

		session.close();
	}

}
