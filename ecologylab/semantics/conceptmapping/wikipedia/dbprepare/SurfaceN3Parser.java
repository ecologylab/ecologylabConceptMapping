package ecologylab.semantics.conceptmapping.wikipedia.dbprepare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SurfaceN3Parser
{
	public class Surface
	{
		public String	surfaceName;

		public String	concept;
	}

	private Pattern	p	= Pattern
												.compile("\\[esc:words \"(.*)\"\\] esc:surface_of \\[esc:name \"(.*)\"\\] \\.");

	public Surface parse(String line)
	{
		Matcher m = p.matcher(line);
		if (!m.matches())
		{
			System.err.println("cannot parse line: " + line);
			return null;
		}

		Surface s = new Surface();
		s.surfaceName = m.group(1);
		s.concept = m.group(2);

		if (s.surfaceName == null || s.concept == null)
		{
			System.err.println("parsing error (null string occurs) for line: " + line);
		}
		
		return s;
	}

	public static void main(String[] args)
	{
		String test = "[esc:words \"pl:GÃ³rnoslÄski OkrÄg PrzemysÅowy\"] esc:surface_of [esc:name \"pl:GÃ³rnoslÄski_OkrÄg_PrzemysÅowy\"] .";

		SurfaceN3Parser p = new SurfaceN3Parser();
		Surface s = p.parse(test);
		System.out.format("%s,%s", s.surfaceName, s.concept);
	}
}
