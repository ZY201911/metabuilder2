package viewers.relationships;

import diagram.Relationship;
import geom.Line;
import geom.Point;
import geom.Rectangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract strategy that describes objects that can draw and
 * compute various geometric properties of Relationships.
 */
public interface RelationshipViewer
{
	/**
     * Gets the smallest rectangle that bounds pRelationship.
     * The bounding rectangle contains all labels.
     * @param pRelationship The Relationship whose bounds we wish to compute.
     * @return the bounding rectangle
     * @pre pRelationship != null
   	 */
	Rectangle getBounds(Relationship pRelationship);
	
	/**
     * Draws pRelationship.
     * @param pRelationship The Relationship to draw.
     * @param pGraphics the graphics context
     * @pre pRelationship != null
	 */
   	void draw(Relationship pRelationship, GraphicsContext pGraphics);
   	
   	/**
   	 * Returns an icon that represents pRelationship.
   	 * @param pRelationship The Relationship for which we need an icon.
     * @return A canvas object on which the icon is painted.
     * @pre pRelationship != null
   	 */
   	Canvas createIcon(Relationship pRelationship);
   	
   	/**
     * Draw selection handles around pRelationship.
     * @param pRelationship The target Relationship
     * @param pGraphics the graphics context
     * @pre pRelationship != null && pGraphics != null
	 */
   	void drawSelectionHandles(Relationship pRelationship, GraphicsContext pGraphics);
   	
   	/**
     * Tests whether pRelationship contains a point.
     * @param pRelationship the Relationship to test
     * @param pPoint the point to test
     * @return true if this element contains aPoint
     */
   	boolean contains(Relationship pRelationship, Point pPoint);
   	
   	/**
     * Gets the points at which pRelationship is connected to
     * its NamedElements.
     * @param pRelationship The target Relationship
     * @return a line joining the two connection points
     * @pre pRelationship != null
     * 
     */
   	Line getConnectionPoints(Relationship pRelationship);
}
