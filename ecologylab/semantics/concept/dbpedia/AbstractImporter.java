package ecologylab.semantics.concept.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import ecologylab.generic.Debug;

public abstract class AbstractImporter extends Debug
{

	public void parse(String filePath) throws IOException
	{
		parse(new File(filePath));
	}

	public void parse(File file) throws IOException
	{
		parse(new FileInputStream(file));
	}

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
		postParse();
	}

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

		postParse();
	}

	abstract public void parseLine(String line);

	public void postParse()
	{
		
	}

}
