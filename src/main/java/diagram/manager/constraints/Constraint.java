package diagram.manager.constraints;

import diagram.Diagram;
import diagram.Relationship;
import diagram.NamedElement;
import geom.Point;

/**
 * Represents a generic constraint on how an Relationship can be created.
 */
public interface Constraint
{
	/**
	 * @param pRelationships The Relationship on which the constraint is applied.
	 * @param pStart The start NamedElement for the Relationship.
	 * @param pEnd The end NamedElement for the Relationship.
	 * @param pStartPoint The point on the canvas where the Relationship rubber band starts.
	 * @param pEndPoint The point on the canvas where the Relationship rubber band ends.
	 * @param pDiagram The diagram in which the Relationship is to be added.
	 * @return True if this constraint is satisfied.
	 */
	boolean satisfied(Relationship pRelationships, NamedElement pStart, NamedElement pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram);
}