package utils;

import java.util.ArrayList;
import java.util.List;

import diagram.Element;
import diagram.NamedElement;
import diagram.manager.CompoundOperation;
import diagram.manager.DiagramBuilder;
import geom.Rectangle;
import viewers.namedelements.NamedElementViewerRegistry;

/**
 * Tracks the movement of a set of selected diagram elements.
 */
public class MoveTracker
{
	private List<NamedElement> aTrackedNamedElements = new ArrayList<>();
	private List<Rectangle> aOriginalBounds = new ArrayList<>();

	/**
	 * Records the elements in pSelectedElements and their position at the 
	 * time where the method is called.
	 * 
	 * @param pSelectedElements The elements that are being moved. Not null.
	 */
	public void startTrackingMove(Iterable<Element> pSelectedElements)
	{
		assert pSelectedElements != null;
		
		aTrackedNamedElements.clear();
		aOriginalBounds.clear();
		
		for(Element element : pSelectedElements)
		{
			assert element != null;
			if(element instanceof NamedElement)
			{
				aTrackedNamedElements.add((NamedElement) element);
				aOriginalBounds.add(NamedElementViewerRegistry.getBounds((NamedElement)element));
			}
		}
	}

	/**
	 * Creates and returns a CompoundOperation that represents the movement
	 * of all tracked NamedElements between the time where startTrackingMove was 
	 * called and the time endTrackingMove was called.
	 * 
	 * @param pDiagramBuilder The Diagram containing the selected elements.
	 * @return A CompoundCommand describing the move.
	 * @pre pDiagramBuilder != null
	 */
	public CompoundOperation endTrackingMove(DiagramBuilder pDiagramBuilder)
	{
		assert pDiagramBuilder != null;
		CompoundOperation operation = new CompoundOperation();
		Rectangle[] selectionBounds2 = new Rectangle[aOriginalBounds.size()];
		int i = 0;
		for(NamedElement NamedElement : aTrackedNamedElements)
		{
			selectionBounds2[i] = NamedElementViewerRegistry.getBounds(NamedElement);
			i++;
		}
		for(i = 0; i < aOriginalBounds.size(); i++)
		{
			int dY = selectionBounds2[i].getY() - aOriginalBounds.get(i).getY();
			int dX = selectionBounds2[i].getX() - aOriginalBounds.get(i).getX();
			if(dX != 0 || dY != 0)
			{
				operation.add(pDiagramBuilder.createMoveNamedElementOperation(aTrackedNamedElements.get(i), dX, dY));
			}
		}
		return operation;
	}
}
