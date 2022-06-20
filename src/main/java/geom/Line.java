package geom;

import static java.lang.Math.min;

import static java.lang.Math.abs;

/**
 * A pair of points in the integer space.
 */
public class Line
{
	private final Point aPoint1;
	private final Point aPoint2;
	
	/**
	 * Creates a new line.
	 * 
	 * @param pPoint1 The first point in the line. Conceptually the "start" of the line.
	 * @param pPoint2 The second point in the line. Conceptually the "end" of the line.
	 * @pre pPoint1 != null
	 * @pre pPoint2 != null
	 */
	public Line(Point pPoint1, Point pPoint2)
	{
		assert pPoint1 != null && pPoint2 != null;
		aPoint1 = pPoint1; 
		aPoint2 = pPoint2;
	}

	/**
	 * @return The first point of the line.
	 */
	public Point getPoint1()
	{
		return aPoint1;
	}
	
	/**
	 * @return The x coordinate of point 1.
	 */
	public int getX1()
	{
		return aPoint1.getX();
	}
	
	/**
	 * @return The y coordinate of point 1.
	 */
	public int getY1()
	{
		return aPoint1.getY();
	}
	
	/**
	 * @return The x coordinate of point 2.
	 */
	public int getX2()
	{
		return aPoint2.getX();
	}
	
	/**
	 * @return The y coordinate of point 2.
	 */
	public int getY2()
	{
		return aPoint2.getY();
	}
	
	/**
	 * @return The second point of the line.
	 */
	public Point getPoint2()
	{
		return aPoint2;
	}
	
	/**
	 * @return The rectangle spanning this line.
	 */
	public Rectangle spanning()
	{
		return new Rectangle(min(getX1(), getX2()), min(getY1(), getY2()), 
				abs(getX2() - getX1()), abs(getY2() - getY1()));
	}
	
	@Override
	public String toString()
	{
		return String.format("[%s, %s]", aPoint1, aPoint2);
	}
}
