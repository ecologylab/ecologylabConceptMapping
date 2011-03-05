package ecologylab.semantics.concept.wikiparsing;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ecologylab.semantics.concept.service.Configs;

/**
 * SAX handler for handling Wikipedia dump XML files.
 * 
 * @author quyin
 * 
 */
public class WikiParsingSAXHandler extends DefaultHandler
{

	private WikiConceptHandler	conceptHandler;

	public WikiParsingSAXHandler() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		this.conceptHandler = (WikiConceptHandler) Configs.getObject("prep.wiki_concept_handler",
				WikiConceptHandlerInitial.class);
	}

	private Stack<String>	parentTags	= new Stack<String>();

	private StringBuilder	currentText	= new StringBuilder();

	private String				wikiTitle;

	private int						wikiId;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		currentText.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException
	{
		parentTags.push(qName);
		currentText.delete(0, currentText.length());
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		parentTags.pop();

		String parentTag = parentTags.peek();
		if (parentTag.equals("page") && qName.equals("title"))
		{
			wikiTitle = currentText.toString();
		}
		else if (parentTag.equals("page") && qName.equals("id"))
		{
			wikiId = Integer.parseInt(currentText.toString());
		}
		else if (parentTag.equals("revision") && qName.equals("text"))
		{
			conceptHandler.handle(wikiId, wikiTitle, currentText.toString());
			tick(wikiTitle);
		}
	}

	@Override
	public void endDocument()
	{
		conceptHandler.finish();
	}

	/**
	 * for counter.
	 */
	protected void tick(String title)
	{

	}

}
