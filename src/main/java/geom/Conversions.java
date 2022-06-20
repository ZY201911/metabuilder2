package geom;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * Conversion utilities.
 */
public final class Conversions
{
	private Conversions() {}
	
	/**
	 * @param pPoint2D The point to convert.
	 * @return A point created from rounding both x and y coordinate
	 *     of the input parameter to integers.
	 * @pre pPoint2D != null;
	 */
	public static Point toPoint(Point2D pPoint2D)
	{
		assert pPoint2D != null;
		return new Point( (int)Math.round(pPoint2D.getX()), (int)Math.round(pPoint2D.getY()));
	}
	
	/**
	 * @param pPoint The point to covert.
	 * @return A Point2D with the same coordinates as pPoint.
	 * @pre pPoint != null;
	 */
	public static Point2D toPoint2D(Point pPoint)
	{
		assert pPoint != null;
		return new Point2D(pPoint.getX(), pPoint.getY());
	}
	
	/**
	 * @param pBounds An input bounds object.
	 * @return A rectangle that corresponds to pBounds.
	 * @pre pBounds != null;
	 */
	public static Rectangle toRectangle(Bounds pBounds)
	{
		assert pBounds != null;
		return new Rectangle((int)pBounds.getMinX(), (int)pBounds.getMinY(), (int)pBounds.getWidth(), (int)pBounds.getHeight());
	}
	
	/**
	 * @param pRectangle2D The rectangle to convert.
	 * @return A rectangle created from rounding both x and y coordinate and the width
	 *     and height of the input parameter to integers.
	 * @pre pRectangle2D != null;
	 */
	public static Rectangle toRectangle(Rectangle2D pRectangle2D)
	{
		assert pRectangle2D != null;
		return new Rectangle( (int)Math.round(pRectangle2D.getMinX()),
							  (int)Math.round(pRectangle2D.getMinY()),
							  (int)Math.round(pRectangle2D.getWidth()),
							  (int)Math.round(pRectangle2D.getHeight()));
	}
}
