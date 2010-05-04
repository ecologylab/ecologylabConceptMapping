package ecologylab.semantics.conceptmapping.wikipedia.featureextraction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ecologylab.semantics.conceptmapping.wikipedia.utils.DatabaseUtils;

public class DisambiguationFeatureExtractorOneModel extends DisambiguationFeatureExtractor
{

	public static final int	N	= 100;

	private PrintWriter			out;

	public DisambiguationFeatureExtractorOneModel(String resultFilepath) throws IOException
	{
		super();
		out = new PrintWriter(new FileWriter(resultFilepath));
	}

	public void extract(String... ambiSurfaces)
	{
		for (String s : ambiSurfaces)
		{
			try
			{
				List<Context> contexts = getContexts(s);
				assert (contexts.size() > 0);
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

	public static void main(String[] args)
	{
		try
		{
			DisambiguationFeatureExtractorOneModel extractor = new DisambiguationFeatureExtractorOneModel(
					"onemodel-features.dat");
			List<String> surfaces = DatabaseUtils.getSurfaces(extractor.getDatabaseAdapter());
			assert (surfaces.size() > N);

			Set<String> testingSurfaces = new HashSet<String>();
			while (testingSurfaces.size() < N)
			{
				int k = (int) (surfaces.size() * Math.random());
				String surface = surfaces.get(k);
				testingSurfaces.add(surface);
			}

			extractor.extract(testingSurfaces.toArray(new String[0]));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
