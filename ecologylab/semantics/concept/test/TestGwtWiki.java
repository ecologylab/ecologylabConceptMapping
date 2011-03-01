package ecologylab.semantics.concept.test;

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
		System.err.println("args: <wiki-markup-file-path>");
		
		String fp = args[0];
		String wikiMarkups = TextUtils.loadTxtAsString(fp);
		
		WikiModel wikiModel = new WikiModel("http://en.wikipedia.org/wiki/${image}", "http://en.wikipedia.org/wiki/${title}");
		String wikiHtml = wikiModel.render(wikiMarkups);
		
		int pDot = fp.lastIndexOf('.');
		String fname = pDot > 0 ? fp.substring(0, pDot) : fp;
		File ouf = new File(fname + ".html");
		FileWriter writer = new FileWriter(ouf);
		writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
		writer.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head><body>\n");
		writer.write(wikiHtml);
		writer.write("\n</body></html>\n");
		writer.close();
	}

}
