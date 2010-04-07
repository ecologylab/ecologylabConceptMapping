package ecologylab.semantics.conceptmapping.test;

import java.util.ArrayList;

import ecologylab.xml.ElementState;
import ecologylab.xml.XMLTranslationException;
import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;

@xml_inherit
@xml_tag("list")
public class ItemList extends ElementState
{
	@xml_nowrap
	@xml_collection
//  @xml_collection("item")
//  @xml_collection("item1")
  @xml_scope(ItemTScope.ITEM_TSCOPE)
//	@xml_classes({ItemList.class, Item.class, Item1.class, Item2.class})
	private ArrayList<Item>	list;

	public ArrayList<Item> getList()
	{
		return list;
	}
	
	public void addItem(Item it)
	{
		list.add(it);
	}

	public ItemList()
	{
		list = new ArrayList<Item>();
	}

	public static void main(String[] args) throws XMLTranslationException
	{
		ItemTScope.get();
		
		ItemList il0 = new ItemList();
		il0.addItem(new Item1());
		il0.addItem(new Item2());
		il0.addItem(new Item1());
		System.out.println(il0.translateToXML().toString());
		
		ItemList il = (ItemList) ItemList.translateFromXML(
				"ecologylab/semantics/conceptmapping/test/test.xml", ItemTScope.get());

		for (Item i : il.getList())
		{
			System.out.println(i.getClassName());
		}
	}
}
