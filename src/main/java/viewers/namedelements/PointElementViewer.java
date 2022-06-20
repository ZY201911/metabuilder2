package viewers.namedelements;

import diagram.NamedElement;
import geom.Direction;
import geom.Point;
import geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a PointNamedElement.
 */
public final class PointElementViewer extends AbstractNamedElementViewer
{
	private static final int SELECTION_DISTANCE = 5;
	
	@Override
	protected Rectangle internalGetBounds(NamedElement pNamedElement)
	{
		return new Rectangle(pNamedElement.getPosition().getX(), pNamedElement.getPosition().getY(), 0, 0);
	}

	@Override
	public boolean contains(NamedElement pNamedElement, Point pPoint)
	{
		return pNamedElement.getPosition().distance(pPoint) < SELECTION_DISTANCE;
	}

	@Override
	public Point getConnectionPoint(NamedElement pNamedElement, Direction pDirection)
	{
		return pNamedElement.getPosition();
	}
	
	@Override
	public void draw(NamedElement pNamedElement, GraphicsContext pGraphics) 
	{
		// Do nothing, a point is invisible.
	}
}
