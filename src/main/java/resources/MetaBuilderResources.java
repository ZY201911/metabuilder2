package resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import metabuilder.MetaBuilder;

/**
 * A single access point for all application resources.
 * 
 * Although ResourceBundles are cached internally, this class defines
 * a singleton to make its nature as a single access point more 
 * explicit.
 * 
 * The instance is exported as a public field instead of a static method
 * to makes its more convenient and clear when statically imported.
 */
public final class MetaBuilderResources
{
	public static final MetaBuilderResources RESOURCES = new MetaBuilderResources();
	
	private static final String ERROR_MESSAGE = "[找不到资源]";
	
	private ResourceBundle aResouceBundle = ResourceBundle.getBundle(MetaBuilder.class.getName());
	
	private MetaBuilderResources() {}
	
	/**
	 * Returns a resource string for the key. If the key cannot be found or is not a string,
	 * returns a string that indicates an error.
	 * 
	 * @param pKey The key to look up. 
	 * @return The corresponding resource string.
	 * @pre pKey != null
	 */
	public String getString(String pKey)
	{
		assert pKey != null;
		try
		{
			return aResouceBundle.getString(pKey);
		}
		catch( MissingResourceException | ClassCastException exception )
		{
			return ERROR_MESSAGE;
		}
	}
	
	/**
	 * @param pKey The key to check. Cannot be null.
	 * @return True if the key is found in the resources.
	 */
	public boolean containsKey(String pKey)
	{
		assert pKey != null;
		return aResouceBundle.containsKey(pKey);
	}
}
