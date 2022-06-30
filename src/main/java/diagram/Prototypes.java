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

	private Prototypes() {}
	
	/**
	 * @return The singleton instance of this class.
	 */
	public static Prototypes instance()
	{
		return INSTANCE;
	}
	
	/**
	 * @param pPrototype The requested prototype
	 * @param pVerbose true if we want the verbose version of this tooltip.
	 * @return The tooltip associated with this prototype.
	 * @pre pPrototype != null
	 */
	public String tooltip(Element pPrototype, boolean pVerbose)
	{
		String typeString = "";
		if(pPrototype instanceof BClass && !((BClass) pPrototype).getIsAbstract() && !((BClass) pPrototype).getIsInterface()) {
			typeString = "class";
		}
		else if(pPrototype instanceof BClass && ((BClass) pPrototype).getIsAbstract() && !((BClass) pPrototype).getIsInterface()) {
			typeString = "abstractclass";
		}
		else if(pPrototype instanceof BClass && !((BClass) pPrototype).getIsAbstract() && ((BClass) pPrototype).getIsInterface()) {
			typeString = "interface";
		}
		else if(pPrototype instanceof Package) {
			typeString = "package";
		}
		else if(pPrototype instanceof Enumeration) {
			typeString = "enumeration";
		}
		else if(pPrototype instanceof DataType) {
			typeString = "datatype";
		}
		else if(pPrototype instanceof Association) {
			typeString = "association";
		}
		else if(pPrototype instanceof Generalization) {
			typeString = "generalization";
		}
		else if(pPrototype instanceof Composition) {
			typeString = "composition";
		}
		else {
			return "[tooltip not found]";
		}
		String basicKey = typeString + ".tooltip";
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
