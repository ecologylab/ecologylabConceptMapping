package ecologylab.semantics.concept.utils;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;

import org.junit.Test;

public class Log
{
	
	private PrintStream out = System.out;
	
	public void setLogDestination(PrintStream out)
	{
		if (out != null)
		{
			this.out = out;
		}
	}
	
	protected void log(String fmt, Object... args)
	{
		DateFormat df = DateFormat.getDateTimeInstance();
		String msg = String.format(fmt, args);
		out.format("[%s] %s: %s\n", df.format(new Date()), this.getClass().getSimpleName(), msg);
	}
	
	@Test
	public void testLog()
	{
		log("%s - %d", "abc", 123);
	}
	
}
