package viewers.namedelements;

import java.util.Optional;

import diagram.NamedElement;
import geom.Direction;
import geom.GeomUtils;
import geom.Point;
import geom.Rectangle;
import viewers.ToolGraphics;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Basic services for drawing NamedElements.
 */
public abstract class AbstractNamedElementViewer implements NamedElementViewer
{
	public static final int BUTTON_SIZE = 25;
	public static final int OFFSET = 3;
	
	private NamedElementStorage aNamedElementStorage = new NamedElementStorage();
	
	/* 
	 * The default behavior for containment is to return true if the point is
	 * within the bounding box of the NamedElement view.
	 * @see views.ElementView#contains(geom.Point)
	 */
	@Override
	public boolean contains(NamedElement pNamedElement, Point pPoint)
	{
		return getBounds(pNamedElement).contains(pPoint);
	}
	
	protected static Optional<Point> computeConnectionPointForCanonicalDirection(Rectangle pBounds, Direction pDirection)
	{
		Optional<Point> result = Optional.empty();
		if( pDirection == Direction.NORTH )
		{
			result = Optional.of(new Point(pBounds.getCenter().getX(), pBounds.getY()));
		}
		else if( pDirection == Direction.SOUTH )
		{
			result =  Optional.of(new Point(pBounds.getCenter().getX(), pBounds.getMaxY()));
		}
		else if( pDirection == Direction.EAST)
		{
			result = Optional.of(new Point(pBounds.getMaxX(), pBounds.getCenter().getY()));
		}
		else if( pDirection == Direction.WEST )
		{
			result = Optional.of(new Point(pBounds.getX(), pBounds.getCenter().getY()));
		}
		return result;
	}
	
	/* 
	 * The default behavior is to returns a point on the bounds of the NamedElement that intersects
	 * the side of the NamedElement at the point where a line in pDirection originating from the center
	 * intersects it.
	 */
	@Override
	public Point getConnectionPoint(NamedElement pNamedElement, Direction pDirection)
	{
		return GeomUtils.intersectRectangle(getBounds(pNamedElement), pDirection);
	}
	
	@Override
	public void drawSelectionHandles(NamedElement pNamedElement, GraphicsContext pGraphics)
	{
		ToolGraphics.drawHandles(pGraphics, getBounds(pNamedElement));		
	}
	
	@Override
	public Canvas createIcon(NamedElement pNamedElement)
	{
		Rectangle bounds = getBounds(pNamedElement);
		int width = bounds.getWidth();
		int height = bounds.getHeight();
		double scaleX = (BUTTON_SIZE - OFFSET)/ (double) width;
		double scaleY = (BUTTON_SIZE - OFFSET)/ (double) height;
		double scale = Math.min(scaleX, scaleY);
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.scale(scale, scale);
		graphics.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));
		graphics.setFill(Color.WHITE);
		graphics.setStroke(Color.BLACK);
		draw(pNamedElement, canvas.getGraphicsContext2D());
		return canvas;
	}
	
	@Override
	public final Rectangle getBounds(NamedElement pNamedElement)
	{
		return aNamedElementStorage.getBounds(pNamedElement, this::internalGetBounds);
	}
	
	@Override
	public final void activateNamedElementStorage()
	{
		aNamedElementStorage.activate();
	}
	
	@Override
	public final void deactivateAndClearNamedElementStorage() 
	{
		aNamedElementStorage.deactivateAndClear();
	}
	
	/**
     * Gets the smallest rectangle that bounds this element.
     * The bounding rectangle contains all labels.
     * @param pNamedElement The NamedElement whose bounds we want.
     * @pre pNamedElement != null
     * @return the bounding rectangle
   	 */
	protected abstract Rectangle internalGetBounds(NamedElement pNamedElement);
}
