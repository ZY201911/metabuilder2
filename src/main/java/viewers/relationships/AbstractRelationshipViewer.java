package viewers.relationships;

import diagram.Relationship;
import geom.Dimension;
import geom.Direction;
import geom.Line;
import geom.Point;
import geom.Rectangle;
import viewers.StringViewer;
import viewers.StringViewer.Alignment;
import viewers.namedelements.NamedElementViewerRegistry;
import viewers.ToolGraphics;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * Provides shared services for viewing an Relationship.
 */
public abstract class AbstractRelationshipViewer implements RelationshipViewer
{
	protected static final int MAX_DISTANCE = 3;
	protected static final int BUTTON_SIZE = 25;
	protected static final int OFFSET = 3;
	protected static final int MAX_LENGTH_FOR_NORMAL_FONT = 15;
	private static final StringViewer SIZE_TESTER = StringViewer.get(Alignment.TOP_LEFT);
	
	private static final int DEGREES_180 = 180;
	
	/**
	 * The default behavior is to draw a straight line between
	 * the connections points oriented in the direction of each 
	 * other NamedElement.
	 * 
	 * @param pRelationship The Relationship whose shape we want
	 * @return The shape. 
	 * @pre pRelationship != null
	 */
	protected Shape getShape(Relationship pRelationship)
	{
		assert pRelationship != null;
		Line endPoints = getConnectionPoints(pRelationship);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(endPoints.getX1(), endPoints.getY1()), 
				new LineTo(endPoints.getX2(), endPoints.getY2()));
		return path;
	}
	
	/**
	 * @param pText Some text to test.
	 * @return The width and height of the text.
	 */
	protected static Dimension textDimensions( String pText )
	{
		return SIZE_TESTER.getDimension(pText);
	}
	
	@Override
	public boolean contains(Relationship pRelationship, Point pPoint)
	{
		// Purposefully does not include the arrow head and labels, which create large bounds.
		Line conn = getConnectionPoints(pRelationship);
		if(pPoint.distance(conn.getPoint1()) <= MAX_DISTANCE || pPoint.distance(conn.getPoint2()) <= MAX_DISTANCE)
		{
			return false;
		}

		Shape fatPath = getShape(pRelationship);
		fatPath.setStrokeWidth(2 * MAX_DISTANCE);
		return fatPath.contains(pPoint.getX(), pPoint.getY());
	}
	
	@Override
	public Rectangle getBounds(Relationship pRelationship)
	{
		Bounds bounds = getShape(pRelationship).getBoundsInLocal();
		return new Rectangle((int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getWidth(), (int)bounds.getHeight());
	}
	
	/*
	 * The default behavior implemented by this method
	 * is to find the connection point that each start/end
	 * NamedElement provides for a direction that is oriented
	 * following a straight line connecting the center
	 * of the rectangular bounds for each NamedElement.
	 */
	@Override
	public Line getConnectionPoints(Relationship pRelationship)
	{
		Rectangle startBounds = NamedElementViewerRegistry.getBounds(pRelationship.getStart());
		Rectangle endBounds = NamedElementViewerRegistry.getBounds(pRelationship.getEnd());
		Point startCenter = startBounds.getCenter();
		Point endCenter = endBounds.getCenter();
		Direction toEnd = Direction.fromLine(startCenter, endCenter);
		return new Line(NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), toEnd), 
				NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), toEnd.rotatedBy(DEGREES_180)));
	}

	@Override
	public void drawSelectionHandles(Relationship pRelationship, GraphicsContext pGraphics)
	{
		ToolGraphics.drawHandles(pGraphics, getConnectionPoints(pRelationship));		
	}
	
	protected String wrapLabel(String pString, int pDistanceInX, int pDistanceInY)
	{
		final int singleCharWidth = SIZE_TESTER.getDimension(" ").width();
		final int singleCharHeight = SIZE_TESTER.getDimension(" ").height();

		int lineLength = MAX_LENGTH_FOR_NORMAL_FONT;
		double distanceInX = pDistanceInX / singleCharWidth;
		double distanceInY = pDistanceInY / singleCharHeight;
		if (distanceInX > 0)
		{
			double angleInDegrees = Math.toDegrees(Math.atan(distanceInY/distanceInX));
			lineLength = Math.max(MAX_LENGTH_FOR_NORMAL_FONT, (int)((distanceInX / 4) * (1 - angleInDegrees / DEGREES_180)));
		}
		return SIZE_TESTER.wrapString(pString, lineLength);
	}
}
