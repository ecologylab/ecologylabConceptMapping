package ecologylab.semantics.concept.wikiparsing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ecologylab.semantics.actions.ParseDocumentSemanticAction;
import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.serialization.ElementState.xml_tag;
import ecologylab.serialization.simpl_inherit;

@simpl_inherit
@xml_tag("parse_document")
public class LinkHandler1 extends ParseDocumentSemanticAction<WikiInfoCollector, SemanticActionHandler>
{

	@Override
	public Object perform(Object obj)
	{
		String title = (String) semanticActionHandler.getSemanticActionVariableMap().get("title");
		String surface = (String) getArgumentObject("surface");
		String target = (String) getArgumentObject("target_title");
		
		debug("link detected: " + title + " -> " + target + " : " + surface);
		
		if (title != null && surface != null && target != null)
		{
			String trueTarget = getRedirectedTitle(target);
			saveWikilink(title, surface, trueTarget);
		}
		return null;
	}

	private synchronized String getRedirectedTitle(String target)
	{
		PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
				"SELECT to_title FROM redirects WHERE from_title=?;"
				);
		try
		{
			ps.setString(1, target);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				return rs.getString("to_title");
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return target;
	}

	private synchronized void saveWikilink(String title, String surface, String trueTarget)
	{
		PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
				"INSERT INTO wikilinks VALUES (?, ?, ?);"
				);
		try
		{
			ps.setString(1, title);
			ps.setString(2, trueTarget);
			ps.setString(3, surface);
			ps.execute();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
