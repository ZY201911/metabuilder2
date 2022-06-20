package viewers;

/**
 * Line styles and their properties.
 */
public enum LineStyle
{
	SOLID( new double[] {} ), DOTTED( new double[] {3, 3} );
	
	// The LineDashes StrokeAttribute. See Canvas API documentation.
	private final double[] aLineDashes;
	
	LineStyle(double[] pDashes)
	{
		aLineDashes = pDashes;
	}
	
	/**
	 * @return The LineDashes stroke attribute for this line style.
	 */
	public double[] getLineDashes()
	{
		return aLineDashes;
	}
}
