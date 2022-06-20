package diagram;

import static resources.MetaBuilderResources.RESOURCES;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Prototype objects for creating diagram elements.
 */
public final class Prototypes
{   // CSOFF:
	private static final Prototypes INSTANCE = new Prototypes();
	private final Map<Element, String> aKeys = new IdentityHashMap<>();
	
	public static final Element CLASS = create(new BClass(false, false), "class");
	public static final Element ABSTRACTCLASS = create(new BClass(true, false), "abstractclass");
	public static final Element INTERFACE = create(new BClass(false, true), "interface");
	public static final Element PACKAGE = create(new Package(), "package");
	public static final Element DATATYPE = create(new DataType(), "datatype");
	public static final Element ENUMERATION = create(new Enumeration(), "enumeration");
	public static final Element GENERALIZATION = create(new Generalization(), "generalization");
	public static final Element ASSOCIATION = create(new Association(), "association");
	public static final Element COMPOSITION = create(new Composition(), "composition");

	private Prototypes() {}
	
	/**
	 * @return The singleton instance of this class.
	 */
	public static Prototypes instance()
	{
		return INSTANCE;
	}
	
	private static Element create(Element pElement, String pKey)
	{
		INSTANCE.aKeys.put(pElement, pKey);
		return pElement;
	}
	
	/**
	 * @param pPrototype The requested prototype
	 * @param pVerbose true if we want the verbose version of this tooltip.
	 * @return The tooltip associated with this prototype.
	 * @pre pPrototype != null
	 */
	public String tooltip(Element pPrototype, boolean pVerbose)
	{
		if( !aKeys.containsKey(pPrototype))
		{
			return "[tooltip not found]";
		}
		String basicKey = aKeys.get(pPrototype) + ".tooltip";
		String verboseKey = basicKey + ".verbose";
		if( pVerbose && RESOURCES.containsKey(verboseKey))
		{
			return RESOURCES.getString(basicKey) + ": " + RESOURCES.getString(verboseKey);
		}
		else
		{
			return RESOURCES.getString(basicKey);
		}
	}
}
