package viewers;

import geom.Dimension;
import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A utility class to determine various font position metrics
 * for the particular font.
 * 
 * A visual diagram for why the bounds values are what they are
 * (with word "Thy"):   ____________________
 * getMinY() (ascent)  |*****  *           |
 *                     |  *    *           |
 *                     |  *    *****   *  *|
 *                     |  *    *   *   *  *|
 *                     |  *    *   *   ****|
 * (baseline)          |------------------*| x=getWidth()
 *                     |                  *|
 *                     |                  *| 
 * y = 0  (descent)    |                ***|
 *                     |                   |
 *                     |                   |
 * getMaxY() (leading) |-------------------|
 *
 * Hence, upon calling getHeight(), to get tight bounds, one should subtract
 * off the leading value (found by getting the max Y value of a one-lined text
 * box)
 */
public class FontMetrics 
{
	public static final int DEFAULT_FONT_SIZE = 12;
	private static final String BLANK = "";
	private Text aTextNamedElement;

	/**
	 * Creates a new FontMetrics object.
	 * @param pFont The font to use.
	 */

	public FontMetrics(Font pFont)
	{
		assert pFont != null;
		
		aTextNamedElement = new Text();
		aTextNamedElement.setFont(pFont);
	}

	/**
	 * Returns the dimension of a given string.
	 * @param pString The string to which the bounds pertain.
	 * @return The dimension of the string
	 */
	public Dimension getDimension(String pString)
	{
		assert pString != null;
		
		aTextNamedElement.setText(pString);
		Bounds bounds = aTextNamedElement.getLayoutBounds();
		aTextNamedElement.setText(BLANK);
		double leading = aTextNamedElement.getLayoutBounds().getMaxY();
		return new Dimension((int) Math.round(bounds.getWidth()), (int) Math.round(bounds.getHeight() - leading));
	}
} 