package ecologylab.semantics.concept.train;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecologylab.semantics.concept.database.DatabaseFacade;
import ecologylab.semantics.concept.detect.Concept;
import ecologylab.semantics.concept.detect.Doc;
import ecologylab.semantics.concept.detect.Instance;
import ecologylab.semantics.concept.detect.Surface;
import ecologylab.semantics.concept.detect.TrieDict;

public abstract class TrainingSetPreparer
{

	private TrieDict					dict;

	private PreparedStatement	pstLinkedConcepts;

	private PreparedStatement	pstWikiText;

	public TrainingSetPreparer(TrieDict dict) throws IOException, SQLException
	{
		this.dict = dict;
		pstLinkedConcepts = DatabaseFacade.get().getConnection()
				.prepareStatement("SELECT to_title, surface FROM wikilinks WHERE from_title=?;");
		pstWikiText = DatabaseFacade.get().getConnection()
				.prepareStatement("SELECT text FROM wikitexts WHERE title=?;");
	}

	/**
	 * prepare a training set using a given article title list. these articles are from wikipedia.
	 * 
	 * @param titleList
	 */
	public void prepareOnArticles(List<String> titleList)
	{
		for (String title : titleList)
		{
			try
			{
				prepareOnArticle(title);
			}
			catch (SQLException e)
			{
				System.err.println("error when preparing on article: " + title);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			reportArticle(title);
		}

		// close prepared statement(s)
		try
		{
			pstLinkedConcepts.close();
			pstWikiText.close();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * prepare a Doc object and call prepare() to generate training examples on that article.
	 * 
	 * @param title
	 * @throws SQLException
	 */
	private void prepareOnArticle(String title) throws SQLException
	{
		String text = getWikiText(title);
		if (text != null && text.length() > 0)
		{
			Doc doc = new Doc(title, text, dict);
			Map<Concept, Surface> linkedConcepts = getLinkedConceptsAndSurfaces(doc.getTitle());
			prepare(doc, linkedConcepts);
		}
	}

	abstract protected void prepare(Doc doc, Map<Concept, Surface> linkedConcepts) throws SQLException;

	private String getWikiText(String title) throws SQLException
	{
		String wikiText = "";

		pstWikiText.setString(1, title);
		ResultSet rs = pstWikiText.executeQuery();
		if (rs.next())
		{
			wikiText = rs.getString("text");
		}
		rs.close();

		return wikiText;
	}

	private Map<Concept, Surface> getLinkedConceptsAndSurfaces(String title) throws SQLException
	{
		Map<Concept, Surface> rst = new HashMap<Concept, Surface>();

		pstLinkedConcepts.setString(1, title);
		ResultSet rs = pstLinkedConcepts.executeQuery();
		while (rs.next())
		{
			String toTitle = rs.getString("to_title");
			String surface = rs.getString("surface");
			rst.put(Concept.get(toTitle), Surface.get(surface));
		}
		rs.close();

		return rst;
	}

	/**
	 * callback after an article is processed. override to customize.
	 * 
	 * @param title
	 */
	public void reportArticle(String title)
	{
		System.out.println(title);
	}

	/**
	 * callback after an instance is generated. override to customize.
	 * 
	 * @param inst
	 */
	public void reportInstance(Instance inst)
	{
		System.out.println(inst);
	}

}
