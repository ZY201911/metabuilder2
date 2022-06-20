package viewers.relationships;

import java.util.IdentityHashMap;

import diagram.Relationship;
import diagram.Composition;
import diagram.Association;
import diagram.Generalization;
import geom.Line;
import geom.Point;
import geom.Rectangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Keeps track of the association between an Relationship type and the viewer
 * that needs to be used to view it.
 */
public final class RelationshipViewerRegistry
{	
	private static final RelationshipViewerRegistry INSTANCE = new RelationshipViewerRegistry();
	
	private IdentityHashMap<Class<? extends Relationship>, RelationshipViewer> aRegistry = 
			new IdentityHashMap<>();
	
	private RelationshipViewerRegistry() 
	{
		aRegistry.put(Association.class,  new AssociationViewer());
		aRegistry.put(Generalization.class, new GeneralizationViewer());
		aRegistry.put(Composition.class, new CompositionViewer());
	}
	
	/**
	 * @param pRelationship The Relationship to view.
	 * @return A viewer for pRelationship
	 * @pre pRelationship != null;
	 */
	private RelationshipViewer viewerFor(Relationship pRelationship)
	{
		assert pRelationship != null && aRegistry.containsKey(pRelationship.getClass());
		return aRegistry.get(pRelationship.getClass());
	}
	
   	/**
     * Tests whether pRelationship contains a point.
     * @param pRelationship the Relationship to test
     * @param pPoint the point to test
     * @return true if this element contains aPoint
     */
	public static boolean contains(Relationship pRelationship, Point pPoint)
	{
		return INSTANCE.viewerFor(pRelationship).contains(pRelationship, pPoint);
	}
	
  	/**
   	 * Returns an icon that represents pRelationship.
   	 * @param pRelationship The Relationship for which we need an icon.
     * @return A canvas object on which the icon is painted.
     * @pre pRelationship != null
   	 */
   	public static Canvas createIcon(Relationship pRelationship)
   	{
   		return INSTANCE.viewerFor(pRelationship).createIcon(pRelationship);
   	}
   	
	/**
     * Draws pRelationship.
     * @param pRelationship The Relationship to draw.
     * @param pGraphics the graphics context
     * @pre pRelationship != null
	 */
   	public static void draw(Relationship pRelationship, GraphicsContext pGraphics)
   	{
   		INSTANCE.viewerFor(pRelationship).draw(pRelationship, pGraphics);
   	}
   	
   	/**
     * Draw selection handles around pRelationship.
     * @param pRelationship The target Relationship
     * @param pGraphics the graphics context
     * @pre pRelationship != null && pGraphics != null
	 */
   	public static void drawSelectionHandles(Relationship pRelationship, GraphicsContext pGraphics)
   	{
   		INSTANCE.viewerFor(pRelationship).drawSelectionHandles(pRelationship, pGraphics);
   	}
   	
	/**
     * Gets the smallest rectangle that bounds pRelationship.
     * The bounding rectangle contains all labels.
     * @param pRelationship The Relationship whose bounds we wish to compute.
     * @return the bounding rectangle
     * @pre pRelationship != null
   	 */
	public static Rectangle getBounds(Relationship pRelationship)
	{
		return INSTANCE.viewerFor(pRelationship).getBounds(pRelationship);
	}
	
  	/**
     * Gets the points at which pRelationship is connected to
     * its NamedElements.
     * @param pRelationship The target Relationship
     * @return a line joining the two connection points
     * @pre pRelationship != null
     * 
     */
   	public static Line getConnectionPoints(Relationship pRelationship)
   	{
		return INSTANCE.viewerFor(pRelationship).getConnectionPoints(pRelationship);
   	}
}
