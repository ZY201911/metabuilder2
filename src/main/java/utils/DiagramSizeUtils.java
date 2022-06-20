package utils;

/**
 * Utility methods for managing the size of a 
 * diagram view.
 */
public final class DiagramSizeUtils
{
	public static final int MAX_SIZE = 4000;
	public static final int MIN_SIZE = 250;
	
	private DiagramSizeUtils()
	{}
	
	/**
	 * @param pValue A value to test
	 * @return True if pValue is a valid diagram dimension
	 */
	public static boolean isValid(int pValue)
	{
		return pValue >= MIN_SIZE && pValue <= MAX_SIZE;
	}
	
	/**
	 * @param pText The value to test
	 * @return True if pText can be converted to an integer
	 *     that is a valid diagram dimension.
	 */
	public static boolean isValid(String pText)
	{
		try
		{
			return isValid(Integer.parseInt(pText));
		}
		catch( NumberFormatException exception )
		{
			return false;
		}
	}
}
