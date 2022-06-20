package viewers.relationships;

import diagram.Relationship;
import diagram.NamedElement;
import geom.Direction;
import javafx.geometry.Point2D;

/**
 * A strategy for drawing a segmented line between two NamedElements.
 */
public interface SegmentationStyle
{
	/**
	 * The side of a rectangle.
	 * This seems to be redundant with Direction, but to 
	 * overload Direction to mean both a side and a direction is
	 * confusing.
	 */
	enum Side
	{WEST, NORTH, EAST, SOUTH;
		
		boolean isEastWest() 
		{ return this == WEST || this == EAST; }
		
		Direction getDirection()
		{
			switch(this)
			{
			case WEST:
				return Direction.WEST;
			case NORTH:
				return Direction.NORTH;
			case EAST:
				return Direction.EAST;
			case SOUTH:
				return Direction.SOUTH;
			default:
				return null;
			}
		}
		
		Side flip()
		{
			switch(this)
			{
			case WEST:
				return EAST;
			case NORTH:
				return SOUTH;
			case EAST:
				return WEST;
			case SOUTH:
				return NORTH;
			default:
				return null;
			}
		}
	}
	
	/**
	 * Determines if it is possible to use this segmentation style.
	 * @param pRelationship The Relationship to draw
	 * @return true if it is possible to use the segmentation style.
	 */
	boolean isPossible(Relationship pRelationship);
	
	/**
     * Gets the points at which the line representing an
     * Relationship is bent according to this strategy.
     * @param pRelationship the Relationship for which a path is determine
     * @return an array list of points at which to bend the
     *     segmented line representing the Relationship. Never null.
	 */
	Point2D[] getPath(Relationship pRelationship);
	
	/**
	 * Returns which side of the NamedElement attached to
	 * an Relationship is attached to the Relationship.
	 * @param pRelationship The Relationship to check.
	 * @param pNameElement The NamedElement to check.
	 * @return The side the Relationship leaves from.
	 * @pre pNameElement == pRelationship.getStart() || pNameElement == pRelationship.getEnd()
	 */
	Side getAttachedSide(Relationship pRelationship, NamedElement pNameElement);
}
