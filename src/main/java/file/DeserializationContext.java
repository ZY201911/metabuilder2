package file;

import diagram.Diagram;
import diagram.NamedElement;

/**
 * A deserialization context allows clients to incrementally build
 * up the context. The identifiers that correspond to objects must be 
 * specified explicitly. 
 */
public class DeserializationContext extends AbstractContext
{
	/**
	 * Initializes an empty context and associates it with
	 * pDiagram.
	 * 
	 * @param pDiagram The diagram associated with the context.
	 * @pre pDiagram != null.
	 */
	public DeserializationContext(Diagram pDiagram)
	{
		super( pDiagram );
	}
	
	/**
	 * Attach all deserialized nodes to their diagram.
	 */
	public void attachNamedElements()
	{
		for( NamedElement node : this )
		{
			node.attach(pDiagram());
		}
	}
	
	/**
	 * Adds a node to the context.
	 * 
	 * @param pNamedElement The node to add.
	 * @param pId The id to associated with this node.
	 * @pre pNamedElement != null;
	 */
	public void addNamedElement(NamedElement pNamedElement, int pId)
	{
		assert pNamedElement != null;
		aNamedElements.put(pNamedElement, pId);
	}
	
	/**
	 * @param pId The identifier to search for.
	 * @return The node associated with this identifier.
	 * @pre pId exists as a value.
	 */
	public NamedElement getNamedElement(int pId)
	{
		for( NamedElement node : this )
		{
			if( aNamedElements.get(node) == pId )
			{
				return node;
			}
		}
		assert false;
		return null;
	}
}
