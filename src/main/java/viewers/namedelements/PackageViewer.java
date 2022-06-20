package viewers.namedelements;

import static geom.GeomUtils.max;

import java.util.Optional;

import diagram.NamedElement;
import diagram.Package;
import geom.Dimension;
import geom.Direction;
import geom.Point;
import geom.Rectangle;
import viewers.StringViewer;
import viewers.StringViewer.Alignment;
import viewers.StringViewer.TextDecoration;
import viewers.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * Common functionality to view the different types of package NamedElements.
 */
public class PackageViewer extends AbstractNamedElementViewer
{
	protected static final int PADDING = 10;
	protected static final int TOP_HEIGHT = 20;
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_BOTTOM_HEIGHT = 60;
	protected static final int DEFAULT_TOP_WIDTH = 60;
	protected static final int NAME_GAP = 3;
	private static final StringViewer NAME_VIEWER = StringViewer.get(Alignment.TOP_LEFT, TextDecoration.PADDED);
	
	@Override
	public void draw(NamedElement pNamedElement, GraphicsContext pGraphics)
	{
		assert pNamedElement instanceof Package;
		Rectangle topBounds = getTopBounds((Package)pNamedElement);
		Rectangle bottomBounds = getBottomBounds((Package)pNamedElement);
		ViewUtils.drawRectangle(pGraphics, topBounds);
		ViewUtils.drawRectangle(pGraphics, bottomBounds);
		NAME_VIEWER.draw(((Package)pNamedElement).getName(), pGraphics, new Rectangle(topBounds.getX() + NAME_GAP, 
				topBounds.getY(), topBounds.getWidth(), topBounds.getHeight()));
	}
	
	protected Rectangle getTopBounds(Package pNamedElement)
	{
		Optional<Rectangle> childrenBounds = getChildrenBounds(pNamedElement);
		Point position = getPosition(pNamedElement, childrenBounds);
		Dimension topDimension = getTopDimension(pNamedElement);
		return new Rectangle(position.getX(), position.getY(), topDimension.width(), topDimension.height());
	}
	
	protected Rectangle getBottomBounds(Package pNamedElement)
	{
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_BOTTOM_HEIGHT;
		
		Optional<Rectangle> childrenBounds = getChildrenBounds(pNamedElement);
		Point position = getPosition(pNamedElement, childrenBounds);
		
		Dimension topDimension = getTopDimension(pNamedElement);
		
		if( childrenBounds.isPresent() )
		{
			width = max( width, childrenBounds.get().getMaxX() + PADDING - position.getX());
			height = max( height, childrenBounds.get().getMaxY() + PADDING - position.getY() - topDimension.height());
		}
		
		width = max( width, topDimension.width()+ (DEFAULT_WIDTH - DEFAULT_TOP_WIDTH));
		
		return new Rectangle(position.getX(), position.getY() + topDimension.height(), 
				width, height);
	}
	
	private Optional<Rectangle> getChildrenBounds(NamedElement pNamedElement)
	{
		if( ((Package)pNamedElement).getChildren().isEmpty() )
		{
			return Optional.empty();
		}
		Rectangle childBounds = null;
		for( NamedElement child : ((Package)pNamedElement).getChildren() )
		{
			if( childBounds == null )
			{
				childBounds = NamedElementViewerRegistry.getBounds(child);
			}
			else
			{
				childBounds = childBounds.add(NamedElementViewerRegistry.getBounds(child));
			}
		}
		assert childBounds != null;
		return Optional.of(childBounds);
	}
	
	@Override
	public Point getConnectionPoint(NamedElement pNamedElement, Direction pDirection)
	{
		assert pNamedElement instanceof Package;
		Rectangle topBounds = getTopBounds((Package)pNamedElement);
		Rectangle bottomBounds = getBottomBounds((Package)pNamedElement);
		Rectangle bounds = topBounds.add(bottomBounds);
		
		Point connectionPoint = super.getConnectionPoint(pNamedElement, pDirection);
		if( connectionPoint.getY() < bottomBounds.getY() && topBounds.getMaxX() < connectionPoint.getX() )
		{
			// The connection point falls in the empty top-right corner, re-compute it so
			// it intersects the top of the bottom rectangle (basic triangle proportions)
			int delta = topBounds.getHeight() * (connectionPoint.getX() - bounds.getCenter().getX()) * 2 / 
					bounds.getHeight();
			int newX = connectionPoint.getX() - delta;
			if( newX < topBounds.getMaxX() )
			{
				newX = topBounds.getMaxX() + 1;
			}
			return new Point(newX, bottomBounds.getY());	
		}
		else
		{
			return connectionPoint;
		}
	}

	@Override
	protected Rectangle internalGetBounds(NamedElement pNamedElement)
	{
		assert pNamedElement instanceof Package;
		return getTopBounds((Package)pNamedElement).add(getBottomBounds((Package)pNamedElement));
	}
	
	/**
	 * @param pNamedElement The package NamedElement
	 * @return The point that corresponds to the actual top right
	 *     corner of the figure (as opposed to bounds).
	 */
	public Point getTopRightCorner(Package pNamedElement)
	{
		Rectangle bottomBounds = getBottomBounds(pNamedElement);
		return new Point(bottomBounds.getMaxX(), bottomBounds.getY());
	}
	
	
	protected Dimension getTopDimension(Package pNamedElement)
	{
		Dimension nameBounds = NAME_VIEWER.getDimension(pNamedElement.getName());
		int topWidth = max(nameBounds.width() + 2 * NAME_GAP, DEFAULT_TOP_WIDTH);
		int topHeight = max(nameBounds.height() - 2 * NAME_GAP, TOP_HEIGHT);
		return new Dimension(topWidth, topHeight);
	}
	
	/*
	 * The NamedElement's position might have to get adjusted if there are children
	 * whose position is to the left or up of the NamedElement's position.
	 */
	private Point getPosition(Package pNamedElement, Optional<Rectangle> pChildrenBounds)
	{
		if( !pChildrenBounds.isPresent() )
		{
			return pNamedElement.getPosition();
		}
		return new Point(pChildrenBounds.get().getX() - PADDING, 
				pChildrenBounds.get().getY() - PADDING - getTopDimension(pNamedElement).height());
	}
}
