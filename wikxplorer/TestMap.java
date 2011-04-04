package wikxplorer;

import java.util.HashMap;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.simpl_inherit;

@simpl_inherit
public class TestMap extends ElementState
{

	@simpl_map("item")
	private HashMap<String, Integer>	map	= new HashMap<String, Integer>();

	public static void main(String[] args) throws SIMPLTranslationException
	{
		TestMap tm = new TestMap();
		tm.map.put("abc", 123);
		tm.serialize(System.out);
	}
}
