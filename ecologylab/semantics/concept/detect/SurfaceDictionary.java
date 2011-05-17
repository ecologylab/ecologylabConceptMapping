package ecologylab.semantics.concept.detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import ecologylab.generic.Debug;
import ecologylab.semantics.concept.database.SessionManager;
import ecologylab.semantics.concept.database.orm.WikiConcept;
import ecologylab.semantics.concept.service.Configs;
import ecologylab.semantics.concept.utils.PrefixTree;

/**
 * 1. Cache frequent surfaces for use. 2. Extract surfaces from free text using prefix matching.
 * 
 * @author quyin
 * 
 */
public class SurfaceDictionary extends Debug
{

	/**
	 * The deliminator between surface and sense count.
	 */
	public static final String				DELIM				= "|";

	/**
	 * The regex representation of DELIM.
	 */
	public static final String				DELIM_REGEX	= "\\|";

	private static SurfaceDictionary	the;

	/**
	 * Get the singleton of global SurfaceDictionary.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static SurfaceDictionary get()
	{
		if (the == null)
		{
			synchronized (SurfaceDictionary.class)
			{
				if (the == null)
				{
					SurfaceDictionary dict = new SurfaceDictionary();
					File dictPath = Configs.getFile("surface_dictionary_path");
					dict.debug("loading surface dictionary from " + dictPath + " ...");
					try
					{
						BufferedReader br = new BufferedReader(new FileReader(dictPath));
						String line = null;
						while ((line = br.readLine()) != null)
						{
							String[] parts = line.trim().split(SurfaceDictionary.DELIM_REGEX);
							if (parts.length == 2)
							{
								String surface = parts[0];
								if (surface != null && !surface.isEmpty())
								{
									int count = Integer.parseInt(parts[1]);

									// add a trailing whitespace to denote word boundary
									dict.surfaces.put(surface + " ", count);

									continue;
								}
							}
							dict.warning("ignoring dictionary line: " + line);
						}
						br.close();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dict.debug("surface dictionary loaded.");

					the = dict;
				}
			}
		}
		return the;
	}

	// surfaces in it have an extra trailing whitespace " " to denote word boundary
	// but this trick should be transparent to outside of this class
	private PrefixTree<Integer>	surfaces	= new PrefixTree<Integer>();

	private SurfaceDictionary()
	{

	}

	/**
	 * How many senses does this surface have?
	 * 
	 * @param surface
	 * @return 0 if not contained in this dictionary, or a positive value.
	 */
	public int getSenseCount(String surface)
	{
		Integer c = surfaces.get(surface + " ");
		assert c >= 0 : "negative value for surface sense count: " + surface;
		return c == null ? 0 : c;
	}

	/**
	 * Extract surfaces using this dictionary.
	 * 
	 * @param text
	 *          Normalized text.
	 * @return
	 */
	public List<String> extractSurfaces(String text)
	{
		List<String> rst = new ArrayList<String>();

		int offset = 0;

		while (offset < text.length())
		{
			// results of this iteration
			List<Integer> currentResult = new ArrayList<Integer>();
			List<Integer> bestResult = new ArrayList<Integer>();

			// extract
			extractSurfacesHelper(text, 0, currentResult, bestResult);

			// cumulate results
			if (bestResult.size() > 0)
			{
				int p = 0;
				for (int i = 0; i < bestResult.size(); ++i)
				{
					int q = bestResult.get(i);
					String result = text.substring(p, q);
					rst.add(result.trim());
				}

				// update offset
				offset += bestResult.get(bestResult.size() - 1);
			}

			// the word at offset is not a prefix of a surface, move to the next whitespace
			while (offset < text.length() && text.charAt(offset) != ' ')
				offset++;
			// skip the whitespace to the beginning of the next word
			offset++;
		}

		return rst;
	}

	/**
	 * extract surfaces from input text in an optimized way, and save result in bestResult.
	 * 
	 * the goal is to match as longer text with as fewer surfaces as possible.
	 * 
	 * note that the last integer in bestResult is the offset of not-yet-extracted text.
	 * 
	 * @param text
	 * @param offset
	 * @param currentResult
	 * @param bestResult
	 */
	private void extractSurfacesHelper(String text, int offset, List<Integer> currentResult,
			List<Integer> bestResult)
	{
		Map<String, Integer> matches = new HashMap<String, Integer>();
		surfaces.prefixMatch(text, offset, matches);
		if (matches.size() == 0)
		{
			if (bestResult.size() > 0)
			{
				int bestResultMaxOffset = bestResult.get(bestResult.size() - 1);
				if (bestResultMaxOffset > offset)
					return;
				if (bestResultMaxOffset == offset && bestResult.size() <= currentResult.size())
					return;
			}
			bestResult.clear();
			bestResult.addAll(currentResult);
		}
		for (String match : matches.keySet())
		{
			offset += match.length();
			currentResult.add(offset);
			extractSurfacesHelper(text, offset, currentResult, bestResult);
			currentResult.remove(currentResult.size() - 1);
			offset -= match.length();
		}
	}

	public static void main(String[] args) throws IOException
	{
		Session session = SessionManager.newSession();
		WikiConcept concept = WikiConcept.getByTitle("United States", session);
		String text = concept.getText();
		session.close();

		SurfaceDictionary dict = SurfaceDictionary.get();
		List<String> testSurfaces = dict.extractSurfaces(text);
		for (String s : testSurfaces)
		{
			System.out.println(s);
		}
		System.out.println(testSurfaces.size() + " surface(s) found.");
	}

}
