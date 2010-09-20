package ecologylab.semantics.concept.train;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecologylab.generic.Debug;
import ecologylab.net.ParsedURL;
import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.wikiparsing.Concept;
import ecologylab.semantics.concept.wikiparsing.ConceptPool;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.TranslationScope;

public class URLListGenerator extends Debug
{

	private Map<String, ParsedURL>	urlMap;

	public URLListGenerator(String conceptXmlFolder)
	{
		urlMap = new HashMap<String, ParsedURL>();

		File folder = new File(conceptXmlFolder);
		File[] xmlFiles = folder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				if (name.endsWith(".xml"))
					return true;
				return false;
			}
		});

		TranslationScope ts = TranslationScope.get("url-list-generator", ConceptPool.class,
				Concept.class);
		for (File xmlFile : xmlFiles)
		{
			debug("processing " + xmlFile + " ...");
			try
			{
				ConceptPool pool = (ConceptPool) ts.deserialize(xmlFile);
				debug(pool.getPool().size() + " concepts found.");
				for (Concept concept : pool.getPool())
				{
					urlMap.put(concept.getName(), concept.getPurl());
				}
			}
			catch (SIMPLTranslationException e)
			{
				error("exception when processing " + xmlFile);
				e.printStackTrace();
			}
		}
	}

	public List<ParsedURL> generate(int n) throws SQLException
	{
		List<ParsedURL> rst = new ArrayList<ParsedURL>();

		PreparedStatement ps = DatabaseAdapter
				.get()
				.getPreparedStatement(
						"SELECT concept FROM concept_count WHERE concept SIMILAR TO '[^0-9]*' ORDER BY count DESC LIMIT ?;");
		ps.setInt(1, n + n / 5);

		ResultSet rs = ps.executeQuery();
		int i = 0;
		while (rs.next() && i < n)
		{
			String concept = null;
			try
			{
				concept = rs.getString("concept");
				ParsedURL purl = urlMap.get(concept);
				if (purl != null)
				{
					String urlString = purl.toString();
					urlString = urlString.replace("http://achilles/", "http://achilles.cse.tamu.edu/");
					rst.add(ParsedURL.getAbsolute(urlString));
					i++;
				}
				else
				{
					warning("url not found for " + concept);
				}
			}
			catch (SQLException e)
			{
				error("exception when processing " + concept);
				e.printStackTrace();
			}
		}

		return rst;
	}

	public static void main(String[] args) throws FileNotFoundException, SQLException
	{
		URLListGenerator ulg = new URLListGenerator("C:/run/all-concepts");
		
		PrintWriter out = new PrintWriter(new File("data/trainset-url.lst"));
		for (ParsedURL purl : ulg.generate(10000))
		{
			out.println(purl);
		}
		
		out.close();
	}

}
