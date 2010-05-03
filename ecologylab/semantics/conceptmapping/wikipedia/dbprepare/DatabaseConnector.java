package ecologylab.semantics.conceptmapping.wikipedia.dbprepare;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import ecologylab.semantics.conceptmapping.wikipedia.dbprepare.InlinkN3Parser.Inlink;

public class DatabaseConnector
{
	private Connection				db;

	private PreparedStatement	inlinkUpdateStatement;

	private PreparedStatement	commonnessUpdateStatement;

	private PreparedStatement	keyphrasenessUpdateStatement;

	private InlinkN3Parser		inlinkParser	= new InlinkN3Parser();

	public void convertInlink(String inN3Filepath) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(inN3Filepath));
		int i = 0;
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.isEmpty())
				continue;
			i++;
			Inlink il = inlinkParser.parse(line);
			try
			{
				inlinkUpdateStatement.setInt(1, i);
				inlinkUpdateStatement.setString(2, il.toConcept);
				inlinkUpdateStatement.setString(3, il.fromConcept);
				inlinkUpdateStatement.setString(4, il.surface);
				inlinkUpdateStatement.executeUpdate();
			}
			catch (SQLException e)
			{
				System.err.println("insertion failed.");
				e.printStackTrace();
			}
		}
		br.close();
	}

	public void convertCommonness(String commonnessFilepath) throws NumberFormatException,
			IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(commonnessFilepath));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.isEmpty())
				continue;
			String parts[] = line.split("\t");
			if (parts.length != 3)
			{
				System.err.println("line parsing failed: " + line);
			}
			try
			{
				commonnessUpdateStatement.setString(1, parts[0]);
				commonnessUpdateStatement.setString(2, parts[1]);
				commonnessUpdateStatement.setDouble(3, Double.valueOf(parts[2]));
				commonnessUpdateStatement.executeUpdate();
			}
			catch (SQLException e)
			{
				System.err.println("insertion failed: " + line);
				e.printStackTrace();
			}
		}
		br.close();
	}

	public void convertKeyphraseness(String commonnessFilepath) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(commonnessFilepath));
		String line;
		while ((line = br.readLine()) != null)
		{
			if (line.isEmpty())
				continue;

			int k = line.indexOf(' ');
			if (k < 0)
			{
				System.err.println("line parsing failed: " + line);
			}

			String keyphrasenessString = line.substring(0, k);
			String phrase = line.substring(k + 1);
			try
			{
				keyphrasenessUpdateStatement.setString(1, phrase);
				keyphrasenessUpdateStatement.setDouble(2, Double.valueOf(keyphrasenessString));
				keyphrasenessUpdateStatement.executeUpdate();
			}
			catch (SQLException e)
			{
				System.err.println("insertion failed: " + line);
				e.printStackTrace();
			}
		}
		br.close();
	}

	public void createIndexes()
	{
		// executeSql("CREATE INDEX inlinks_toconcept_index ON inlinks (to_concept);");
		// executeSql("CREATE INDEX inlinks_fromconcept_index ON inlinks (from_concept);");
		executeSql("CREATE INDEX inlinks_surface_index ON inlinks (surface);");
		executeSql("CREATE INDEX commonness_surface_index ON commonness (surface);");
		executeSql("CREATE INDEX commonness_concept_index ON commonness (concept);");
		executeSql("CREATE INDEX keyphraseness_surface_index ON keyphraseness (surface);");
	}

	private Statement	st;

	private boolean executeSql(String sql)
	{
		if (st == null)
		{
			try
			{
				st = db.createStatement();
			}
			catch (SQLException e)
			{
				System.err.println("cannot create statement. message: " + e.getMessage());
				return false;
			}
		}
		
		try
		{
			System.out.println("executing: " + sql);
			return st.execute(sql);
		}
		catch (SQLException e)
		{
			System.err.println("cannot execute SQL statement: " + sql + ", message: " + e.getMessage());
			return false;
		}
	}

	public DatabaseConnector()
	{
		try
		{
			Class.forName("org.postgresql.Driver");

			String url = "jdbc:postgresql://achilles.cse.tamu.edu/wikiparsing";
			String username = "quyin";
			String password = "quyindbpwd";

			db = DriverManager.getConnection(url, username, password);

			inlinkUpdateStatement = db.prepareStatement("INSERT INTO inlinks VALUES (?, ?, ?, ?)");
			commonnessUpdateStatement = db.prepareStatement("INSERT INTO commonness VALUES (?, ?, ?)");
			keyphrasenessUpdateStatement = db.prepareStatement("INSERT INTO keyphraseness VALUES (?, ?)");
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("database driver not found.");
		}
		catch (SQLException e)
		{
			System.err.println("database connection failed.");
		}
	}

	public static void main(String[] args) throws IOException
	{
		DatabaseConnector dc = new DatabaseConnector();
		// dc.convertInlink("C:/run/sorted/sorted-inlinks.n3");
		// dc.convertCommonness("C:/run/commonness/commonness.tsv");
		// dc.convertKeyphraseness("S:/quyin/rada's data and processed/keyPhrasenessScore.2007");
		dc.createIndexes();
	}

}
