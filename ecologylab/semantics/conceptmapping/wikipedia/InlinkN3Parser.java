package ecologylab.semantics.conceptmapping.wikipedia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InlinkN3Parser
{
	public class Inlink
	{
		public String	toConcept;

		public String	fromConcept;

		public String	surface;
	}

	private Pattern	p	= Pattern
												.compile("\\[esc:name \"(.*)\"\\] esc:linked_by \\[esc:name \"(.*)\"\\]; esc:surface \"(.*)\" \\.");

	public Inlink parse(String line)
	{
		Matcher m = p.matcher(line);
		if (!m.matches())
		{
			System.err.println("cannot parse line: " + line);
		}

		Inlink il = new Inlink();
		il.toConcept = m.group(1);
		il.fromConcept = m.group(2);
		il.surface = m.group(3);
		
		if (il.toConcept == null || il.fromConcept == null || il.surface == null)
		{
			System.err.println("parsing error (null string occurs) for line: " + line);
		}

		return il;
	}

	public static void main(String[] args)
	{
		String test = "[esc:name \"\"I Am\" Religious Activity of the Saint Germain Foundation\"] esc:linked_by [esc:name \"Charles Sindelar\"]; esc:surface \"\"I AM\" Religious Activity of the Saint Germain Foundation\" .";

		InlinkN3Parser p = new InlinkN3Parser();
		Inlink il = p.parse(test);
		System.out.format("%s,%s,%s", il.toConcept, il.fromConcept, il.surface);
	}
}
