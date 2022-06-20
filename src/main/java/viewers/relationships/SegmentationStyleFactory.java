package viewers.relationships;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import diagram.Relationship;
import diagram.NamedElement;
import diagram.Composition;
import diagram.Association;
import diagram.Generalization;
import diagram.Package;
import geom.Conversions;
import geom.Direction;
import geom.Point;
import viewers.relationships.SegmentationStyle.Side;
import viewers.namedelements.NamedElementViewerRegistry;
import viewers.namedelements.PackageViewer;
import javafx.geometry.Point2D;

/**
 * A class for creating line segmentation strategies.
 */
public final class SegmentationStyleFactory
{
	private static final PackageViewer PACKAGE_VIEWER = new PackageViewer();
	
	private static final int MARGIN = 20;
	private static final int MIN_SEGMENT = 10;
	private static final int MAX_NUDGE = 11;

	private SegmentationStyleFactory(){}
	
	/**
	 * Creates a strategy to draw straight (unsegmented) 
	 * lines by choosing the connection points that induce the 
	 * shortest path between two NamedElements (except in the case of self-paths). 
	 * @return A strategy for creating straight lines.
	 */
	public static SegmentationStyle createStraightStrategy()
	{
		return new Straight();
	}
	
	/**
	 * Creates a strategy that attempts to create horizontal links between
	 * NamedElements (except in the case of self-Relationships). If the NamedElement geometry
	 * does not permit it, attempts to use the VHV style and, if that
	 * still does not work, resorts to the straight style.
	 * @return A strategy for creating lines according to the HVH style.
	 */
	public static SegmentationStyle createHVHStrategy()
	{
		return new HVH();
	}
	
	/**
	 * Creates a strategy that attempts to create vertical links between
	 * NamedElements (except in the case of self-Relationships). If the NamedElement geometry
	 * does not permit it, attempts to use the HVH style and, if that
	 * still does not work, resorts to the straight style.
	 * @return A strategy for creating lines according to the VHV style.
	 */
	public static SegmentationStyle createVHVStrategy()
	{
		return new VHV();
	}
	
	/*
	 * The idea for creating a self path is to find the top left corner of 
	 * the actual figure and walk back N pixels away from it.
	 * Assumes that pNamedElement is composed of rectangles with sides at least
	 * N wide.
	 */
	private static Point2D[] createSelfPath(NamedElement pNamedElement)
	{
		Point2D topRight = findTopRightCorner(pNamedElement);
		double x1 = topRight.getX() - MARGIN;
		double y1 = topRight.getY();
		double x2 = x1;
		double y2 = y1 - MARGIN;
		double x3 = x2 + MARGIN * 2;
		double y3 = y2;
		double x4 = x3;
		double y4 = topRight.getY() + MARGIN;
		double x5 = topRight.getX();
		double y5 = y4;
		
		return new Point2D[] {new Point2D(x1, y1), new Point2D(x2, y2),
							  new Point2D(x3, y3), new Point2D(x4, y4), new Point2D(x5, y5)};
	}
	
	/*
	 * This solution is very complex if we can't assume any knowlRelationship
	 * of NamedElement types and only rely on getConnectionPoints, but it can
	 * be made quite optimal in exchange for an unpretty dependency to
	 * specific NamedElement types.
	 */
	private static Point2D findTopRightCorner(NamedElement pNamedElement)
	{
		if( pNamedElement instanceof Package )
		{
			return Conversions.toPoint2D(PACKAGE_VIEWER.getTopRightCorner((Package)pNamedElement)); 
		}
		else
		{
			return new Point2D(NamedElementViewerRegistry.getBounds(pNamedElement).getMaxX(), NamedElementViewerRegistry.getBounds(pNamedElement).getY());
		}
	}
	
	private static class Straight implements SegmentationStyle
	{
		@Override
		public Side getAttachedSide(Relationship pRelationship, NamedElement pNamedElement)
		{
			Side bestSide = Side.WEST; // Placeholder
			double shortestDistance = Double.MAX_VALUE;
			for( Side side : Side.values() )
			{
				Point start = NamedElementViewerRegistry.getConnectionPoints(pNamedElement, side.getDirection());
				for( Side inner : Side.values() )
				{
					Point end = NamedElementViewerRegistry.getConnectionPoints(otherNamedElement(pRelationship, pNamedElement), inner.getDirection());
					double distance = start.distance(end);
					if( distance < shortestDistance )
					{
						shortestDistance = distance;
						bestSide = side;
					}
				}
			}
			return bestSide;
		}
		
		@Override
		public boolean isPossible(Relationship pRelationship) 
		{
			return true;
		}
		
		@Override
		public Point2D[] getPath(Relationship pRelationship)
		{
			if( pRelationship.getStart() == pRelationship.getEnd() )
			{
				return createSelfPath(pRelationship.getStart());
			}
			
			Side startSide = getAttachedSide(pRelationship, pRelationship.getStart());
			Point start = NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), startSide.getDirection());
			if( pRelationship.getDiagram() != null )
			{
				start = computePointPosition(pRelationship.getStart(), startSide, computePosition(pRelationship, startSide, true));
			}
			
			Side endSide = getAttachedSide(pRelationship, pRelationship.getEnd());
			Point end = NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), endSide.getDirection());
			if( pRelationship.getDiagram() != null )
			{
				end = computePointPosition(pRelationship.getEnd(), endSide, computePosition(pRelationship, endSide, false));
			}
			
		    return new Point2D[] {Conversions.toPoint2D(start), Conversions.toPoint2D(end) };
		}		
	}
	
	/*
	 * Compute the point where to attach an Relationship in position pPosition on side pSide of NamedElement pNamedElement
	 */
	private static Point computePointPosition(NamedElement pNamedElement, Side pSide, Position pPosition)
	{
		assert pNamedElement != null && pSide != null && pPosition != null && pNamedElement.getOptionalDiagram().isPresent();
		Point start = NamedElementViewerRegistry.getConnectionPoints(pNamedElement, pSide.getDirection());
		if( pSide.isEastWest() )
		{
			double yPosition = start.getY()+ pPosition.computeNudge(NamedElementViewerRegistry.getBounds(pNamedElement).getHeight()); // Default
			if( hasSelfRelationship(pNamedElement) && pSide == Side.EAST )
			{
				double increment = (NamedElementViewerRegistry.getBounds(pNamedElement).getHeight() - MARGIN) / (pPosition.aTotal+1);
				yPosition = NamedElementViewerRegistry.getBounds(pNamedElement).getY() + MARGIN + pPosition.getIndex() * increment;
			}
			return new Point( start.getX(), (int) Math.round(yPosition));	
		}
		else
		{
			double xPosition = start.getX()+ pPosition.computeNudge(NamedElementViewerRegistry.getBounds(pNamedElement).getWidth());
			if( hasSelfRelationship(pNamedElement) && pSide == Side.NORTH )
			{
				double increment = (NamedElementViewerRegistry.getBounds(pNamedElement).getWidth() - MARGIN) / (pPosition.aTotal+1);
				xPosition = NamedElementViewerRegistry.getBounds(pNamedElement).getX() + pPosition.getIndex() * increment;
			}
			return new Point( (int) Math.round(xPosition), start.getY());
		}
	}
	
	private static boolean hasSelfRelationship(NamedElement pNamedElement)
	{
		assert pNamedElement.getOptionalDiagram().isPresent();
		for( Relationship Relationship : pNamedElement.getOptionalDiagram().get().RelationshipsConnectedTo(pNamedElement))
		{
			if( Relationship.getStart() == Relationship.getEnd())
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Computes the relative attachment position for an Relationship's NamedElement endpoint:
	 * either the start NamedElement (pForward == true) or the end NamedElement (pForward == false).
	 * The position is given in terms of top-bottom for sides, and left-to-right
	 * for top and bottom. 
	 * @param pRelationship The Relationship containing the NamedElement for which a connection is being calculated
	 * @param pStartSide The side of the NamedElement for which a connection is being calculated
	 * @param pForward true if this is the calculation for the start NamedElement of the Relationship
	 * @return The position on the side of the NamedElement where the Relationship should be connected.
	 */
	private static Position computePosition(Relationship pRelationship, Side pStartSide, boolean pForward)
	{
		assert pRelationship != null && pStartSide != null && pRelationship.getDiagram() != null;
		NamedElement tempTarget = pRelationship.getStart();
		if( !pForward )
		{
			tempTarget = pRelationship.getEnd();
		}
		final NamedElement target = tempTarget;
		List<Relationship> RelationshipsOnSelectedSide = getAllRelationshipsForSide(target, pStartSide);
		sortPositions(RelationshipsOnSelectedSide, target, pStartSide);
		
		// Group identical Relationship ends
		List<Relationship> finalPositions = new ArrayList<>();
		int index = -1;
		for( Relationship Relationship : RelationshipsOnSelectedSide )
		{
			int aggregated = -1;
			for( Relationship classifiedRelationship : finalPositions )
			{
				if( canAggregate(Relationship, classifiedRelationship, target))
				{
					aggregated = finalPositions.indexOf(classifiedRelationship);
					break;
				}
			}
			if( aggregated < 0 )
			{
				finalPositions.add(Relationship);
				aggregated = finalPositions.size() - 1;
			}
			if( Relationship == pRelationship )
			{
				index = aggregated;
			}
		}
		return new Position(index + 1, finalPositions.size());
	}
	
	// CSOFF:
	private static boolean canAggregate(Relationship pRelationship1, Relationship pRelationship2, NamedElement pTarget)
	{
		if( pRelationship1.getEnd() == pTarget && pRelationship2.getEnd() == pTarget &&
				pRelationship1 instanceof Generalization && pRelationship2 instanceof Generalization)
		{
			return true;
		}
		else if( pRelationship1.getStart() == pTarget && pRelationship2.getStart() == pTarget && 
				pRelationship1 instanceof Composition && pRelationship2 instanceof Composition)
		{
			return true;
		}
		else if( pRelationship1.getStart() == pTarget && pRelationship2.getStart() == pTarget && 
				pRelationship1 instanceof Composition && pRelationship2 instanceof Composition)
		{
			return true;
		}
		else
		{
			return false;
		}
	} // CSON:
	
	private static List<Relationship> getAllRelationshipsForSide(NamedElement pTarget, Side pSide)
	{
		assert pTarget.getOptionalDiagram().isPresent();
		List<Relationship> RelationshipsOnSelectedSide = new ArrayList<>();
		for( Relationship Relationship : pTarget.getOptionalDiagram().get().RelationshipsConnectedTo(pTarget))
		{
			if( otherNamedElement(Relationship, pTarget) == pTarget)
			{
				continue; // Do not count self-Relationships
			}
			if( !(isClassRelationshipRelationship(Relationship)))
			{
				continue;
			}
			getAttachedSide(Relationship, pTarget).filter( side -> side == pSide ).ifPresent( side -> RelationshipsOnSelectedSide.add(Relationship) );
		}
		return RelationshipsOnSelectedSide;
	}
	
	private static boolean isClassRelationshipRelationship(Relationship pRelationship)
	{
		return pRelationship instanceof Association ||
			   pRelationship instanceof Composition ||
			   pRelationship instanceof Generalization;
	}
	
	private static Optional<Side> getAttachedSide(Relationship pRelationship, NamedElement pTarget )
	{
		if( pRelationship instanceof Composition || pRelationship instanceof Association )
		{
			return Optional.of(SegmentationStyleFactory.createHVHStrategy().getAttachedSide(pRelationship, pTarget));
		}
		else if( pRelationship instanceof Generalization )
		{
			return Optional.of(SegmentationStyleFactory.createVHVStrategy().getAttachedSide(pRelationship, pTarget));
		}
		return Optional.empty();
	}
	
	// Sort in terms of the position of the other NamedElement
	private static void sortPositions(List<Relationship> pRelationships, NamedElement pTarget, Side pSide)
	{
		Collections.sort(pRelationships, (pRelationship1, pRelationship2) ->
		{
			NamedElement otherNamedElement1 = otherNamedElement(pRelationship1, pTarget);
			NamedElement otherNamedElement2 = otherNamedElement(pRelationship2, pTarget);
			
			if( otherNamedElement1 == otherNamedElement2)
			{
				// Sort by type
				int direction = pRelationship1.getClass().getSimpleName().compareTo(pRelationship2.getClass().getSimpleName());
				return direction;
			}
						
			if( pSide.isEastWest() )
			{		
				return NamedElementViewerRegistry.getBounds(otherNamedElement1).getCenter().getY() - 
						NamedElementViewerRegistry.getBounds(otherNamedElement2).getCenter().getY();
			}
			else
			{
				return NamedElementViewerRegistry.getBounds(otherNamedElement1).getCenter().getX() - 
						NamedElementViewerRegistry.getBounds(otherNamedElement2).getCenter().getX();
			}
		});
	}
	
	private static NamedElement otherNamedElement(Relationship pRelationship, NamedElement pNamedElement)
	{
		if( pRelationship.getStart() == pNamedElement)
		{
			return pRelationship.getEnd();
		}
		else
		{
			return pRelationship.getStart();
		}
	}
	
	private static class HVH implements SegmentationStyle
	{
		@Override
		public Side getAttachedSide(Relationship pRelationship, NamedElement pNamedElement)
		{
			Side lReturn = Side.WEST; // Placeholder
			if( pRelationship.getStart() == pRelationship.getEnd() )
			{
				if( pNamedElement == pRelationship.getStart() )
				{
					return Side.NORTH;
				}
				else
				{
					return Side.EAST;
				}
			}
			if( goingEast(pRelationship) )
			{
				if( pNamedElement == pRelationship.getStart() )
				{
					lReturn = Side.EAST;
				}
				else
				{
					lReturn = Side.WEST;
				}
			}
			else if( goingWest(pRelationship) )
			{
				if( pNamedElement == pRelationship.getStart() )
				{
					lReturn = Side.WEST;
				}
				else
				{
					lReturn = Side.EAST;
				}
			}
			else
			{
				SegmentationStyle vhv = new VHV();
				if( vhv.isPossible(pRelationship) )
				{
					lReturn = vhv.getAttachedSide(pRelationship, pNamedElement);
				}
				else
				{
					lReturn = new Straight().getAttachedSide(pRelationship, pNamedElement);
				}
			}
			return lReturn;
		}
		
		/*
		 * There is room for at least two segments going right from the start NamedElement
		 * to the end NamedElement.
		 */
		private static boolean goingEast(Relationship pRelationship)
		{
			return NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), Direction.EAST).getX() + 2 * MIN_SEGMENT <= 
					NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), Direction.WEST).getX();
		}
		
		/*
		 * There is room for at least two segments going left from the start NamedElement
		 * to the end NamedElement.
		 */
		private static boolean goingWest(Relationship pRelationship)
		{
			return NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), Direction.EAST).getX() + 2 * MIN_SEGMENT <= 
					NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), Direction.WEST).getX();
		}
		
		@Override
		public boolean isPossible(Relationship pRelationship) 
		{
			return goingEast(pRelationship) || goingWest(pRelationship);
		}
		
		@Override
		public Point2D[] getPath(Relationship pRelationship)
		{
			assert pRelationship != null;
			
			if( pRelationship.getStart() == pRelationship.getEnd() )
			{
				return createSelfPath(pRelationship.getStart());
			}
			if( !isPossible(pRelationship) )
			{
				SegmentationStyle alternate = new VHV();
				if( alternate.isPossible(pRelationship))
				{
					return alternate.getPath(pRelationship);
				}
				else
				{
					return new Straight().getPath(pRelationship);
				}
			}
			
			Point start = NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), Direction.EAST);
			Point end = NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), Direction.WEST);
			Side startSide = Side.EAST;
			
			if( goingEast(pRelationship) )
			{ 	// There is enough space to create the segment, we keep this order
			}
			else if( goingWest(pRelationship) )
			{ 	// The segment goes in the other direction
				startSide = Side.WEST;	
				start = NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), Direction.WEST);
				end = NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), Direction.EAST);
			}
						
			if( pRelationship.getDiagram() != null )
			{
				start = computePointPosition(pRelationship.getStart(), startSide, computePosition(pRelationship, startSide, true));
				end = computePointPosition(pRelationship.getEnd(), startSide.flip(), 
						computePosition(pRelationship, startSide.flip(), false));
			}
			
	  		if(Math.abs(start.getY() - end.getY()) <= MIN_SEGMENT)
	  		{
	  			return new Point2D[] {new Point2D(start.getX(), end.getY()), new Point2D(end.getX(), end.getY()) };
	  		}
	  		else
	  		{
	  			return new Point2D[] { new Point2D(start.getX(), start.getY()), 
	  								   new Point2D((start.getX() + end.getX()) / 2, start.getY()),
	  								   new Point2D((start.getX() + end.getX()) / 2, end.getY()), 
	  								   new Point2D(end.getX(), end.getY())};
	  		}
		}
	}
	
	private static class VHV implements SegmentationStyle
	{
		@Override
		public Side getAttachedSide(Relationship pRelationship, NamedElement pNamedElement)
		{
			Side lReturn = Side.SOUTH; // Placeholder
			if( pRelationship.getStart() == pRelationship.getEnd() )
			{
				if( pNamedElement == pRelationship.getStart() )
				{
					return Side.NORTH;
				}
				else
				{
					return Side.EAST;
				}
			}
			if( goingSouth(pRelationship) )
			{
				if( pNamedElement == pRelationship.getStart() )
				{
					lReturn = Side.SOUTH;
				}
				else
				{
					lReturn = Side.NORTH;
				}
			}
			else if( goingNorth(pRelationship) )
			{
				if( pNamedElement == pRelationship.getStart() )
				{
					lReturn = Side.NORTH;
				}
				else
				{
					lReturn = Side.SOUTH;
				}
			}
			else
			{
				SegmentationStyle hvh = new HVH();
				if( hvh.isPossible(pRelationship) )
				{
					lReturn = hvh.getAttachedSide(pRelationship, pNamedElement);
				}
				else
				{
					lReturn = new Straight().getAttachedSide(pRelationship, pNamedElement);
				}
			}
			return lReturn;
		}
		
		/*
		 * There is room for at least two segments going down from the start NamedElement
		 * to the end NamedElement.
		 */
		private static boolean goingSouth(Relationship pRelationship)
		{
			return NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), Direction.SOUTH).getY() + 2 * MIN_SEGMENT <= 
					NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), Direction.NORTH).getY();
		}
		
		/*
		 * There is room for at least two segments going up from the start NamedElement
		 * to the end NamedElement.
		 */
		private static boolean goingNorth(Relationship pRelationship)
		{
			return NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), Direction.SOUTH).getY() + 2 * MIN_SEGMENT <= 
					NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), Direction.NORTH).getY();
		}
		
		@Override
		public boolean isPossible(Relationship pRelationship)
		{	
			return goingSouth(pRelationship) || goingNorth(pRelationship);
		}
		
		@Override
		public Point2D[] getPath(Relationship pRelationship)
		{
			assert pRelationship != null;
			
			if( pRelationship.getStart() == pRelationship.getEnd() )
			{
				return createSelfPath(pRelationship.getStart());
			}
			if( !isPossible(pRelationship) )
			{
				SegmentationStyle alternate = new HVH();
				if( alternate.isPossible(pRelationship))
				{
					return alternate.getPath(pRelationship);
				}
				else
				{
					return new Straight().getPath(pRelationship);
				}
			}
			
			Point start = NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), Direction.SOUTH);
			Point end = NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), Direction.NORTH);
			Side startSide = Side.SOUTH;
			
			if( start.getY() + 2* MIN_SEGMENT <= end.getY() )
			{ 	// There is enough space to create the segment, we keep this order
			}
			else if( NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), Direction.SOUTH).getY() + 
					2 * MIN_SEGMENT <= NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), Direction.NORTH).getY() )
			{ 	// The segment goes in the other direction
				startSide = Side.NORTH;
				start = NamedElementViewerRegistry.getConnectionPoints(pRelationship.getStart(), Direction.NORTH);
				end = NamedElementViewerRegistry.getConnectionPoints(pRelationship.getEnd(), Direction.SOUTH);
			}
			
			if( pRelationship.getDiagram() != null )
			{
				start = computePointPosition(pRelationship.getStart(), startSide, computePosition(pRelationship, startSide, true));
				end = computePointPosition(pRelationship.getEnd(), startSide.flip(), 
						computePosition(pRelationship, startSide.flip(), false));
			}
			
	  		if(Math.abs(start.getX() - end.getX()) <= MIN_SEGMENT)
	  		{
	  			return new Point2D[] {new Point2D(end.getX(), start.getY()), new Point2D(end.getX(), end.getY())};
	  		}
	  		else
	  		{
	  			return new Point2D[] {new Point2D(start.getX(), start.getY()), 
	  								  new Point2D(start.getX(), (start.getY() + end.getY()) / 2), 
	  								  new Point2D(end.getX(), (start.getY() + end.getY()) / 2), 
	  								  new Point2D(end.getX(), end.getY())};
	  		}
		}
	}
	
	/** 
	 * Indicates the total number of connection points
	 * on the side of a rectangular NamedElement, and the index
	 * of a NamedElement. Immutable. The index starts at 1.
	 */
	private static class Position
	{
		private int aIndex;
		private int aTotal;
		
		Position( int pIndex, int pTotal)
		{
			aIndex = pIndex;
			aTotal = pTotal;
		}
		
		int getIndex()
		{
			return aIndex;
		}
		
		/* Returns the index in the middle of the series */
		private double getMiddle()
		{
			return ((double)aTotal +1 )/2.0;
		}
		
		/* Returns the nudge value for a position */
		double computeNudge(double pMaxWidth)
		{
			double increment = MAX_NUDGE;
			double availableSpace = pMaxWidth - (2 * MAX_NUDGE  );
			if( (aTotal - 2) * MAX_NUDGE > availableSpace )
			{
				increment = availableSpace / (aTotal - 1);
			}
			return -(getMiddle()-getIndex()) * increment;
		}
		
		public String toString()
		{
			return aIndex + " of " + aTotal;
		}
	}
}

