package ecologylab.semantics.conceptmapping.test;

import ecologylab.xml.TranslationScope;

public class ItemTScope
{
	public static final String	ITEM_TSCOPE	= "item_tscope";

	static final Class[]				CLASSES			=
																	{ ItemList.class, Item.class, Item1.class, Item2.class };

	public static TranslationScope get()
	{
		return TranslationScope.get(ITEM_TSCOPE, CLASSES);
	}
}
