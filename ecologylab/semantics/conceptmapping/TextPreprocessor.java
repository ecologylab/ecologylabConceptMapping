package ecologylab.semantics.conceptmapping;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecologylab.semantics.model.text.PorterStemmer;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class TextPreprocessor extends MaxentTagger
{
	public final static Pattern			pattern									= Pattern.compile("[^a-zA-Z0-9]+");

	public final static String			default_model_file_path	= "../stanford-postagger-2009-12-24/models/bidirectional-distsim-wsj-0-18.tagger";

	public static TextPreprocessor	default_tagger;

	static
	{
		try
		{
			default_tagger = new TextPreprocessor(default_model_file_path);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String filter(String s)
	{
		Matcher matcher = pattern.matcher(s);
		String result = matcher.replaceAll("");
		return result;
	}

	public static void main(String[] args)
	{
		String line = "West End theatre is a popular term for mainstream professional theatre staged in the large theatres of London's \"Theatreland\".[1]  Along with New York's Broadway theatre, West End theatre is usually considered to represent the highest level of commercial theatre in the English speaking world. Seeing a West End show is a common tourist activity in London.[1]\nTotal attendances first surpassed 12 million in 2002, and in June 2005 The Times reported that this record might be beaten in 2005. Total attendance numbers surpassed 13 million in 2007,[2] setting a new record for the West End. Factors behind high ticket sales in the first half of 2005 included new hit musicals such as Billy Elliot, The Producers and Mary Poppins and the high number of film stars appearing. Since the late 1990s there has been an increase in the number of American screen actors on the London stage, and in 2005 these included Brooke Shields, Val Kilmer, Rob Lowe, David Schwimmer and Kevin Spacey.";
		List<Token> tokens;
		try
		{
			tokens = preprocess(line, true);
			System.out.println(tokens);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Token> preprocess(String text, boolean isPosTagNeeded) throws Exception
	{
		StringReader sr = new StringReader(text);
		List<Sentence<? extends HasWord>> sentences = tokenizeText(sr);
		List<Sentence<TaggedWord>> taggings = null;
		if (isPosTagNeeded)
			taggings = default_tagger.process(sentences);
		List<Token> tokens = new ArrayList<Token>();

		int stn_size = sentences.size();
		for (int i = 0; i < stn_size; ++i)
		{
			Sentence stn = sentences.get(i);
			Sentence tagging = null;
			if (isPosTagNeeded)
				tagging = taggings.get(i);

			int word_size = stn.size();
			for (int j = 0; j < word_size; ++j)
			{
				CoreLabel label = (CoreLabel) stn.get(j);
				TaggedWord tag = null;
				if (isPosTagNeeded)
					tag = (TaggedWord) tagging.get(j);

				Token tk = new Token();
				tk.surface = label.get(CoreAnnotations.WordAnnotation.class);
				tk.normForm = stem(filter(tk.surface.toLowerCase()));
				if (tk.normForm != null && tk.normForm != "")
				{
					tk.context = text;
					tk.offsetBegin = label.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
					tk.offsetEnd = label.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
					tk.posTag = "";
					if (isPosTagNeeded)
						tk.posTag = tag.tag();
					tokens.add(tk);
				}
			}
		}

		return tokens;
	}

	public static String stem(String s)
	{

		PorterStemmer porter = new PorterStemmer();
		porter.add(s.toCharArray(), s.length());
		porter.stem();
		return porter.toString();
	}

	public static List<Sentence<? extends HasWord>> tokenizeText(Reader r)
	{
		return tokenizeText(r, PTBTokenizer.PTBTokenizerFactory.newPTBTokenizerFactory(false, true));
	}

	public TextPreprocessor(String modelFile) throws Exception
	{
		super(modelFile);
		// TODO Auto-generated constructor stub
	}
}
