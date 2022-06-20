/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import diagram.Diagram;
import diagram.Element;
import diagram.Relationship;
import diagram.NamedElement;
import diagram.Package;
import geom.Line;
import geom.Rectangle;
import viewers.relationships.RelationshipViewerRegistry;
import viewers.namedelements.NamedElementViewerRegistry;
import viewers.ViewerUtilities;

/**
 * Encapsulates all state related to the selection feature of a diagram canvas.
 * Conceptually, a selection model comprises three components:
 * 
 * 1. A list of selected elements.
 * 2. Optionally, a rubberband tool used to select two NamedElements for drawing an Relationship
 * 3. Optionally, a lasso tool used to select any element within a region on a canvas.
 */
public class SelectionModel implements Iterable<Element>
{
	private final SelectionObserver aObserver;
	
	private List<Element> aSelected = new ArrayList<>();
	private Optional<Line> aRubberband = Optional.empty();
	private Optional<Rectangle> aLasso = Optional.empty();
	
	/**
	 * Creates a new selection model with a single observer.
	 * 
	 * @param pObserver The observer for this model.
	 */
	public SelectionModel(SelectionObserver pObserver)
	{
		aObserver = pObserver;
	}
	
	/**
	 * Clears the selection model and selects all root NamedElements and 
	 * Relationships in the diagram. Triggers a notification.
	 * 
	 * @param pDiagram Provides the data for the selection.
	 * @pre pDiagram != null
	 */
	public void selectAll(Diagram pDiagram)
	{
		assert pDiagram != null;
		clearSelection();
		pDiagram.getNamedElements().forEach(this::internalAddToSelection);
		pDiagram.getRelationships().forEach(this::internalAddToSelection);
		aObserver.selectionModelChanged();
	}

	/**
	 * @return A rectangle that represents the bounding
	 *     box of the entire selection.
	 */
	public Rectangle getSelectionBounds()
	{
		Rectangle bounds = getLastSelectedBounds();
		for(Element selected : aSelected )
		{
			bounds = bounds.add(ViewerUtilities.getBounds(selected));
		}
		return bounds;
	}
	
	/*
	 * Returns a rectangle that represents the bounding box of the last selected element.
	 */
	private Rectangle getLastSelectedBounds() 
	{
		Optional<Element> lastSelected = getLastSelected();
		assert lastSelected.isPresent();
		return ViewerUtilities.getBounds(lastSelected.get());
	}
	
	/**
	 * @return A rectangle that represents the bounding box of the 
	 *     entire selection including the bounds of their parent NamedElements.
	 */
	public Rectangle getEntireSelectionBounds()
	{
		Rectangle bounds = getLastSelectedBounds();
		for(Element selected : aSelected )
		{
			bounds = addBounds(bounds, selected);
		}
		return bounds;
	}
	
	// Recursively enlarge the current rectangle to include the selected Elements
	private Rectangle addBounds(Rectangle pBounds, Element pSelected)
	{
		if( pSelected instanceof NamedElement && ((NamedElement) pSelected).hasParent())
		{
			return addBounds(pBounds, ((NamedElement) pSelected).getParent());
		}
		else
		{
			return pBounds.add(ViewerUtilities.getBounds(pSelected));
		}
	}
	
	/**
	 * @return An iterable of all selected NamedElements. This 
	 *     corresponds to the entire selection, except the Relationship.
	 */
	public List<NamedElement> getSelectedNamedElements()
	{
		List<NamedElement> result = new ArrayList<>();
		for( Element element : aSelected )
		{
			if( element instanceof NamedElement )
			{
				result.add((NamedElement) element);
			}
		}
		return result;
	}
	
	/**
	 * Records information about an active lasso selection tool, select all elements
	 * in the lasso, and triggers a notification.
	 * 
	 * @param pLasso The bounds of the current lasso.
	 * @param pDiagram Data about the diagram whose elements are being selected with the lasso.
	 *     only the elements in the lasso are selected.
	 * @pre pLasso != null;
	 * @pre pDiagram != null;
	 */
	public void activateLasso(Rectangle pLasso, Diagram pDiagram)
	{
		assert pLasso != null;
		aLasso = Optional.of(pLasso);
		pDiagram.getNamedElements().forEach( NamedElement -> selectNamedElement(NamedElement, pLasso));
		pDiagram.getRelationships().forEach( Relationship -> selectRelationship(Relationship, pLasso));
		aObserver.selectionModelChanged();
	}
	
	private void selectNamedElement(NamedElement pNamedElement, Rectangle pLasso)
	{
		if(pLasso.contains(NamedElementViewerRegistry.getBounds(pNamedElement)))
		{
			internalAddToSelection(pNamedElement);
		}
		if(pNamedElement instanceof Package) {
			((Package)pNamedElement).getChildren().forEach(child -> selectNamedElement(child, pLasso));
		}
	}
	
	private void selectRelationship(Relationship pRelationship, Rectangle pLasso )
	{
		if(pLasso.contains(RelationshipViewerRegistry.getBounds(pRelationship)))
		{
			internalAddToSelection(pRelationship);
		}		
	}
	
	/**
	 * @return The active lasso, if available.
	 */
	public Optional<Rectangle> getLasso()
	{
		return aLasso;
	}
	
	/**
	 * Removes the active lasso from the model and triggers a notification.
	 */
	public void deactivateLasso()
	{
		aLasso = Optional.empty();
		aObserver.selectionModelChanged();
	}
	
	/**
	 * Records information about an active rubberband selection tool and triggers a notification.
	 * @param pLine The line that represents the rubberband.
	 * @pre pLine != null;
	 */
	public void activateRubberband(Line pLine)
	{
		assert pLine != null;
		aRubberband = Optional.of(pLine);
		aObserver.selectionModelChanged();
	}
	
	
	/**
	 * @return The active rubberband, if available.
	 */
	public Optional<Line> getRubberband()
	{
		return aRubberband;
	}
	
	/**
	 * Removes the active rubberband from the model and triggers a notification.
	 */
	public void deactivateRubberband()
	{
		aRubberband = Optional.empty();
		aObserver.selectionModelChanged();
	}
	
	/**
	 * Clears any existing selection and initializes it with pNewSelection.
	 * Triggers a notification.
	 * 
	 * @param pNewSelection A list of elements to select.
	 * @pre pNewSelection != null;
	 */
	public void setSelectionTo(List<Element> pNewSelection)
	{
		assert pNewSelection != null;
		clearSelection();
		pNewSelection.forEach(this::internalAddToSelection);
		aObserver.selectionModelChanged();
	}
	
	/**
	 * Adds an element to the selection set and sets it as the last 
	 * selected element. If the element is already in the list, it
	 * is added to the end of the list. If the NamedElement is transitively 
	 * a child of any NamedElement in the list, it is not added.
	 * Triggers a notification.
	 * 
	 * @param pElement The element to add to the list.
	 * @pre pElement != null
	 */
	public void addToSelection(Element pElement)
	{
		assert pElement != null;
		internalAddToSelection(pElement);
		aObserver.selectionModelChanged();
	}
	
	private void internalAddToSelection(Element pElement)
	{
		if( !containsParent( pElement ))
		{
			aSelected.remove(pElement);
			aSelected.add(pElement);
			
			// Remove children in case a parent was added.
			ArrayList<Element> toRemove = new ArrayList<>();
			for( Element element : aSelected )
			{
				if( containsParent(element) )
				{
					toRemove.add(element);
				}
			}
			for( Element element : toRemove )
			{
				// Do no use removeFromSelection because it notifies the observer
				aSelected.remove(element); 
			}
		}
	}
	
	/*
	 * Returns true if any of the parents of pElement is contained
	 * (transitively).
	 * @param pElement The element to test
	 * @return true if any of the parents of pElement are included in the 
	 * selection.
	 */
	private boolean containsParent(Element pElement)
	{
		if( pElement instanceof NamedElement )
		{
			if( !((NamedElement) pElement).hasParent() )
			{
				return false;
			}
			else if( aSelected.contains(((NamedElement) pElement).getParent()))
			{
				return true;
			}
			else
			{
				return containsParent(((NamedElement) pElement).getParent());
			}
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Removes all selections and triggers a notification.
	 */
	public void clearSelection()
	{
		aSelected.clear();
		aObserver.selectionModelChanged();
	}
	
	/**
	 * @return The last element that was selected, if present.
	 */
	public Optional<Element> getLastSelected()
	{
		if( aSelected.isEmpty() )
		{
			return Optional.empty();
		}
		else
		{
			return Optional.of(aSelected.get(aSelected.size()-1));
		}
	}
	
	/**
	 * @param pElement The element to test.
	 * @return True if pElement is in the list of selected elements.
	 */
	public boolean contains(Element pElement)
	{
		return aSelected.contains(pElement);
	}
	
	/**
	 * Removes pElement from the list of selected elements,
	 * or does nothing if pElement is not selected.
	 * Triggers a notification.
	 * @param pElement The element to remove.
	 * @pre pElement != null;
	 */
	public void removeFromSelection(Element pElement)
	{
		assert pElement != null;
		aSelected.remove(pElement);
		aObserver.selectionModelChanged();
	}
	
	/**
	 * Sets pElement as the single selected element.
	 * Triggers a notification.
	 * @param pElement The element to set as selected.
	 * @pre pElement != null;
	 */
	public void set(Element pElement)
	{
		assert pElement != null;
		aSelected.clear();
		aSelected.add(pElement);
		aObserver.selectionModelChanged();
	}

	@Override
	public Iterator<Element> iterator()
	{
		return aSelected.iterator();
	}
	
	/**
	 * @return True if there is not element in the selection list.
	 */
	public boolean isEmpty()
	{
		return aSelected.isEmpty();
	}
}
