package diagram.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import diagram.Diagram;
import diagram.NamedElement;
import diagram.Composition;
import diagram.Association;
import diagram.Generalization;
import diagram.manager.constraints.ConstraintSet;
import diagram.manager.constraints.RelationshipConstraints;
import diagram.Package;
import geom.Point;
import viewers.namedelements.NamedElementViewerRegistry;

/**
* A builder for metamodel diagrams.
*/
public class DiagramBuilder extends AbstractDiagramBuilder
{
	private static final int PADDING = 10;
	private static final int TOP_HEIGHT = 20;
	private static final ConstraintSet CONSTRAINTS = new ConstraintSet(
			RelationshipConstraints.maxRelationships(1)
//			RelationshipConstraints.noSelfGeneralization(),
//			RelationshipConstraints.noDirectCycles(Generalization.class),
//			RelationshipConstraints.noDirectCycles(Composition.class),
//			RelationshipConstraints.noDirectCycles(Association.class)
	);

	/**
	 * Creates a new builder for metamodel diagrams.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.CLASS
	 */
	public DiagramBuilder( Diagram pDiagram )
	{
		super( pDiagram );
	}

	@Override
	public DiagramOperation createAddNamedElementOperation(NamedElement pNamedElement, Point pRequestedPosition)
	{
		DiagramOperation result = null;
		if( validChild(pNamedElement))
		{
			Optional<Package> container = findContainer(aDiagram.getNamedElements(), pRequestedPosition);
			if( container.isPresent() )
			{
				if( container.get().getChildren().size()==0 )
				{
					// If pNamedElement would be the first child NamedElement, position the NamedElement according to its container's position
					positionNamedElement(pNamedElement, new Point(container.get().getPosition().getX() + PADDING, 
							container.get().getPosition().getY() + PADDING + TOP_HEIGHT));
				}
				else 
				{
					positionNamedElement(pNamedElement, pRequestedPosition);
				}
				result = new SimpleOperation( ()->  
				{ 
					pNamedElement.attach(aDiagram);
					container.get().addChild(pNamedElement); 
				},
				()-> 
				{ 
					pNamedElement.detach();
					container.get().removeChild(pNamedElement); 
				});
			}
		}
		if( result == null )
		{
			result = super.createAddNamedElementOperation(pNamedElement, pRequestedPosition);
		}
		return result;
	}
	
	@Override
	protected ConstraintSet getRelationshipConstraints()
	{
		return CONSTRAINTS;
	}
	
	private static boolean validChild(NamedElement pPotentialChild)
	{
		return pPotentialChild instanceof NamedElement;
	}
	
	/* 
	 * Finds if the NamedElement to be added should be added to a package. Returns Optional.empty() if not. 
	 * If packages overlap, select the last one added, which by default should be on
	 * top. This could be fixed if we ever add a z coordinate to the diagram.
	 */
	private Optional<Package> findContainer( List<NamedElement> pNamedElements, Point pPoint)
	{
		Package container = null;
		for( NamedElement NamedElement : pNamedElements )
		{
			if( NamedElement instanceof Package && NamedElementViewerRegistry.contains(NamedElement, pPoint) )
			{
				container = (Package) NamedElement;
			}
		}
		if( container == null )
		{
			return Optional.empty();
		}
		List<NamedElement> children = new ArrayList<>(container.getChildren());
		if( children.size() == 0 )
		{
			return Optional.of(container);
		}
		else
		{
			Optional<Package> deeperContainer = findContainer( children, pPoint );
			if( deeperContainer.isPresent() )
			{
				return deeperContainer;
			}
			else 
			{
				return Optional.of(container);
			}
		}
	}

	/*
	 * Finds the package NamedElement under the position of the first NamedElement in pNamedElements.
	 * Returns Optional.empty() if there is no such package NamedElement for the NamedElements in pNamedElements to attach to,
	 * or the package NamedElement is already in the pNamedElements.
	 */
	private Optional<Package> findPackageToAttach(List<NamedElement> pNamedElements)
	{
		assert pNamedElements != null && pNamedElements.size() > 0;
		List<NamedElement> getNamedElements = new ArrayList<>(aDiagram.getNamedElements());
		Point requestedPosition = pNamedElements.get(0).getPosition();
		for( NamedElement pNamedElement: pNamedElements )
		{
			if(aDiagram.contains(pNamedElement))
			{
				getNamedElements.remove(pNamedElement);
			}
		}
		Optional<Package> Package = findContainer(getNamedElements, requestedPosition);
		if( !Package.isPresent() )
		{
			return Optional.empty();
		}
		// Returns Optional.empty() if the package NamedElement is in pNamedElements or contains any NamedElement in pNamedElements
		for(NamedElement pNamedElement: pNamedElements)
		{
			if( Package.get() == pNamedElement || Package.get().getChildren().contains(pNamedElement) )
			{
				return Optional.empty();
			}
		}
		return Package;
	}
	
	/*
	 * Returns true iff all the NamedElements in pNamedElements are linkable to a parent, or if the list is empty.
	 * @pre pNamedElements != null
	 */
	private static boolean allLinkable(List<NamedElement> pNamedElements)
	{
		assert pNamedElements != null;
		return pNamedElements.stream().allMatch(DiagramBuilder::isLinkable);
	}
	
	/*
	 * @param pNamedElement The NamedElement to check
	 * @return True if the NamedElement is a valid child that does not already
	 * have a parent.
	 * @pre pNamedElement != null
	 */
	private static boolean isLinkable(NamedElement pNamedElement)
	{
		return validChild(pNamedElement) && !pNamedElement.hasParent();
	}
	
	/*
	 * @param pNamedElement The NamedElement to check
	 * @return True if the NamedElement is a valid child that already
	 * has a parent.
	 * @pre pNamedElement != null
	 */
	private static boolean isUnlinkable(NamedElement pNamedElement)
	{
		// To be on the safe side we should technically check
		// that pNamedElement also does not require a parent, but since
		// currently all linkable NamedElements do not require a parent, skip.
		return pNamedElement.hasParent(); 
	}
	
	/*
	 * Returns true if all the NamedElements in pNamedElements are detachable
	 * @pre pNamedElements != null && pNamedElements.size() > 0
	 */
	private static boolean allUnlinkable(List<NamedElement> pNamedElements)
	{
		assert pNamedElements != null && pNamedElements.size() > 0;
		return pNamedElements.stream().allMatch(DiagramBuilder::isUnlinkable);
	}

	/*
	 * Finds the parent of all the NamedElements in pNamedElements. Returns Optional.empty() if the NamedElements have different parents.
	 */
	private static Optional<Package> findSharedParent(List<NamedElement> pNamedElements)
	{
		assert allUnlinkable(pNamedElements);
		// Get the parent of the first child NamedElement and check with other NamedElements
		Package parent = pNamedElements.get(0).getParent();
		for(NamedElement pNamedElement: pNamedElements)
		{
			if(parent != pNamedElement.getParent())
			{
				return Optional.empty();
			}
		}
		return Optional.of(parent);
	}
	
	/**
	 * Returns whether attaching the NamedElements in pNamedElements to the package NamedElement under the position
	 * of the first NamedElement in pNamedElements is a valid operation on the diagram.
	 * 
	 * @param pNamedElements The NamedElements to attach. 
	 * @return True if it is possible to attach pNamedElements to the package NamedElement.
	 * @pre pNamedElements != null;
	 */
	public boolean canLinkToPackage(List<NamedElement> pNamedElements)
	{
		assert pNamedElements!= null;
		if( pNamedElements.isEmpty() ) 
		{
			return false;
		}
		return allLinkable(pNamedElements) && findPackageToAttach(pNamedElements).isPresent();
	}
	
	/**
	 * Returns whether detaching the NamedElements in pNamedElements from the their parent is
	 * a valid operation on the diagram.
	 * 
	 * @param pNamedElements The NamedElements to detach.
	 * @return True if it is possible to detach pNamedElements from their parents, false otherwise
	 *     or if the list is empty.
	 * @pre pNamedElements != null;
	 */
	public boolean canUnlinkFromPackage(List<NamedElement>pNamedElements)
	{
		assert pNamedElements!= null;
		if( pNamedElements.isEmpty() )
		{
			return false;
		}
		return allUnlinkable(pNamedElements) && findSharedParent(pNamedElements).isPresent();
	}
	
	/**
	 * Creates an operation that attaches all the NamedElements in pNamedElements to the package NamedElement under 
	 * the position of the first NamedElement in pNamedElements.
	 * 
	 * @param pNamedElements The NamedElements to attach.
	 * @return The requested operation.
	 * @pre canAttachToPackage(pNamedElements);
	 */
	public DiagramOperation createLinkToPackageOperation(List<NamedElement>pNamedElements)
	{
		assert canLinkToPackage(pNamedElements);
		Package Package = findPackageToAttach(pNamedElements).get();
		return new SimpleOperation( 
				()-> 
				{
					for( NamedElement pNamedElement: pNamedElements )
					{
						aDiagram.removeNamedElement(pNamedElement);
						Package.addChild(pNamedElement);
						pNamedElement.link(Package);
					}
				},
				()->
				{
					for( NamedElement pNamedElement: pNamedElements )
					{
						aDiagram.addNamedElement(pNamedElement);
						Package.removeChild(pNamedElement);
					}
				});	
	}
	

	/**
	 * Creates an operation that detaches all the NamedElements in pNamedElements from their parent.
	 * 
	 * @param pNamedElements The NamedElements to detach.
	 * @return The requested operation.
	 * @pre canUnlinkFromPackage(pNamedElements);
	 */
	public DiagramOperation createUnlinkFromPackageOperation(List<NamedElement> pNamedElements)
	{
		assert canUnlinkFromPackage(pNamedElements);
		Package parent = findSharedParent(pNamedElements).get();
		// CSOFF:
		Package outerParent = parent.hasParent() ? parent.getParent() : null; //CSON:
		if( outerParent == null )
		{
			// The parent of the NamedElements in pNamedElements does not have parent, 
			// set the detached NamedElements as root NamedElements in the diagram
			return new SimpleOperation( ()-> 
					{
						for( NamedElement pNamedElement: pNamedElements )
						{
							aDiagram.addNamedElement(pNamedElement);
							parent.removeChild(pNamedElement);
						}
					},
					()->
					{
						for( NamedElement pNamedElement: pNamedElements )
						{
							aDiagram.removeNamedElement(pNamedElement);
							parent.addChild(pNamedElement);
						}
					});	
		}
		else 
		{
			// Attach the detached NamedElements to the parent of their current parent
			return new SimpleOperation( 
					()-> 
					{
						for( NamedElement pNamedElement: pNamedElements )
						{
							parent.removeChild(pNamedElement);
							outerParent.addChild(pNamedElement);
						}
					},
					()->
					{
						for( NamedElement pNamedElement: pNamedElements )
						{
							outerParent.removeChild(pNamedElement);
							parent.addChild(pNamedElement);
						}
					});	
		}
	}
}

