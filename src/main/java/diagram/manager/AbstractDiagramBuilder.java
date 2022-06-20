package diagram.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import diagram.Diagram;
import diagram.Element;
import diagram.Relationship;
import diagram.NamedElement;
import diagram.manager.constraints.ConstraintSet;
import diagram.Package;
import diagram.PointElement;
import geom.Dimension;
import geom.Point;
import geom.Rectangle;
import viewers.namedelements.NamedElementViewerRegistry;
import viewers.diagrams.DiagramViewer;
import viewers.namedelements.PackageViewer;

/**
* Wrapper around a Diagram that provides the logic for converting
* requests to creates or remove NamedElements and Relationships, and convert these
* requests into operation. An object of this class should perform
* read-only access to the diagram. However, executing the operations
* created by methods of this class will change the state of the 
* diagram.
*/
public abstract class AbstractDiagramBuilder
{
	// Arbitrary default value, used to simplify the testing code
	private static final int DEFAULT_DIMENSION = 1000;
	
	protected final Diagram aDiagram;
	private Dimension aCanvasDimension = new Dimension(DEFAULT_DIMENSION, DEFAULT_DIMENSION);
	
	/**
	 * Creates a builder for pDiagram.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null;
	 */
	protected AbstractDiagramBuilder( Diagram pDiagram )
	{
		assert pDiagram != null;
		aDiagram = pDiagram;
	}
	
	/**
	 * @return The diagram wrapped by this builder.
	 */
	public final Diagram getDiagram()
	{
		return aDiagram;
	}
	
	/**
	 * Provide information to this builder about the size
	 * of the canvas the diagram is built on.
	 * 
	 * @param pDimension The canvas size.
	 * @pre pDimension != null.
	 */
	public void setCanvasDimension(Dimension pDimension)
	{
		assert pDimension != null;
		aCanvasDimension = pDimension;
	}
	
	private static List<NamedElement> getNamedElementAndAllChildren(NamedElement pNamedElement)
	{
		List<NamedElement> result = new ArrayList<>();
		result.add(pNamedElement);
		if(pNamedElement instanceof Package) {
			((Package)pNamedElement).getChildren().forEach(NamedElement -> result.addAll(getNamedElementAndAllChildren(NamedElement)));
		}
		return result;
	}
	
	/**
	 * Returns whether adding pRelationship between pStart and pEnd
	 * is a valid operation on the diagram. 
	 * 
	 * @param pRelationship The requested Relationship
	 * @param pStart A requested start point
	 * @param pEnd A requested end point
	 * @return True if it's possible to add an Relationship of this type given the requested points.
	 * @pre pRelationship != null && pStart = null && pEnd != null
	 */
	public final boolean canAdd(Relationship pRelationship, Point pStart, Point pEnd)
	{
		assert pRelationship != null && pStart != null && pEnd != null;
		
		final DiagramViewer viewer = aDiagram.getDiagramViewer();
		Optional<NamedElement> startNamedElement = viewer.NamedElementAt(aDiagram, pStart);
		Optional<NamedElement> endNamedElement = viewer.NamedElementAt(aDiagram, pEnd);

		if(!startNamedElement.isPresent() || !endNamedElement.isPresent() )
		{
			return false;
		}
		

		return getRelationshipConstraints().satisfied(pRelationship, startNamedElement.get(), endNamedElement.get(), pStart, pEnd, aDiagram);
		
	}
	
	/**
	 * Returns whether adding pNamedElement at pRequestedPosition is a valid
	 * operation on the diagram. True by default. 
	 * Override to provide cases where this should be false.
	 * 
	 * @param pNamedElement The NamedElement to add if possible. 
	 * @param pRequestedPosition The requested getPosition for the NamedElement.
	 * @return True if it is possible to add pNamedElement at getPosition pRequestedPosition.
	 * @pre pNamedElement != null && pRequestedPosition != null
	 */
	public boolean canAdd(NamedElement pNamedElement, Point pRequestedPosition)
	{
		assert pNamedElement != null && pRequestedPosition != null;
		return true;
	}
	
	/**
	 * @return diagram type-specific constraints for adding Relationships.
	 */
	protected abstract ConstraintSet getRelationshipConstraints();
	
	/** 
	 * The default behavior is to getPosition the NamedElement so it entirely fits in the diagram, then 
	 * add it as a root NamedElement.
	 * @param pNamedElement The NamedElement to add.
	 * @param pRequestedPosition A point that is the requested getPosition of the NamedElement.
	 * @return The requested operation
	 * @pre pNamedElement != null && pRequestedPosition != null
	 * @pre canAdd(pNamedElement, pRequestedPosition)
	 */
	public DiagramOperation createAddNamedElementOperation(NamedElement pNamedElement, Point pRequestedPosition)
	{
		assert pNamedElement != null && pRequestedPosition != null;
		assert canAdd(pNamedElement, pRequestedPosition);
		positionNamedElement(pNamedElement, pRequestedPosition);
		return new SimpleOperation( ()-> aDiagram.addNamedElement(pNamedElement), 
				()-> aDiagram.removeNamedElement(pNamedElement));
	}
	
	/**
	 * Creates an operation that adds all the elements in pElements. Assumes all NamedElements
	 * are root NamedElements and all Relationships are connected, and that there are no dangling references.
	 * 
	 * @param pElements The elements to add.
	 * @return The requested operation
	 * @pre pElements != null
	 */
	public final DiagramOperation createAddElementsOperation(Iterable<Element> pElements)
	{
		CompoundOperation operation = new CompoundOperation();
		for( Element element : pElements)
		{
			if( element instanceof NamedElement )
			{
				operation.add(new SimpleOperation(
						()-> aDiagram.addNamedElement((NamedElement)element),
						()-> aDiagram.removeNamedElement((NamedElement)element)));
			}
			else if( element instanceof Relationship)
			{
				/* We need to re-connect the Relationship to set the correct value for the
				 * reference to the diagram, to cover the cases where elements might 
				 * be added by being copied from one diagram and pasted into another.
				 */
				operation.add(new SimpleOperation(
						()-> 
						{ 
							Relationship Relationship = (Relationship) element;
							aDiagram.addRelationship(Relationship); 
							Relationship.connect(Relationship.getStart(), Relationship.getEnd(), aDiagram);	
						},
						()-> aDiagram.removeRelationship((Relationship)element)));
			}
		}
		
		return operation;
	}
	
	/**
	 * Finds the elements that should be removed if pElement is removed,
	 * to preserve the integrity of the diagram.
	 * 
	 * @param pElement The element to remove.
	 * @return The list of elements that have to be removed with pElement.
	 * @pre pElement != null && aDiagram.contains(pElement);
	 */
	protected List<Element> getCoRemovals(Element pElement)
	{
		assert pElement != null && aDiagram.contains(pElement);
		ArrayList<Element> result = new ArrayList<>();
		result.add(pElement);
		if( pElement.getClass() == PointElement.class )
		{
			for( Relationship Relationship : aDiagram.RelationshipsConnectedTo((NamedElement)pElement))
			{
				result.add(Relationship);
			}
		}
		if( pElement instanceof NamedElement )
		{
			List<NamedElement> descendants = getNamedElementAndAllChildren((NamedElement)pElement);
			for(Relationship Relationship : aDiagram.getRelationships())
			{
				if(descendants.contains(Relationship.getStart() ) || descendants.contains(Relationship.getEnd()))
				{
					result.add(Relationship);
				}
			}
		}
		return result;
	}
	
	/*
	 * Organize the elements to delete so that they can be reinserted properly
	 */
	private List<Element> tweakOrder(Set<Element> pElements)
	{
		List<Element> result = new ArrayList<>();
		ArrayList<Element> result2 = new ArrayList<>();
		ArrayList<Relationship> Relationships = new ArrayList<>();
		ArrayList<NamedElement> NamedElements = new ArrayList<>();
		for( Element element : pElements )
		{
			result.add(element);
			if( element instanceof Relationship )
			{
				Relationships.add((Relationship)element);
			}
			else if( element instanceof NamedElement && ((NamedElement)element).hasParent() )
			{
				NamedElements.add((NamedElement)element);
			}
			else
			{
				result2.add(element);
			}
		}
		Collections.sort(Relationships, (pRelationship1, pRelationship2) -> aDiagram.indexOf(pRelationship2) - aDiagram.indexOf(pRelationship1));
		Collections.sort(NamedElements, new Comparator<NamedElement>() 
		{
			@Override
			public int compare(NamedElement pNamedElement1, NamedElement pNamedElement2)
			{
				NamedElement parent1 = pNamedElement1.getParent();
				NamedElement parent2 = pNamedElement2.getParent();
				if( parent1 == parent2 && parent1 instanceof Package && parent2 instanceof Package)
				{
					return ((Package)parent2).getChildren().indexOf(pNamedElement2) -  ((Package)parent1).getChildren().indexOf(pNamedElement1);
				}
				else 
				{
					return aDiagram.getNamedElements().indexOf(parent2) - aDiagram.getNamedElements().indexOf(parent1);
				}
			}
		});
		result2.addAll(Relationships);
		result2.addAll(NamedElements);
		return result2;
	}
	
	/**
	 * Creates an operation that removes all the elements in pElements.
	 * 
	 * @param pElements The elements to remove.
	 * @return The requested operation.
	 * @pre pElements != null.
	 */
	public final DiagramOperation createRemoveElementsOperation(Iterable<Element> pElements)
	{
		assert pElements != null;
		Set<Element> toDelete = new HashSet<>();
		for( Element element : pElements)
		{
			toDelete.addAll(getCoRemovals(element));
		}
		CompoundOperation result = new CompoundOperation();
		
		for( Element element : tweakOrder(toDelete))
		{
			if( element instanceof Relationship )
			{
				int index = aDiagram.indexOf((Relationship)element);
				result.add(new SimpleOperation(
						()-> aDiagram.removeRelationship((Relationship)element),
						()-> aDiagram.addRelationship(index, (Relationship)element)));
			}
			else if( element instanceof NamedElement )
			{
				if(((NamedElement) element).hasParent())
				{
					result.add(new SimpleOperation(
						createDetachOperation((NamedElement)element),
						createReinsertOperation((NamedElement)element)));
				}
				else
				{
					result.add(new SimpleOperation(
						()-> aDiagram.removeNamedElement((NamedElement)element),
						()-> aDiagram.addNamedElement((NamedElement)element)));
				}
			}
		}
		return result;
	}
	
	/**
	 * Create an operation to move a NamedElement.
	 * 
	 * @param pNamedElement The NamedElement to move.
	 * @param pX The amount to move the NamedElement in the x-coordinate.
	 * @param pY The amount to move the NamedElement in the y-coordinate.
	 * @return The requested operation.
	 * @pre pNamedElement != null.
	 */
	public final DiagramOperation createMoveNamedElementOperation(NamedElement pNamedElement, int pX, int pY)
	{
		return new SimpleOperation(
				()-> pNamedElement.translate(pX, pY),
				()-> pNamedElement.translate(-pX, -pY));
	}
	
	/**
	 * Create an operation to add and Relationship.
	 * 
	 * @param pRelationship The Relationship to add.
	 * @param pStart The starting point.
	 * @param pEnd The end point.
	 * @return The requested operation.
	 */
	public final DiagramOperation createAddRelationshipOperation(Relationship pRelationship, Point pStart, Point pEnd)
	{ 
		assert canAdd(pRelationship, pStart, pEnd);
		DiagramViewer viewer = aDiagram.getDiagramViewer();
		
		NamedElement NamedElement1 = viewer.NamedElementAt(aDiagram, pStart).get();
		Optional<NamedElement> NamedElement2in = viewer.NamedElementAt(aDiagram, pEnd);
		NamedElement NamedElement2 = null;
		if( NamedElement2in.isPresent() )
		{
			NamedElement2 = NamedElement2in.get();
		}
		CompoundOperation result = new CompoundOperation();
		assert NamedElement2 != null;
		completeRelationshipAdditionOperation(result, pRelationship, NamedElement1, NamedElement2, pStart, pEnd);
		return result;
	}
	
	/**
	 * Finishes the addition operation. By default, this just connects the Relationship to the NamedElements
	 * and adds the Relationship to the diagram.
	 * 
	 * @param pOperation The operation being constructed. 
	 * @param pRelationship The Relationship to add.
	 * @param pStartNamedElement The start NamedElement.
	 * @param pEndNamedElement The end NamedElement.
	 * @param pStartPoint The start point.
	 * @param pEndPoint The end point.
	 * @pre No null references as arguments.
	 */
	protected void completeRelationshipAdditionOperation( CompoundOperation pOperation, Relationship pRelationship, NamedElement pStartNamedElement, NamedElement pEndNamedElement,
			Point pStartPoint, Point pEndPoint)
	{
		pRelationship.connect(pStartNamedElement, pEndNamedElement, aDiagram);
		pOperation.add(new SimpleOperation(()-> aDiagram.addRelationship(pRelationship),
				()-> aDiagram.removeRelationship(pRelationship)));
	}
	
	private Runnable createReinsertOperation(NamedElement pNamedElement)
	{
		Package parent = pNamedElement.getParent();
		int index = parent.getChildren().indexOf(pNamedElement);
		return ()-> 
		{
			parent.addChild(index, pNamedElement);
			pNamedElement.attach(aDiagram);
		};
	}
	
	private Runnable createDetachOperation(NamedElement pNamedElement)
	{
		Package parent = pNamedElement.getParent();
		if(parent.getClass()==Package.class && parent.getChildren().size()==1)
		{
			return ()-> 
			{ 
				Rectangle parentBound = new PackageViewer().getBounds(parent);
				pNamedElement.detach(); 
				parent.removeChild(pNamedElement); 
				parent.translate( parentBound.getX()-parent.getPosition().getX(),  parentBound.getY()-parent.getPosition().getY() );
			};
		}
		return ()-> 
		{ 
			pNamedElement.detach(); 
			parent.removeChild(pNamedElement); 
		};
	}
	
	private Point computePosition(Rectangle pBounds, Point pRequestedPosition)
	{
		int newX = pRequestedPosition.getX();
		int newY = pRequestedPosition.getY();
		if(newX + pBounds.getWidth() > aCanvasDimension.width())
		{
			newX = aCanvasDimension.width() - pBounds.getWidth();
		}
		if (newY + pBounds.getHeight() > aCanvasDimension.height())
		{
			newY = aCanvasDimension.height() - pBounds.getHeight();
		}
		return new Point(newX, newY);
	}
	
	/**
	 * Positions pNamedElement as close to the requested getPosition as possible.
	 * 
	 * @param pNamedElement The NamedElement to getPosition. 
	 * @param pRequestedPosition The requested getPosition.
	 * @pre pNamedElement != null && pRequestedPosition != null
	 */
	protected void positionNamedElement(NamedElement pNamedElement, Point pRequestedPosition)
	{
		assert pNamedElement != null && pRequestedPosition != null;
		Rectangle bounds = NamedElementViewerRegistry.getBounds(pNamedElement);
		Point getPosition = computePosition(bounds, pRequestedPosition);
		pNamedElement.translate(getPosition.getX() - bounds.getX(), getPosition.getY() - bounds.getY());
	}
}

