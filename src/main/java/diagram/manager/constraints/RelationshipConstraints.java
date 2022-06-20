package diagram.manager.constraints;

import diagram.Diagram;
import diagram.Relationship;
import diagram.NamedElement;
import diagram.PointElement;
import geom.Point;

/**
 * Methods to create Relationship addition constraints that apply to
 * all diagrams. CSOFF:
 */
public final class RelationshipConstraints
{
	private RelationshipConstraints() {}
	
	/*
	 * Only pNumber of Relationships of the same type are allowed in one direction between two NamedElements
	 */
	public static Constraint maxRelationships(int pNumber)
	{
		assert pNumber > 0;
		return (Relationship pRelationship, NamedElement pStart, NamedElement pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram)->
		{
			return numberOfRelationships(pRelationship.getClass(), pStart, pEnd, pDiagram) <= pNumber-1;
		};
	}
	
	/*
	 * Self-Relationships are not allowed.
	 */
	public static Constraint noSelfRelationship()
	{
		return (Relationship pRelationship, NamedElement pStart, NamedElement pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram)-> { return pStart != pEnd; };
	}

	/*
	 * Returns the number of Relationships of type pType between pStart and pEnd
	 */
	private static int numberOfRelationships(Class<? extends Relationship> pType, NamedElement pStart, NamedElement pEnd, Diagram pDiagram)
	{
		assert pType != null && pStart != null && pEnd != null && pDiagram != null;
		int result = 0;
		for(Relationship Relationship : pDiagram.getRelationships())
		{
			if(Relationship.getClass() == pType && Relationship.getStart() == pStart && Relationship.getEnd() == pEnd)
			{
				result++;
			}
		}
		return result;
	}
}