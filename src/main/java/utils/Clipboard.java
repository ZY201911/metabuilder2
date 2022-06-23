package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import diagram.Diagram;
import diagram.Element;
import diagram.Relationship;
import diagram.NamedElement;
import diagram.Package;
import diagram.PointElement;

/**
 * Stores a set of diagram elements for the purpose of pasting into a diagram.
 * 
 * Copying a list of elements into the clipboard results in a number of transformations
 * to the list and its elements to render the elements suitable for pasting:
 * - All elements are cloned
 * - Dangling Relationships are removed
 * - NamedElements requiring a missing parent are removed
 * - Dangling references to parents are removed
 * - The NamedElements are repositioned so that the top left coordinate of the set of elements
 *   is at the origin (0,0).
 *   
 * The list of elements stored into the clipboard is assumed to respect the non-redundancy 
 * constraint that no element whose deletion leads to the deletion of a NamedElement is selected with the NamedElement.
 * 
 * The clipboard is a singleton. This is necessary to allow copying elements
 * between diagrams of the same type.
 */
public final class Clipboard 
{
	private static final Clipboard INSTANCE = new Clipboard();
	
	private List<NamedElement> aNamedElements = new ArrayList<>();
	private List<Relationship> aRelationships = new ArrayList<>();

	/**
	 * Creates an empty clip-board.
	 */
	private Clipboard() 
	{}
	
	/**
	 * @return The Singleton instance of the Clipboard.
	 */
	public static Clipboard instance()
	{
		return INSTANCE;
	}
	
	/**
	 * Copies the elements in pSelection into the clip board.  
	 * The list of elements stored into the clipboard is assumed to 
	 * respect the non-redundancy constraint that no element whose 
	 * deletion leads to the deletion of a NamedElement is selected with the NamedElement.
	 * The transformation described in the class documentation are applied.
	 * 
	 * @param pSelection The elements to copy. Cannot be null.
	 */
	public void copy(Iterable<Element> pSelection)
	{
		assert pSelection != null;
		clear();
		aRelationships.addAll(copyRelationships(pSelection));
		aNamedElements.addAll(copyNamedElements(aRelationships, pSelection));
		removeDanglingRelationships();
		removeDanglingReferencesToParents();
	}
	
	/**
	 * @return A list of clones of the elements in this clipboard.
	 */
	public Iterable<Element> getElements()
	{
		List<Relationship> clonedRelationships = copyRelationships(new ArrayList<>(aRelationships));
		List<NamedElement> clonedNamedElements = copyNamedElements(clonedRelationships, new ArrayList<>(aNamedElements));
		List<Element> result = new ArrayList<>();
		result.addAll(clonedRelationships);
		result.addAll(clonedNamedElements);
		return result;
	}
	
	/*
	 * Empties the clipboard
	 */
	private void clear()
	{
		aNamedElements.clear();
		aRelationships.clear();
	}
	
	/*
	 * Makes a clone of every Relationships in pSelection and copies it into the clipboard	 
	 */
	private List<Relationship> copyRelationships(Iterable<Element> pSelection)
	{
		List<Relationship> result = new ArrayList<>();
		for( Element element : pSelection )
		{
			if( element instanceof Relationship )
			{	
				result.add((Relationship)element.clone());
			}
		}
		return result;
	}
	
	/*
	 * Makes a clone of every NamedElement in pSelection, copies it into the clipboard,
	 * and reassigns its Relationships
	 */
	private List<NamedElement> copyNamedElements(List<Relationship> pRelationships, Iterable<Element> pSelection)
	{
		List<NamedElement> result = new ArrayList<>();
		for( Element element : pSelection )
		{
			if( element instanceof NamedElement )
			{
//				if( missingParent( (NamedElement)element ))
//				{
//					continue;
//				}
				NamedElement cloned = (NamedElement) element.clone();
				result.add(cloned);
				reassignRelationships(pRelationships, (NamedElement)element, cloned);
			}
		}
		return result;
	}
	
	private void removeDanglingRelationships()
	{
		List<Relationship> toDelete = new ArrayList<>();
		for( Relationship Relationship : aRelationships )
		{
			if( !recursivelyContains(Relationship.getStart()) || !recursivelyContains(Relationship.getEnd()))
			{
				toDelete.add(Relationship);
			}
		}
		for( Relationship Relationship : toDelete )
		{
			aRelationships.remove(Relationship);
		}
	}
	
	private boolean recursivelyContains(NamedElement pNamedElement)
	{
		for( NamedElement NamedElement : aNamedElements )
		{
			if( NamedElement == pNamedElement )
			{
				return true;
			}
			else if( NamedElement instanceof Package ) {
				if( recursivelyContains(pNamedElement, ((Package)NamedElement).getChildren()) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean recursivelyContains(NamedElement pNamedElement, List<NamedElement> pNamedElements)
	{
		for( NamedElement NamedElement : pNamedElements )
		{
			if( NamedElement == pNamedElement )
			{
				return true;
			}
			else if( NamedElement instanceof Package ) {
				if( recursivelyContains(pNamedElement, ((Package)NamedElement).getChildren()) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private void reassignRelationships(List<Relationship> pRelationships, NamedElement pOld, NamedElement pNew)
	{
		for( Relationship Relationship : pRelationships )
		{
			if( Relationship.getStart() == pOld )
			{
				Relationship.connect(pNew, Relationship.getEnd(), Relationship.getDiagram());
			}
			if( Relationship.getEnd() == pOld)
			{
				Relationship.connect(Relationship.getStart(), pNew, Relationship.getDiagram());
			}
		}
		if( pOld instanceof Package ) {
			List<NamedElement> oldChildren = ((Package)pOld).getChildren();
			List<NamedElement> newChildren = ((Package)pNew).getChildren();
			for( int i = 0; i < oldChildren.size(); i++ )
			{
				reassignRelationships(pRelationships, oldChildren.get(i), newChildren.get(i));
			}
		}
	}
	
	/*
	 * Returns true if pNamedElement needs a parent that isn't in 
	 * the clipboard.
	 */
//	private boolean missingParent(NamedElement pNamedElement)
//	{
//		return pNamedElement.requiresParent() && !aNamedElements.contains(pNamedElement.getParent());
//	}
	
	/*
	 * Removes the reference to the parent of any NamedElement in the list.
	 * This operation is safe because NamedElements in the clip-board
	 * can only be pasted as root NamedElements. Children NamedElements would
	 * be copied through their parent.
	 */
	private void removeDanglingReferencesToParents()
	{
		for(NamedElement NamedElement : aNamedElements) {
			if(NamedElement.hasParent()) {
				NamedElement.unlink();
			}
		}
	}
	
	/**
	 * Returns true only of all the NamedElements and Relationships in the selection 
	 * are compatible with the type of the target diagram.
	 * 
	 * @param pDiagram The diagram to paste into.
	 * 
	 * @return True if and only if it is possible to paste the content
	 *     of the clipboard into pDiagram.
	 */
	public boolean validPaste(Diagram pDiagram)
	{
		for( Relationship Relationship : aRelationships )
		{
			if( !validElementFor(Relationship, pDiagram ))
			{
				return false;
			}
		}
		for( NamedElement NamedElement : aNamedElements )
		{
			if( !validElementFor(NamedElement, pDiagram ))
			{
				return false;
			}
		}
		return true;
	}
	
	private static boolean validElementFor( Element pElement, Diagram pDiagram )
	{
		// PointElements are allowed in all diagrams despite not being contained in prototypes.
//		if ( pElement.getClass() == PointElement.class ) 
//		{
//			return true;
//		}
//		return pDiagram.getPrototypes().stream()
//				.map(Object::getClass)
//				.anyMatch(Predicate.isEqual(pElement.getClass()));
		return true;
	}
}
