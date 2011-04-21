package ecologylab.semantics.concept.preparation.postparsing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiSurface;
import ecologylab.semantics.concept.utils.StopWordsUtils;

/**
 * initialize or build the surface dictionary. filter out stop words.
 * 
 * @author quyin
 * 
 */
public class SurfaceDictionaryBuilder
{

	public void buildDictionary(OutputStream out, boolean withOccurrenceCount)
	{
		Session session = SessionManager.newSession();

		Criteria q = session.createCriteria(WikiSurface.class);
		q.addOrder(Order.asc("surface"));
		ScrollableResults results = q.scroll();
		while (results.next())
		{
			WikiSurface ws = (WikiSurface) results.get(0);
			String surface = ws.getSurface();
			if (StopWordsUtils.containsLetter(surface) && !StopWordsUtils.isStopWord(surface))
			{
				String line = String.format("%s|%d\n", surface,
						withOccurrenceCount ? ws.getTotalOccurrence() : 0);
				try
				{
					out.write(line.getBytes("UTF-8"));
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
		}
		results.close();

		session.close();
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println("opts:");
			System.err
					.println("  -i <dictionary-path>: initialization (without occurrence count; used for calculating keyphraseness;");
			System.err.println("  -b <dictionary-path>: building (with occurrence count).");
		}

		SurfaceDictionaryBuilder builder = new SurfaceDictionaryBuilder();
		OutputStream out = new FileOutputStream(args[1]);

		if (args[0].equals("-i"))
		{
			builder.buildDictionary(out, false);
		}
		else if (args[0].equals("-b"))
		{
			builder.buildDictionary(out, true);
		}

		out.close();
	}

}
