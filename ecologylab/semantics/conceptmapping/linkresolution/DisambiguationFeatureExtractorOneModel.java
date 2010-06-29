package ecologylab.semantics.conceptmapping.linkresolution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DisambiguationFeatureExtractorOneModel extends DisambiguationFeatureExtractor
{

	public static final int	N	= 10000;

	private PrintWriter			out;

	public DisambiguationFeatureExtractorOneModel(String resultFilepath) throws IOException
	{
		super();
		out = new PrintWriter(new FileWriter(resultFilepath));
	}
	
	public void close()
	{
		out.close();
	}

	public void extract(String... ambiSurfaces)
	{
		for (String s : ambiSurfaces)
		{
			try
			{
				List<Context> contexts = getContexts(s);
				assert (contexts.size() > 0);
				out.println("#contexts: " + contexts.size());
				Context C = contexts.get(0);
				try
				{
					List<DisambiguationInstance> insts = processASurfaceOccurrence(s, C);
					if (insts != null)
						storeInstances(insts.toArray(new DisambiguationInstance[0]));
				}
				catch (SQLException e)
				{
					System.err.println("cannot process surface " + s + " in context " + C + ": "
							+ e.getMessage());
				}
			}
			catch (SQLException e)
			{
				System.err.println("cannot get contexts for surface " + s + ": " + e.getMessage());
			}
		}
	}

	protected void storeInstances(DisambiguationInstance... instances)
	{
		for (DisambiguationInstance inst : instances)
		{
			String s = String.format("%s,%f,%f,%f#%s->%s", inst.target, inst.commonness,
					inst.contextualRelatedness, inst.contextQuality, inst.surface, inst.concept);
			System.out.println(s);
			out.println(s);
		}
	}
	
	public static List<String> readAmbiguousSurfaces(String ambiSurfacesFilepath) throws IOException
	{
		List<String> rst = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(ambiSurfacesFilepath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			line = line.trim();
			if (line.isEmpty())
				continue;
			rst.add(line);
		}
		br.close();
		
		return rst;
	}

	public static void main(String[] args)
	{
		try
		{
			List<String> surfaces = readAmbiguousSurfaces("C:/run/commonness/ambi-surfaces.lst");
			assert (surfaces.size() > N);

			Set<String> testingSurfaces = new HashSet<String>();
			while (testingSurfaces.size() < N)
			{
				int k = (int) (surfaces.size() * Math.random());
				String surface = surfaces.get(k);
				testingSurfaces.add(surface);
			}

			DisambiguationFeatureExtractorOneModel extractor = new DisambiguationFeatureExtractorOneModel(
					"onemodel-features.dat");
			extractor.extract(testingSurfaces.toArray(new String[0]));
			extractor.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
