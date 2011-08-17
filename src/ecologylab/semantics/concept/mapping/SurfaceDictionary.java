package ecologylab.semantics.concept.mapping;

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
import ecologylab.semantics.concept.utils.TextUtils;

/**
 * 1. Cache frequent surfaces for use. 2. Extract surfaces from free text using prefix matching.
 * 
 * @author quyin
 * 
 */
public class SurfaceDictionary extends Debug
{

	public static final String				SURFACE_DICTIONARY_PATH	= "surface_dictionary.path";

	/**
	 * The deliminator between surface and sense count.
	 */
	public static final String				DELIM										= "|";

	/**
	 * The regex representation of DELIM.
	 */
	public static final String				DELIM_REGEX							= "\\|";

	public static final int						pU;

	public static final int						pM;

	static
	{
		pU = Configs.getInt("surface_dictionary.punish_unmatch");
		pM = Configs.getInt("surface_dictionary.punish_match");
	}

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
					File dictPath = Configs.getFile(SURFACE_DICTIONARY_PATH);
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
								}
							}
							else
							{
								dict.warning("ignoring dictionary line: " + line);
							}
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
	 * extract surfaces in this dictionary, using a "punishment" method (punishing parameters can be
	 * tuned), implemented with DP.
	 * 
	 * ref: http://www.matrix67.com/blog/archives/4212
	 * 
	 * @param text
	 * @param outSurfaces
	 *          output buffer receiving extracted surfaces. ignored if == null.
	 * @param outTextOffsets
	 *          output buffer receiving text offsets of extracted surfaces. ignored if == null.
	 * @return number of surfaces extracted
	 */
	public int extractSurfaces(String text, List<String> outSurfaces, List<Integer> outTextOffsets)
	{
		// add trailing whitespace
		text = text + " ";

		// pre-compute positions of each term
		int n = TextUtils.count(text, ' ');
		int[] pos = new int[n + 1];
		pos[0] = 0;
		int i0 = 1;
		for (int i = 0; i < text.length(); ++i)
		{
			if (text.charAt(i) == ' ')
			{
				pos[i0] = i + 1;
				i0++;
			}
		}

		// optimize cost to segment stream of terms into surfaces, using DP
		// f[i] = lowest cost of term_i to term_{n-1}
		int[] f = new int[n + 1];
		// trace[i] = optimal length of surface at term_i; 0 means no surfaces found from here
		int[] trace = new int[n + 1];
		f[n] = 0;
		trace[n] = -1;
		for (int i = n - 1; i >= 0; --i)
		{
			int c0 = f[i + 1] + pU; // case 0: take the next term as unmatch

			// other cases: match partial text from term i
			int bestCost = c0;
			int bestCostTermCount = 0;
			int p = pos[i];
			Map<String, Integer> matches = new HashMap<String, Integer>();
			surfaces.prefixMatch(text, p, matches);
			for (String match : matches.keySet())
			{
				int l = TextUtils.count(match, ' '); // there is a trailing whitespace
				int cost = f[i + l] + pM;
				if (cost < bestCost)
				{
					bestCost = cost;
					bestCostTermCount = l;
				}
			}

			trace[i] = bestCostTermCount;
			f[i] = bestCost;
		}

		// rebuild optimal solution from traces
		int count = 0;
		int i = 0;
		while (trace[i] >= 0)
		{
			if (trace[i] == 0)
			{
				// unmatch
				i++;
			}
			else
			{
				// match
				int p0 = pos[i];
				i += trace[i];
				int p1 = pos[i];
				String s = text.substring(p0, p1 - 1); // minus 1 to eliminate the trailing whitespace
				if (outSurfaces != null)
					outSurfaces.add(s);
				if (outTextOffsets != null)
					outTextOffsets.add(p0);
				count++;
			}
		}
		return count;
	}

	public static void main(String[] args) throws IOException
	{
		Session session = SessionManager.newSession();
		WikiConcept concept = WikiConcept.getByTitle("Foreign relations of Colombia", session);
		String text = concept.getText();
		session.close();

		// BufferedWriter out = new BufferedWriter(new FileWriter("usa.txt"));
		// out.write(text);
		// out.newLine();
		// out.close();

		// String text1 = "we know that united states 2000 census is famous in united states";

		SurfaceDictionary dict = SurfaceDictionary.get();
		List<String> surfaces = new ArrayList<String>();
		List<Integer> offsets = new ArrayList<Integer>();
		int count = dict.extractSurfaces(text, surfaces, offsets);
		for (int i = 0; i < count; ++i)
		{
			System.out.println(surfaces.get(i) + "@" + offsets.get(i));
		}
		System.out.println(count + " surface(s) found.");
	}

}
