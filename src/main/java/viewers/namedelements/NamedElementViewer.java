package viewers.namedelements;

import diagram.NamedElement;
import geom.Direction;
import geom.Point;
import geom.Rectangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract strategy that describes objects that can draw and
 * compute various geometric properties of NamedElements.
 */
public interface NamedElementViewer
{
	/**
     * Gets the smallest rectangle that bounds this element.
     * The bounding rectangle contains all labels.
     * @param pNamedElement The NamedElement whose bounds we want.
     * @pre pNamedElement != null
     * @return the bounding rectangle
   	 */
	Rectangle getBounds(NamedElement pNamedElement);
   	
	/**
     * Draw the element.
     * @param pNamedElement The NamedElement to draw.
     * @param pGraphics the graphics context
     * @pre pNamedElement != null && pGraphics != null
	 */
   	void draw(NamedElement pNamedElement, GraphicsContext pGraphics);
   	
   	/**
   	 * Returns an icon that represents the element.
   	 * @param pNamedElement The NamedElement to create an icon for.
     * @return A canvas object on which the icon is painted.
     * @pre pNamedElement != null
   	 */
   	Canvas createIcon(NamedElement pNamedElement);
   	
   	/**
     * Draw selection handles around the element.
     * @param pNamedElement The NamedElement to draw selection handles around
     * @param pGraphics the graphics context
     * @pre pNamedElement != null && pGraphics != null
	 */
   	void drawSelectionHandles(NamedElement pNamedElement, GraphicsContext pGraphics);
   	
   	/**
     * Tests whether the NamedElement contains a point.
     * @param pNamedElement The NamedElement to test
     * @param pPoint the point to test
     * @return true if this element contains aPoint
     * @pre pNamedElement != null && pPoint != null
     */
   	boolean contains(NamedElement pNamedElement, Point pPoint);
   	
   	/**
     * Get the best connection point to connect this NamedElement 
     * with another NamedElement. This should be a point on the boundary
     * of the shape of this NamedElement.
     * @param pNamedElement The target NamedElement.
     * @param pDirection the direction from the center 
     *     of the bounding rectangle towards the boundary 
     * @return the recommended connection point
     * @pre pNamedElement != null && pDirection != null
	 */
	Point getConnectionPoint(NamedElement pNamedElement, Direction pDirection);
	
	/**
	 * Activates the NamedElementStorage.
	 */
	void activateNamedElementStorage();
	
	/**
	 * Deactivates and clears the NamedElementStorage. 
	 */
	void deactivateAndClearNamedElementStorage();
}
