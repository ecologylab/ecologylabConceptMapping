package ecologylab.semantics.concept.service;

import java.io.File;

import ecologylab.appframework.types.prefs.Pref;
import ecologylab.appframework.types.prefs.PrefSet;
import ecologylab.appframework.types.prefs.PrefSetBaseClassProvider;
import ecologylab.generic.Debug;
import ecologylab.serialization.ElementState;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.TranslationScope;

public class Configs extends Debug
{

	private static PrefSet	configs;

	static
	{
		TranslationScope translationScope = TranslationScope.get(
				PrefSet.PREFS_TRANSLATION_SCOPE,
				PrefSetBaseClassProvider.STATIC_INSTANCE.provideClasses()
				);
		try
		{
			configs = PrefSet.load("concept_mapping.xml", translationScope);
			configs.serialize(System.out, ElementState.FORMAT.XML);
		}
		catch (SIMPLTranslationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getString(String name)
	{
		return Pref.lookupString(name);
	}

	public static int getInt(String name)
	{
		return Pref.lookupInt(name);
	}

	public static double getDouble(String name)
	{
		return Pref.lookupDouble(name);
	}

	public static File getFile(String name)
	{
		return Pref.lookupFile(name);
	}

	public static Object getObject(String name, Class defaultClass) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException
	{
		Class clazz = defaultClass;
		String className = getString(name);
		if (className != null)
			clazz = Class.forName(className);
		return clazz.newInstance();
	}

}
