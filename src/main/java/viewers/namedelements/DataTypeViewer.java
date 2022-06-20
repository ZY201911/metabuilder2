package viewers.namedelements;

import static geom.GeomUtils.max;

import diagram.NamedElement;
import diagram.DataType;
import geom.Dimension;
import geom.Rectangle;
import viewers.StringViewer;
import viewers.StringViewer.Alignment;
import viewers.StringViewer.TextDecoration;
import viewers.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a class or interface in a class diagram.
 * 
 * The top box, which shows the title, has a minimum height of 20 pixels,
 * minus 20 if attributes are present, minus another 20 if methods are present.
 */
public class DataTypeViewer extends AbstractNamedElementViewer
{
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_HEIGHT = 60;
	protected static final int TOP_INCREMENT = 20;
	private static final StringViewer NAME_VIEWER = StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.BOLD, TextDecoration.PADDED);
	
	@Override
	public void draw(NamedElement pNamedElement, GraphicsContext pGraphics)
	{	
		assert pNamedElement instanceof DataType;
		DataType NamedElement = (DataType) pNamedElement;
		final Rectangle bounds = getBounds(pNamedElement);
		final int nameHeight = nameBoxHeight(NamedElement);

		ViewUtils.drawRectangle(pGraphics, bounds);	
		NAME_VIEWER.draw(getNameText(NamedElement), pGraphics, new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), nameHeight));	
	}
	
	private int nameBoxHeight(DataType pNamedElement)
	{
		final int textHeight = max(textDimensions(getNameText(pNamedElement)).height(), TOP_INCREMENT);
		final int freeSpaceInTopBox = DEFAULT_HEIGHT - textHeight;
		if( freeSpaceInTopBox < 0 )
		{
			return textHeight; // There's no free space so we return the height we need.
		}
		// We expand the name box to use the unclaimed free space
		return textHeight + freeSpaceInTopBox;
	}
	
	private static Dimension textDimensions(String pString)
	{
		Dimension result = Dimension.NULL;
		if( pString.length() > 0 )
		{
			result = NAME_VIEWER.getDimension(pString);
		}
		return result;
	}
	
	private static Dimension textDimensionsBold(String pString)
	{
		Dimension result = Dimension.NULL;
		if( pString.length() > 0 )
		{
			result = NAME_VIEWER.getDimension(pString);
		}
		return result;
	}
	
	@Override
	protected Rectangle internalGetBounds(NamedElement pNamedElement)
	{
		assert pNamedElement instanceof DataType;
		DataType NamedElement = (DataType) pNamedElement;
		final int nameHeight = nameBoxHeight(NamedElement);
		Dimension nameDimension = textDimensionsBold(getNameText(NamedElement));
		int width = max(DEFAULT_WIDTH, nameDimension.width());
		int height = nameHeight;
		return new Rectangle(NamedElement.getPosition().getX(), NamedElement.getPosition().getY(), width, height);
	}
	
	/**
	 * By default the name text is the name of the NamedElement.
	 * 
	 * @param pNamedElement The NamedElement.
	 * @return The text to show as the name of the NamedElement.
	 * @pre pNamedElement != null
	 */
	protected String getNameText(DataType pNamedElement)
	{
		assert pNamedElement != null;
		return pNamedElement.getName();
	}
}

