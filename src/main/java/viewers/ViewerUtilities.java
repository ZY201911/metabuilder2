package viewers;

import diagram.Element;
import diagram.Relationship;
import diagram.NamedElement;
import geom.Rectangle;
import viewers.relationships.RelationshipViewerRegistry;
import viewers.namedelements.NamedElementViewerRegistry;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Convenience methods to obtain viewer services.
 */
public final class ViewerUtilities
{
	private ViewerUtilities() {}
	
	/**
	 * Draws selection handles around pElement.
	 * 
	 * @param pElement The diagram element to select.
	 * @param pContext The graphics context.
	 * @pre pElement != null && pContext != null
	 */
	public static void drawSelectionHandles(Element pElement, GraphicsContext pContext)
	{
		assert pElement != null && pContext != null;
		if( pElement instanceof NamedElement )
		{
			NamedElementViewerRegistry.drawSelectionHandles((NamedElement) pElement, pContext);
		}
		else
		{
			assert pElement instanceof Relationship;
			RelationshipViewerRegistry.drawSelectionHandles((Relationship)pElement, pContext);
		}
	}
	
	/**
	 * Obtains the bounds for an element.
	 * 
	 * @param pElement The element whose bounds we want
	 * @return The bounds for this element.
	 * @pre pElement != null
	 */
	public static Rectangle getBounds(Element pElement)
	{
		assert pElement != null;
		if( pElement instanceof NamedElement )
		{
			return NamedElementViewerRegistry.getBounds((NamedElement)pElement);
		}
		else
		{
			assert pElement instanceof Relationship;
			return RelationshipViewerRegistry.getBounds((Relationship)pElement);
		}
	}
	
	/**
	 * @param pElement The element for which we want an icon
	 * @return An icon that represents this element
	 * @pre pElement != null
	 */
	public static Canvas createIcon(Element pElement)
	{
		/* 
		 * This method exists to cover the case where we wish to create an icon 
		 * for a diagram element without knowing whether it's a NamedElement or an Relationship.
		 */
		assert pElement != null;
		if( pElement instanceof NamedElement )
		{
			return NamedElementViewerRegistry.createIcon((NamedElement)pElement);
		}
		else
		{
			assert pElement instanceof Relationship;
			return RelationshipViewerRegistry.createIcon((Relationship)pElement);
		}
	}
}
