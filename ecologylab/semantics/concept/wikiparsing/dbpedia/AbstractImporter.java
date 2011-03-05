package ecologylab.semantics.concept.wikiparsing.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import ecologylab.generic.Debug;

/**
 * The abstract class to import records from a line-based text file into the database.
 * 
 * @author quyin
 *
 */
public abstract class AbstractImporter extends Debug
{

	/**
	 * Parse a given file and import records from it.
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public void parse(String filePath) throws IOException
	{
		parse(new File(filePath));
	}

	/**
	 * Parse a given file and import records from it.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void parse(File file) throws IOException
	{
		parse(new FileInputStream(file));
	}

	/**
	 * Parse a given input stream and import records from it.
	 * 
	 * @param stream
	 * @throws IOException
	 */
	public void parse(InputStream stream) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			try
			{
				parseLine(line);
			}
			catch (Exception e)
			{
				error(e.getMessage() + " when parsing line: " + line);
			}
		}
		br.close();
	}

	/**
	 * Parse a given list of lines and import records from it.
	 * 
	 * @param lines
	 */
	public void parse(List<String> lines)
	{
		for (String line : lines)
		{
			try
			{
				parseLine(line);
			}
			catch (Exception e)
			{
				error(e.getMessage() + " when parsing line: " + line);
			}
		}
	}

	/**
	 * The parsing function, accepting a line, extracting information and storing to the database.
	 * Sub-classes should implement this function to do parsing.
	 * 
	 * @param line
	 */
	abstract public void parseLine(String line);

}
