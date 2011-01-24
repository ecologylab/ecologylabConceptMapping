package ecologylab.semantics.concept.wikiparser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ecologylab.semantics.concept.utils.TextUtils;
import info.bliki.wiki.model.WikiModel;

public class TestGwtWiki
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		WikiModel wikiModel = new WikiModel("http://en.wikipedia.org/wiki/${image}", "http://en.wikipedia.org/wiki/${title}");
		
		String usaWiki = TextUtils.loadTxtAsString("usa.wiki");
		String usaHtml = wikiModel.render(usaWiki);
		File ouf = new File("usa.html");
		FileWriter writer = new FileWriter(ouf);
		writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
		writer.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head><body>\n");
		writer.write(usaHtml);
		writer.write("\n</body></html>\n");
		writer.close();
	}

}
