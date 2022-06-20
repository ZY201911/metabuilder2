package viewers.namedelements;

import java.util.IdentityHashMap;
import diagram.NamedElement;
import diagram.BClass;
import diagram.Package;
import diagram.PointElement;
import diagram.DataType;
import diagram.Enumeration;
import geom.Direction;
import geom.Point;
import geom.Rectangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Keeps track of the association between a NamedElement type and the viewer
 * that needs to be used to view it.
 */
public final class NamedElementViewerRegistry
{	
	private static final NamedElementViewerRegistry INSTANCE = new NamedElementViewerRegistry();
	
	private IdentityHashMap<Class<? extends NamedElement>, NamedElementViewer> aRegistry = 
			new IdentityHashMap<>();
	
	private NamedElementViewerRegistry() 
	{
		aRegistry.put(BClass.class, new BClassViewer());
		aRegistry.put(Package.class, new PackageViewer());
		aRegistry.put(DataType.class, new DataTypeViewer());
		aRegistry.put(Enumeration.class, new EnumerationViewer());
		aRegistry.put(PointElement.class, new PointElementViewer());
	}
	
	/**
	 * @param pNamedElement The NamedElement to view.
	 * @return A viewer for pNamedElement
	 * @pre pNamedElement != null;
	 */
	private NamedElementViewer viewerFor(NamedElement pNamedElement)
	{
		assert pNamedElement != null && aRegistry.containsKey(pNamedElement.getClass());
		return aRegistry.get(pNamedElement.getClass());
	}
	
   	/**
     * Tests whether pNamedElement contains a point.
     * @param pNamedElement the NamedElement to test
     * @param pPoint the point to test
     * @return true if this element contains aPoint
     */
	public static boolean contains(NamedElement pNamedElement, Point pPoint)
	{
		return INSTANCE.viewerFor(pNamedElement).contains(pNamedElement, pPoint);
	}
	
  	/**
   	 * Returns an icon that represents pNamedElement.
   	 * @param pNamedElement The NamedElement for which we need an icon.
     * @return A canvas object on which the icon is painted.
     * @pre pNamedElement != null
   	 */
   	public static Canvas createIcon(NamedElement pNamedElement)
   	{
   		return INSTANCE.viewerFor(pNamedElement).createIcon(pNamedElement);
   	}
   	
	/**
     * Draws pNamedElement.
     * @param pNamedElement The NamedElement to draw.
     * @param pGraphics the graphics context
     * @pre pNamedElement != null
	 */
   	public static void draw(NamedElement pNamedElement, GraphicsContext pGraphics)
   	{
   		INSTANCE.viewerFor(pNamedElement).draw(pNamedElement, pGraphics);
   	}
   	
   	/**
     * Draw selection handles around pNamedElement.
     * @param pNamedElement The target NamedElement
     * @param pGraphics the graphics context
     * @pre pNamedElement != null && pGraphics != null
	 */
   	public static void drawSelectionHandles(NamedElement pNamedElement, GraphicsContext pGraphics)
   	{
   		INSTANCE.viewerFor(pNamedElement).drawSelectionHandles(pNamedElement, pGraphics);
   	}
   	
	/**
     * Gets the smallest rectangle that bounds pNamedElement.
     * The bounding rectangle contains all labels.
     * @param pNamedElement The NamedElement whose bounds we wish to compute.
     * @return the bounding rectangle
     * @pre pNamedElement != null
   	 */
	public static Rectangle getBounds(NamedElement pNamedElement)
	{
		return INSTANCE.viewerFor(pNamedElement).getBounds(pNamedElement);
	}
	
  	/**
     * Gets the points at which pNamedElement is connected to
     * its NamedElements.
     * @param pNamedElement The target NamedElement
     * @param pDirection The desired direction.
     * @return A connection point on the NamedElement.
     * @pre pNamedElement != null && pDirection != null
     * 
     */
   	public static Point getConnectionPoints(NamedElement pNamedElement, Direction pDirection)
   	{
		return INSTANCE.viewerFor(pNamedElement).getConnectionPoint(pNamedElement, pDirection);
   	}
   	
   	/**
   	 * Activates all the NamedElementStorages of the NamedElementViewers present in the registry. 
   	 */
   	public static void activateNamedElementStorages()
   	{
   		for (NamedElementViewer NamedElementViewer : INSTANCE.aRegistry.values())
   		{
   			NamedElementViewer.activateNamedElementStorage();
   		}
   	}
   	
   	/**
   	 * Deactivates and clears all the NamedElementStorages of the NamedElementViewers present in the registry. 
   	 */
   	public static void deactivateAndClearNamedElementStorages()
   	{
   		for (NamedElementViewer NamedElementViewer : INSTANCE.aRegistry.values())
   		{
   			NamedElementViewer.deactivateAndClearNamedElementStorage();
   		}
   	}
}
