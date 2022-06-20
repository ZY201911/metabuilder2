package viewers.namedelements;

import static geom.GeomUtils.max;

import diagram.NamedElement;
import diagram.Enumeration;
import geom.Dimension;
import geom.Rectangle;
import viewers.LineStyle;
import viewers.StringViewer;
import viewers.StringViewer.Alignment;
import viewers.StringViewer.TextDecoration;
import viewers.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a class or interface in a class diagram.
 * 
 * The top box, which shows the title, has a minimum height of 20 pixels,
 * minus 20 if literals are present, minus another 20 if methods are present.
 */
public class EnumerationViewer extends AbstractNamedElementViewer
{
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_HEIGHT = 60;
	protected static final int TOP_INCREMENT = 20;
	private static final StringViewer NAME_VIEWER = StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.BOLD, TextDecoration.PADDED);
	private static final StringViewer STRING_VIEWER = StringViewer.get(Alignment.TOP_LEFT, TextDecoration.PADDED);
	
	@Override
	public void draw(NamedElement pNamedElement, GraphicsContext pGraphics)
	{	
		assert pNamedElement instanceof Enumeration;
		Enumeration NamedElement = (Enumeration) pNamedElement;
		final Rectangle bounds = getBounds(pNamedElement);
		final int literalHeight = literalBoxHeight(NamedElement);
		final int nameHeight = nameBoxHeight(NamedElement, literalHeight);

		ViewUtils.drawRectangle(pGraphics, bounds);	
		NAME_VIEWER.draw(getNameText(NamedElement), pGraphics, new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), nameHeight));
		
		if( literalHeight > 0 )
		{
			final int splitY = bounds.getY() + nameHeight;
			ViewUtils.drawLine(pGraphics, bounds.getX(), splitY, bounds.getMaxX(), splitY, LineStyle.SOLID);
			STRING_VIEWER.draw(NamedElement.getLiteralsString(), pGraphics, new Rectangle(bounds.getX(), splitY, bounds.getWidth(), literalHeight));
		}
	}
	
	private static int literalBoxHeight(Enumeration pNamedElement)
	{
		return textDimensions(pNamedElement.getLiteralsString()).height();
	}
	
	private int nameBoxHeight(Enumeration pNamedElement, int pLiteralBoxHeight)
	{
		final int textHeight = max(textDimensions(getNameText(pNamedElement)).height(), TOP_INCREMENT);
		final int freeSpaceInTopBox = DEFAULT_HEIGHT - textHeight;
		if( freeSpaceInTopBox < 0 )
		{
			return textHeight; // There's no free space so we return the height we need.
		}
		if( pLiteralBoxHeight > freeSpaceInTopBox )
		{
			return textHeight; // We use all the free space so we return the height we need.
		}
		// We expand the name box to use the unclaimed free space
		return textHeight + freeSpaceInTopBox - pLiteralBoxHeight;
	}
	
	private static Dimension textDimensions(String pString)
	{
		Dimension result = Dimension.NULL;
		if( pString.length() > 0 )
		{
			result = STRING_VIEWER.getDimension(pString);
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
		assert pNamedElement instanceof Enumeration;
		Enumeration NamedElement = (Enumeration) pNamedElement;
		final int literalHeight = literalBoxHeight(NamedElement);
		final int nameHeight = nameBoxHeight(NamedElement, literalHeight);
		Dimension nameDimension = textDimensionsBold(getNameText(NamedElement));
		Dimension literalDimension = textDimensions(NamedElement.getLiteralsString());
		int width = max(DEFAULT_WIDTH, nameDimension.width(), literalDimension.width());
		int height = literalHeight + + nameHeight;
		return new Rectangle(NamedElement.getPosition().getX(), NamedElement.getPosition().getY(), width, height);
	}
	
	/**
	 * By default the name text is the name of the NamedElement.
	 * 
	 * @param pNamedElement The NamedElement.
	 * @return The text to show as the name of the NamedElement.
	 * @pre pNamedElement != null
	 */
	protected String getNameText(Enumeration pNamedElement)
	{
		assert pNamedElement != null;
		return pNamedElement.getName();
	}
}

