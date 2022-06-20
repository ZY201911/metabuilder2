package viewers.relationships;

import java.util.function.Function;

import diagram.Relationship;
import geom.Conversions;
import geom.Dimension;
import geom.Line;
import geom.Point;
import geom.Rectangle;
import viewers.ArrowHead;
import viewers.LineStyle;
import viewers.StringViewer;
import viewers.StringViewer.Alignment;
import viewers.ToolGraphics;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * Renders Relationships as a path consisting of straight line segments.
 */
public class SegmentedRelationshipViewer extends AbstractRelationshipViewer
{
	private static final StringViewer TOP_CENTERED_STRING_VIEWER = StringViewer.get(Alignment.TOP_CENTER);
	private static final StringViewer BOTTOM_CENTERED_STRING_VIEWER = StringViewer.get(Alignment.BOTTOM_CENTER);
	private static final StringViewer LEFT_JUSTIFIED_STRING_VIEWER = StringViewer.get(Alignment.TOP_LEFT);
			
	private Function<Relationship, LineStyle> aLineStyleExtractor;
	private Function<Relationship, ArrowHead> aArrowStartExtractor;
	private Function<Relationship, ArrowHead> aArrowEndExtractor;
	private Function<Relationship, String> aStartLabelExtractor;
	private Function<Relationship, String> aMiddleLabelExtractor;
	private Function<Relationship, String> aEndLabelExtractor;
	private SegmentationStyle aStyle;
	
	/**
	 * @param pStyle The segmentation style.
	 * @param pLineStyle The line style.
	 * @param pStart The arrowhead at the start.
	 * @param pEnd The arrowhead at the start.
	 * @param pStartLabelExtractor Extracts the start label from the Relationship
	 * @param pMiddleLabelExtractor Extracts the middle label from the Relationship
	 * @param pEndLabelExtractor Extracts the end label from the Relationship
	 */
	public SegmentedRelationshipViewer(SegmentationStyle pStyle, Function<Relationship, LineStyle> pLineStyle, Function<Relationship, ArrowHead> pStart, 
			Function<Relationship, ArrowHead> pEnd, Function<Relationship, String> pStartLabelExtractor, 
			Function<Relationship, String> pMiddleLabelExtractor, Function<Relationship, String> pEndLabelExtractor)
	{
		aStyle = pStyle;
		aLineStyleExtractor = pLineStyle;
		aArrowStartExtractor = pStart;
		aArrowEndExtractor = pEnd;
		aStartLabelExtractor = pStartLabelExtractor;
		aMiddleLabelExtractor = pMiddleLabelExtractor;
		aEndLabelExtractor = pEndLabelExtractor;
	}
	
	/**
	 * Draws a string.
	 * @param pGraphics the graphics context
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param pString the string to draw 
	 * @param pCenter true if the string should be centered along the segment
	 */
	private void drawString(GraphicsContext pGraphics, Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrowHead, String pString, boolean pCenter, boolean pIsStepUp)
	{
		if (pString == null || pString.length() == 0)
		{
			return;
		}
		String label = wrapLabel(pString, pEndPoint1, pEndPoint2);
		Rectangle bounds = getStringBounds(pEndPoint1, pEndPoint2, pArrowHead, label, pCenter, pIsStepUp);
		if(pCenter) 
		{
			if ( pEndPoint2.getY() >= pEndPoint1.getY() )
			{
				TOP_CENTERED_STRING_VIEWER.draw(label, pGraphics, bounds);
			}
			else
			{
				BOTTOM_CENTERED_STRING_VIEWER.draw(label, pGraphics, bounds);
			}
		}
		else
		{
			LEFT_JUSTIFIED_STRING_VIEWER.draw(label, pGraphics, bounds);
		}
	}
	
	private String wrapLabel(String pString, Point2D pEndPoint1, Point2D pEndPoint2) 
	{
		int distanceInX = (int)Math.abs(pEndPoint1.getX() - pEndPoint2.getX());
		int distanceInY = (int)Math.abs(pEndPoint1.getY() - pEndPoint2.getY());
		return super.wrapLabel(pString, distanceInX, distanceInY);
	}

	@Override
	public void draw(Relationship pRelationship, GraphicsContext pGraphics)
	{
		Point2D[] points = getPoints(pRelationship);		
		ToolGraphics.strokeSharpPath(pGraphics, getSegmentPath(pRelationship), aLineStyleExtractor.apply(pRelationship));
		aArrowStartExtractor.apply(pRelationship).view().draw(pGraphics, 
				Conversions.toPoint(points[1]), 
				Conversions.toPoint(points[0]));
		
		aArrowEndExtractor.apply(pRelationship).view().draw(pGraphics, 
				Conversions.toPoint(points[points.length - 2]), 
				Conversions.toPoint(points[points.length - 1]));
		drawString(pGraphics, points[1], points[0], aArrowStartExtractor.apply(pRelationship), 
				aStartLabelExtractor.apply(pRelationship), false, isStepUp(pRelationship));
		drawString(pGraphics, points[points.length / 2 - 1], points[points.length / 2], null, 
				aMiddleLabelExtractor.apply(pRelationship), true, isStepUp(pRelationship));
		drawString(pGraphics, points[points.length - 2], points[points.length - 1], 
				aArrowEndExtractor.apply(pRelationship), aEndLabelExtractor.apply(pRelationship), false, isStepUp(pRelationship));
	}
	
	/**
	 * Computes the attachment point for drawing a string.
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param b the bounds of the string to draw
	 * @param pCenter true if the string should be centered along the segment
	 * @return the point at which to draw the string
	 */
	private static Point2D getAttachmentPoint(Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrow, Rectangle pDimension, boolean pCenter, boolean pIsStepUp)
	{    
		final int gap = 3;
		double xoff = gap;
		double yoff = -gap - pDimension.getHeight();
		Point2D attach = pEndPoint2;
		if (pCenter)
		{
			if (pEndPoint1.getX() > pEndPoint2.getX()) 
			{ 
				return getAttachmentPoint(pEndPoint2, pEndPoint1, pArrow, pDimension, pCenter, pIsStepUp); 
			}
			attach = new Point2D((pEndPoint1.getX() + pEndPoint2.getX()) / 2, 
					(pEndPoint1.getY() + pEndPoint2.getY()) / 2);
			if (pEndPoint1.getX() == pEndPoint2.getX() && pIsStepUp)
			{
				yoff = gap;
			}
			else if (pEndPoint1.getX() == pEndPoint2.getX() && !pIsStepUp)
			{
				yoff =  -gap-pDimension.getHeight();
			}
			else if (pEndPoint1.getY() == pEndPoint2.getY())
			{
				if (pDimension.getWidth() > Math.abs(pEndPoint1.getX() - pEndPoint2.getX()))
				{
					attach = new Point2D(pEndPoint2.getX() + (pDimension.getWidth() / 2) + gap, 
							(pEndPoint1.getY() + pEndPoint2.getY()) / 2);
				}
				xoff = -pDimension.getWidth() / 2;
			}
		}
		else 
		{
			if(pEndPoint1.getX() < pEndPoint2.getX())
			{
				xoff = -gap - pDimension.getWidth();
			}
			if(pEndPoint1.getY() > pEndPoint2.getY())
			{
				yoff = gap;
			}
			if(pArrow != null && pArrow != ArrowHead.NONE)
			{
				Bounds arrowBounds = pArrow.view().getPath(
						Conversions.toPoint(pEndPoint1), 
						Conversions.toPoint(pEndPoint2)).getBoundsInLocal();
				if(pEndPoint1.getY() == pEndPoint2.getY())
				{
					yoff -= arrowBounds.getHeight() / 2;
				}
				else if(pEndPoint1.getX() == pEndPoint2.getX())
				{
					xoff += arrowBounds.getWidth() / 2;
				}
			}
		}
		return new Point2D(attach.getX() + xoff, attach.getY() + yoff);
	}
	
	private Point2D[] getPoints(Relationship pRelationship)
	{
		return aStyle.getPath(pRelationship);
	}

	@Override
	public Line getConnectionPoints(Relationship pRelationship)
	{
		Point2D[] points = getPoints(pRelationship);
		return new Line(Conversions.toPoint(points[0]), 
				Conversions.toPoint(points[points.length - 1]));
	}
	
	@Override
	protected Shape getShape(Relationship pRelationship)
	{
		Path path = getSegmentPath(pRelationship);
		Point2D[] points = getPoints(pRelationship);
		path.getElements().addAll(aArrowStartExtractor.apply(pRelationship).view().getPath(
				Conversions.toPoint(points[1]),
				Conversions.toPoint(points[0])).getElements());
		path.getElements().addAll(aArrowEndExtractor.apply(pRelationship).view().getPath(
				Conversions.toPoint(points[points.length - 2]), 
				Conversions.toPoint(points[points.length - 1])).getElements());
		return path;
	}

	private Path getSegmentPath(Relationship pRelationship)
	{
		Point2D[] points = getPoints(pRelationship);
		Path path = new Path();
		Point2D p = points[points.length - 1];
		MoveTo moveTo = new MoveTo((float) p.getX(), (float) p.getY());
		path.getElements().add(moveTo);
		for(int i = points.length - 2; i >= 0; i--)
		{
			p = points[i];
			LineTo lineTo = new LineTo((float) p.getX(), (float) p.getY());
			path.getElements().add(lineTo);
		}
		return path;
	}
	
	/*
	 * Computes the extent of a string that is drawn along a line segment.
	 * @param p an endpoint of the segment along which to draw the string
	 * @param q the other endpoint of the segment along which to draw the string
	 * @param s the string to draw
	 * @param center true if the string should be centered along the segment
	 * @return the rectangle enclosing the string
	 */
	private static Rectangle getStringBounds(Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrow, String pString, boolean pCenter, boolean pIsStepUp)
	{
		if (pString == null || pString.isEmpty())
		{
			return new Rectangle((int)Math.round(pEndPoint2.getX()), 
					(int)Math.round(pEndPoint2.getY()), 0, 0);
		}
		
		Dimension textDimensions = textDimensions(pString);
		Rectangle stringDimensions = new Rectangle(0, 0, textDimensions.width(), textDimensions.height());
		Point2D a = getAttachmentPoint(pEndPoint1, pEndPoint2, pArrow, stringDimensions, pCenter, pIsStepUp);
		return new Rectangle((int)Math.round(a.getX()), (int)Math.round(a.getY()),
				Math.round(stringDimensions.getWidth()), Math.round(stringDimensions.getHeight()));
	}
	
	@Override
	public Rectangle getBounds(Relationship pRelationship)
	{
		Point2D[] points = getPoints(pRelationship);
		Rectangle bounds = super.getBounds(pRelationship);
		bounds = bounds.add(getStringBounds(points[1], points[0], 
				aArrowStartExtractor.apply(pRelationship), aStartLabelExtractor.apply(pRelationship), false, isStepUp(pRelationship)));
		bounds = bounds.add(getStringBounds(points[points.length / 2 - 1], 
				points[points.length / 2], null, aMiddleLabelExtractor.apply(pRelationship), true, isStepUp(pRelationship)));
		bounds = bounds.add(getStringBounds(points[points.length - 2], points[points.length - 1], 
				aArrowEndExtractor.apply(pRelationship), aEndLabelExtractor.apply(pRelationship), false, isStepUp(pRelationship)));
		return bounds;
	}
	
	private boolean isStepUp(Relationship pRelationship) 
	{
		Point point1 = RelationshipViewerRegistry.getConnectionPoints(pRelationship).getPoint1();
		Point point2 = RelationshipViewerRegistry.getConnectionPoints(pRelationship).getPoint2();
		return point1.getX() < point2.getX() && point1.getY() > point2.getY() || 
				point1.getX() > point2.getX() && point1.getY() < point2.getY();
	}

	@Override
	public Canvas createIcon(Relationship pRelationship) 
	{
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(OFFSET, OFFSET), new LineTo(BUTTON_SIZE-OFFSET, BUTTON_SIZE-OFFSET));
		ToolGraphics.strokeSharpPath(canvas.getGraphicsContext2D(), path, aLineStyleExtractor.apply(pRelationship));
		aArrowEndExtractor.apply(pRelationship).view().draw(canvas.getGraphicsContext2D(), 
				new Point(OFFSET, OFFSET), new Point(BUTTON_SIZE-OFFSET, BUTTON_SIZE - OFFSET));
		aArrowStartExtractor.apply(pRelationship).view().draw(canvas.getGraphicsContext2D(), 
				new Point(BUTTON_SIZE-OFFSET, BUTTON_SIZE - OFFSET), new Point(OFFSET, OFFSET));
		return canvas;
	}
}
