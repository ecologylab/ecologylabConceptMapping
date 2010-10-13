package ecologylab.semantics.concept.wikiparsing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.database.DatabaseUtils;

public class FrequentConceptsIdentifier
{
	private static int		threshold							= 5;

	private static String	freqConceptsFilePath	= "data/freq-concepts.lst";

	private static String	freqSurfacesFilePath	= "data/freq-surfaces.lst";

	private static String	sql;

	public FrequentConceptsIdentifier()
	{
		initSqls();
	}

	private void initSqls()
	{
		sql = "SELECT to_title, count(*) AS count FROM wikilinks GROUP BY to_title HAVING count(*) > "
				+ threshold + " ORDER BY count DESC;";
	}

	public void identify() throws IOException, SQLException
	{
		List<String> freqConcepts = new ArrayList<String>();
		BufferedWriter bw = new BufferedWriter(new FileWriter(freqConceptsFilePath));
		ResultSet rs = DatabaseAdapter.get().executeQuerySql(sql);
		while (rs.next())
		{
			String title = rs.getString("to_title");
			int count = rs.getInt("count");
			bw.write(String.format("%s\t%d", title, count));
			bw.newLine();

			freqConcepts.add(title);
		}
		rs.close();
		bw.close();

		int n = freqConcepts.size();
		System.out.println(n + " freq concepts recognized.");

		bw = new BufferedWriter(new FileWriter(freqSurfacesFilePath));
		for (int i = 0; i < freqConcepts.size(); ++i)
		{
			String concept = freqConcepts.get(i);
			List<String> surfaces = DatabaseUtils.get().querySurfaces(concept);
			for (String surface : surfaces)
			{
				bw.write(surface);
				bw.newLine();
			}
			if (i % 1000 == 999)
			{
				System.out.println(i + "concepts processed: " + i * 100.0 / n + "%");
			}
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		if (args.length == 3)
		{
			threshold = Integer.parseInt(args[0]);
			freqConceptsFilePath = args[1];
			freqSurfacesFilePath = args[2];
		}
		else
		{
			System.err.println("warning: using default threshold 5.");
			System.err.println("args: <threshold> <freq-concepts-file-path> <freq-surfaces-file-path>");
		}

		FrequentConceptsIdentifier fci = new FrequentConceptsIdentifier();
		fci.identify();
	}

}
