package diagram.manager.constraints;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import diagram.Diagram;
import diagram.Relationship;
import diagram.NamedElement;
import geom.Point;

/**
 * Represents a set of constraints.
 */
public class ConstraintSet
{
	private final Set<Constraint> aConstraints = new HashSet<>();
	
	/**
	 * Initializes a ConstraintSet with all the constraints in 
	 * pConstraints.
	 * 
	 * @param pConstraints The constraints to put in this set.
	 * @pre pConstraints != null.
	 */
	public ConstraintSet( Constraint... pConstraints )
	{
		assert pConstraints != null;
		aConstraints.addAll(Arrays.asList(pConstraints));
	}
		
	/**
	 * Returns True if and only if all the constraints in the set are satisfied.
	 * 
	 * @param pRelationship The Relationship on which the constraints are applied.
	 * @param pStart The start NamedElement for the Relationship.
	 * @param pEnd The end NamedElement for the Relationship.
	 * @param pStartPoint The point on the canvas where the Relationship rubber band starts.
	 * @param pEndPoint The point on the canvas where the Relationship rubber band ends.
	 * @param pDiagram The diagram in which the Relationship is to be added.
	 * @return True if and only if all the constraints in the set are satisfied.
	 */
	public boolean satisfied(Relationship pRelationship, NamedElement pStart, NamedElement pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram)
	{
		for( Constraint constraint : aConstraints )
		{
			if( !constraint.satisfied(pRelationship, pStart, pEnd, pStartPoint, pEndPoint, pDiagram))
			{
				return false;
			}
		}
		return true;
	}
}