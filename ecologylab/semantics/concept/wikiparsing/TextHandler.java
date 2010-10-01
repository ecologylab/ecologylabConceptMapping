package ecologylab.semantics.concept.wikiparsing;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import ecologylab.semantics.actions.CreateAndVisualizeTextSurrogateSemanticAction;
import ecologylab.semantics.concept.database.DatabaseAdapter;
import ecologylab.semantics.concept.utils.TextUtils;
import ecologylab.semantics.generated.library.Paragraph;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit
@xml_tag("create_and_visualize_text_surrogate")
public class TextHandler extends CreateAndVisualizeTextSurrogateSemanticAction
{

	@Override
	public Object perform(Object obj)
	{
		String title = (String) getArgumentObject("title");
		Collection<Paragraph> paragraphs = (Collection<Paragraph>) getArgumentObject("wiki_text");

		StringBuilder textBuilder = new StringBuilder();
		for (Paragraph p : paragraphs)
		{
			String wikiText = p.getParagraphText();
			textBuilder.append(TextUtils.removeWikiMessages(wikiText));
			textBuilder.append("\n");
		}
		String text = textBuilder.toString();

		saveWikitext(title, text);

		return null;
	}

	private void saveWikitext(String title, String text)
	{
		PreparedStatement ps = DatabaseAdapter.get().getPreparedStatement(
				"INSERT INTO wikitexts VALUES (?,?);");
		try
		{
			ps.setString(1, title);
			ps.setString(2, text);
			ps.execute();
		}
		catch (SQLException e)
		{
			warning(String.format("error when saving wikitext for %s, error message: %s", title,
					e.getMessage()));
		}
	}

}
