package viewers.diagrams;

import java.util.Optional;

import diagram.Diagram;
import diagram.Element;
import diagram.Relationship;
import diagram.NamedElement;
import diagram.Package;
import geom.Point;
import geom.Rectangle;
import viewers.ViewerUtilities;
import viewers.relationships.RelationshipViewerRegistry;
import viewers.namedelements.NamedElementViewerRegistry;
import javafx.scene.canvas.GraphicsContext;

/**
 * A strategy for drawing a diagram and computing geometric properties of a 
 * diagram. This class can be inherited if certain diagram types require specialized 
 * services. This class is stateless.
 */
public class DiagramViewer
{
	/**
	 * Draws pDiagram onto pGraphics.
	 * 
	 * @param pGraphics the graphics context where the
	 *     diagram should be drawn.
	 * @param pDiagram the diagram to draw.
	 * @pre pDiagram != null && pGraphics != null.
	 */
	public final void draw(Diagram pDiagram, GraphicsContext pGraphics)
	{
		assert pDiagram != null && pGraphics != null;
		NamedElementViewerRegistry.activateNamedElementStorages();
		pDiagram.getNamedElements().forEach(NamedElement -> drawNamedElement(NamedElement, pGraphics));
		pDiagram.getRelationships().forEach(Relationship -> RelationshipViewerRegistry.draw(Relationship, pGraphics));
		NamedElementViewerRegistry.deactivateAndClearNamedElementStorages();
	}
	
	private void drawNamedElement(NamedElement pNamedElement, GraphicsContext pGraphics)
	{
		NamedElementViewerRegistry.draw(pNamedElement, pGraphics);
		if(pNamedElement instanceof Package) {
			((Package)pNamedElement).getChildren().forEach(NamedElement -> drawNamedElement(NamedElement, pGraphics));
		}
	}
	
	/**
	 * Returns the Relationship underneath the given point, if it exists.
	 * 
	 * @param pDiagram The diagram to query
	 * @param pPoint a point
	 * @return An Relationship containing pPoint or Optional.empty() if no Relationship is under pPoint
	 * @pre pDiagram != null && pPoint != null
	 */
	public final Optional<Relationship> RelationshipAt(Diagram pDiagram, Point pPoint)
	{
		assert pDiagram != null && pPoint != null;
		return pDiagram.getRelationships().stream()
				.filter(Relationship -> RelationshipViewerRegistry.contains(Relationship, pPoint))
				.findFirst();
	}
		
	/**
     * Finds a NamedElement that contains the given point. Always returns
     * the deepest child and the last one in a list.
     * @param pDiagram The diagram to query.
     * @param pPoint A point
     * @return a NamedElement containing pPoint or null if no NamedElements contain pPoint
     * @pre pDiagram != null && pPoint != null.
     */
	public final Optional<NamedElement> NamedElementAt(Diagram pDiagram, Point pPoint)
	{
		assert pDiagram != null && pPoint != null;
		return pDiagram.getNamedElements().stream()
			.map(NamedElement -> deepFindNamedElement(pDiagram, NamedElement, pPoint))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.reduce((first, second) -> second);
	}
	
	/**
     * Finds a NamedElement that contains the given point, if this is a NamedElement that can be 
     * selected. The difference between this method and NamedElementAt is that it is specialized for
     * NamedElements that can be selected by the users, whereas NamedElementAt is also used for Relationship creation.
     * By default, this method has the same behavior as NamedElementAt.
     * @param pDiagram The diagram to query.
     * @param pPoint A point
     * @return a NamedElement containing pPoint or null if no NamedElements contain pPoint
     * @pre pDiagram != null && pPoint != null.
     */
	public Optional<NamedElement> selectableNamedElementAt(Diagram pDiagram, Point pPoint)
	{
		return NamedElementAt(pDiagram, pPoint);
	}
	
	/**
	 * Find the "deepest" child that contains pPoint,
	 * where depth is measured in terms of distance from
	 * pNamedElement along the parent-child relation.
	 * @param pDiagram The diagram to query.
	 * @param pNamedElement The starting NamedElement for the search.
	 * @param pPoint The point to test for.
	 * @return The deepest child containing pPoint,
	 *     or null if pPoint is not contained by pNamedElement or 
	 *     any of its children.
	 * @pre pNamedElement != null, pPoint != null;
	 */
	protected Optional<NamedElement> deepFindNamedElement(Diagram pDiagram, NamedElement pNamedElement, Point pPoint)
	{
		assert pDiagram != null && pNamedElement != null && pPoint != null;
		
		if(pNamedElement instanceof Package) {
			return ((Package)pNamedElement).getChildren().stream()
					.map(NamedElement -> deepFindNamedElement(pDiagram, NamedElement, pPoint))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.findFirst()
					.or( () -> Optional.of(pNamedElement).filter(originalNamedElement -> NamedElementViewerRegistry.contains(originalNamedElement, pPoint)));

		}
		else if(NamedElementViewerRegistry.contains(pNamedElement, pPoint)) {
			return Optional.of(pNamedElement);
		}
		else return Optional.empty();
	}
	
	/**
	 * Gets the smallest rectangle enclosing the diagram.
	 * 
	 * @param pDiagram The diagram to query
	 * @return The bounding rectangle
	 * @pre pDiagram != null
	 */
	public final Rectangle getBounds(Diagram pDiagram)
	{
		assert pDiagram != null;
		Rectangle bounds = null;
		for(NamedElement NamedElement : pDiagram.getNamedElements() )
		{
			if(bounds == null)
			{
				bounds = NamedElementViewerRegistry.getBounds(NamedElement);
			}
			else
			{
				bounds = bounds.add(NamedElementViewerRegistry.getBounds(NamedElement));
			}
		}
		for(Relationship Relationship : pDiagram.getRelationships())
		{
			bounds = bounds.add(RelationshipViewerRegistry.getBounds(Relationship));
		}
		if(bounds == null )
		{
			return new Rectangle(0, 0, 0, 0);
		}
		else
		{
			return new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		}
	}
	
	/**
	 * Used during pasting to determine whether the current selection bounds completely overlaps the new elements.
	 * @param pCurrentSelectionBounds The current selection bounds
	 * @param pNewElements Elements to be pasted
	 * @return Is the current selection bounds completely overlapping the new elements
	 */
	public boolean isOverlapping(Rectangle pCurrentSelectionBounds, Iterable<Element> pNewElements) 
	{
		Rectangle newElementBounds = null;
		for (Element element : pNewElements) 
		{
			if (newElementBounds == null) 
			{
				newElementBounds = ViewerUtilities.getBounds(element);
			}
			newElementBounds = newElementBounds.add(ViewerUtilities.getBounds(element));
		}
		if (pCurrentSelectionBounds.equals(newElementBounds)) 
		{
			return true;
		}
		return false;
	}
}
