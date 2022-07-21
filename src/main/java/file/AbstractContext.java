package file;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import diagram.Diagram;
import diagram.NamedElement;

/**
 * Base class for serialization and deserialization contexts. A context 
 * is a mapping between nodes and arbitrary identifiers. The only constraint
 * on identifiers is that they consistently preserve mapping between objects and
 * their identity.
 */
public abstract class AbstractContext implements Iterable<NamedElement>
{
	protected final Map<NamedElement, Integer> aNamedElements = new HashMap<>();
	private final Diagram aDiagram;
	
	/**
	 * Initializes the context with a diagram.
	 * 
	 * @param pDiagram The diagram that corresponds to the context.
	 * @pre pDiagram != null.
	 */
	protected AbstractContext(Diagram pDiagram)
	{
		assert pDiagram != null;
		aDiagram = pDiagram;
	}
	
	/**
	 * @return The diagram associated with this context. Never null.
	 */
	public Diagram pDiagram()
	{
		return aDiagram;
	}
	
	/**
	 * @param pNamedElement The node to check.
	 * @return The id for the node.
	 * @pre pNamedElement != null
	 * @pre pNamedElement is in the map.
	 */
	public int getId(NamedElement pNamedElement)
	{
		assert pNamedElement != null;
		assert aNamedElements.containsKey(pNamedElement);
		return aNamedElements.get(pNamedElement);
	}
	
	@Override
	public Iterator<NamedElement> iterator()
	{
		return aNamedElements.keySet().iterator();
	}
}
