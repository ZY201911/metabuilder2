package geom;

import static java.lang.Math.max;

/**
 * Represents a pair of width and height.
 */
public class Dimension
{
	public static final Dimension NULL = new Dimension(0, 0);
	
	private final int aWidth;
	private final int aHeight;
	
	/**
	 * @param pWidth The width. >= 0
	 * @param pHeight The height. >= 0
	 */
	public Dimension( int pWidth, int pHeight )
	{
		assert pWidth >= 0 && pHeight >= 0;
		aWidth = pWidth;
		aHeight = pHeight;
	}
	
	@Override
	public String toString()
	{
		return String.format("[Dimension: w=%d x h=%d]", aWidth, aHeight);
	}
	
	/**
	 * Creates a dimension that is the maximum of this dimension
	 * and pDimension for both width and height.
	 * 
	 * @param pWidth The minimum width to include.
	 * @param pHeight The minimum height to include.
	 * @return A new dimension object that has the maximum
	 *     width and height of either dimensions.
	 * @pre pWidth >= 0 && pHeight >= 0;
	 */
	public Dimension include(int pWidth, int pHeight)
	{
		assert pWidth >= 0 && pHeight >= 0;
		return new Dimension( max(aWidth, pWidth), max(aHeight, pHeight) );
	}
	
	/**
	 * @return The width component of this dimension.
	 */
	public int width()
	{
		return aWidth;
	}
	
	/**
	 * @return The height component of this dimension.
	 */
	public int height()
	{
		return aHeight;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		return prime * (prime + aHeight) + aWidth;
	}

	@Override
	public boolean equals(Object pObject)
	{
		if(this == pObject)
		{
			return true;
		}
		if(pObject == null)
		{
			return false;
		}
		if(getClass() != pObject.getClass())
		{
			return false;
		}
		Dimension other = (Dimension) pObject;
		return aHeight == other.aHeight && aWidth == other.aWidth;
	}
}
