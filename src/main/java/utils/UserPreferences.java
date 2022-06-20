package utils;

import static viewers.FontMetrics.DEFAULT_FONT_SIZE;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.prefs.Preferences;

import metabuilder.MetaBuilder;

/**
 * A Singleton that manages all user preferences global to
 * the application.
 */
public final class UserPreferences
{
	/**
	 * A boolean preference.
	 */
	public enum booleanPreference
	{	
		showGrid(true), showToolHints(false), autoEditNamedElement(false), verboseToolTips(false),
		showTips(true);
		
		private boolean aDefault;
		
		booleanPreference( boolean pDefault )
		{ 
			aDefault = pDefault;
		}
		
		String getDefault()
		{
			return Boolean.toString(aDefault);
		}
	}
	
	/**
	 * An integer preference.
	 */
	public enum IntegerPreference
	{
		diagramWidth(0), diagramHeight(0), nextTipId(1), fontSize(DEFAULT_FONT_SIZE);
		
		private int aDefault;
		
		IntegerPreference( int pDefault )
		{
			aDefault = pDefault;
		}
		
		String getDefault()
		{
			return Integer.toString(aDefault);
		}
	}
	
	/**
	 * An object that can react to a change to a boolean user preference.
	 */
	public interface booleanPreferenceChangeHandler
	{
		/**
		 * Callback for change in boolean preference values.
		 * 
		 * @param pPreference The preference that just changed.
		 */
		void booleanPreferenceChanged(booleanPreference pPreference);
	}

	/**
	 * An object that can react to a change to an integer user preference.
	 */
	public interface IntegerPreferenceChangeHandler
	{
		/**
		 * Callback for change in integer preference values.
		 * 
		 * @param pPreference The preference that just changed.
		 */
		void integerPreferenceChanged(IntegerPreference pPreference);
	}
	
	private static final UserPreferences INSTANCE = new UserPreferences();
	
	private EnumMap<booleanPreference, Boolean> aBooleanPreferences = new EnumMap<>(booleanPreference.class);
	private final List<booleanPreferenceChangeHandler> aBooleanPreferenceChangeHandlers = new ArrayList<>();
	private EnumMap<IntegerPreference, Integer> aIntegerPreferences = new EnumMap<>(IntegerPreference.class);
	private final List<IntegerPreferenceChangeHandler> aIntegerPreferenceChangeHandlers = new ArrayList<>();
	
	private UserPreferences()
	{
		for( booleanPreference preference : booleanPreference.values() )
		{
			aBooleanPreferences.put(preference, 
					Boolean.valueOf(Preferences.userNodeForPackage(MetaBuilder.class)
							.get(preference.name(), preference.getDefault())));
		}
		for( IntegerPreference preference : IntegerPreference.values() )
		{
			aIntegerPreferences.put( preference, 
					Integer.valueOf(Preferences.userNodeForPackage(MetaBuilder.class)
							.get(preference.name(), preference.getDefault())));
		}
	}
	
	public static UserPreferences instance() 
	{ return INSTANCE; }
	
	/**
	 * @param pPreference The property whose value to obtain.
	 * @return The value of the property.
	 */
	public boolean getboolean(booleanPreference pPreference)
	{
		return aBooleanPreferences.get(pPreference);
	}
	
	/**
	 * @param pPreference The property whose value to obtain.
	 * @return The value of the property.
	 */
	public int getInteger(IntegerPreference pPreference)
	{
		return aIntegerPreferences.get(pPreference);
	}
	
	/**
	 * Sets and persists the value of a preference.
	 * 
	 * @param pPreference The property to set.
	 * @param pValue The value to set.
	 */
	public void setboolean(booleanPreference pPreference, boolean pValue)
	{
		aBooleanPreferences.put(pPreference, pValue);
		Preferences.userNodeForPackage(MetaBuilder.class).put(pPreference.name(), Boolean.toString(pValue));
		aBooleanPreferenceChangeHandlers.forEach(handler -> handler.booleanPreferenceChanged(pPreference));
	}
	
	/**
	 * Sets and persists the value of a preference.
	 * 
	 * @param pPreference The property to set.
	 * @param pValue The value to set.
	 */
	public void setInteger(IntegerPreference pPreference, int pValue)
	{
		aIntegerPreferences.put(pPreference, pValue);
		Preferences.userNodeForPackage(MetaBuilder.class).put(pPreference.name(), Integer.toString(pValue));
		aIntegerPreferenceChangeHandlers.forEach(handler -> handler.integerPreferenceChanged(pPreference));
	}
	
	/**
	 * Adds a handler for a boolean property change. Don't forget to remove handers if 
	 * objects are removed, e.g., diagram Tabs.
	 * 
	 * @param pHandler A handler for a change in boolean preferences.
	 */
	public void addbooleanPreferenceChangeHandler(booleanPreferenceChangeHandler pHandler)
	{
		aBooleanPreferenceChangeHandlers.add(pHandler);
	}
	
	/**
	 * Removes a handler.
	 * 
	 * @param pHandler The handler to remove.
	 */
	public void removebooleanPreferenceChangeHandler(booleanPreferenceChangeHandler pHandler)
	{
		aBooleanPreferenceChangeHandlers.remove(pHandler);
	}
	
	/**
	 * Adds a handler for an integer property change. Don't forget to remove handers if 
	 * objects are removed, e.g., diagram Tabs.
	 * 
	 * @param pHandler A handler for a change in integer preferences.
	 */
	public void addIntegerPreferenceChangeHandler(IntegerPreferenceChangeHandler pHandler)
	{
		aIntegerPreferenceChangeHandlers.add(pHandler);
	}

	/**
	 * Removes a handler.
	 * 
	 * @param pHandler The handler to remove.
	 */
	public void removeIntegerPreferenceChangeHandler(IntegerPreferenceChangeHandler pHandler)
	{
		aIntegerPreferenceChangeHandlers.remove(pHandler);
	}
}
