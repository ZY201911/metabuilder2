package viewers.relationships;

import diagram.Association;
import viewers.ArrowHead;
import viewers.LineStyle;

/**
 * A straight solid line.
 */
public final class AssociationViewer extends SegmentedRelationshipViewer
{	
	/**
	 * Creates a viewer for DependencyRelationship instances.
	 */
	public AssociationViewer()
	{
		super(SegmentationStyleFactory.createHVHStrategy(),
				e -> LineStyle.SOLID, 
				e -> getStartArrowHead((Association)e), 
				e -> getEndArrowHead((Association)e),
				e -> ((Association)e).getStartLabel(), 
				e -> ((Association)e).getMidLabel(), 
				e -> ((Association)e).getEndLabel());
	}
	
	/**
	 * @return The arrow end at the start of this Relationship.
	 */
	private static ArrowHead getStartArrowHead(Association pRelationship)
	{
		if( pRelationship.getBiDirection() )
		{
			return ArrowHead.V;
		}
		else
		{
			return ArrowHead.NONE;
		}
	}
	
	/**
	 * @return The arrow end at the end of this Relationship.
	 */
	private static ArrowHead getEndArrowHead(Association pRelationship)
	{
		if( pRelationship.getBiDirection() || pRelationship.getUniDirection() )
		{
			return ArrowHead.V;
		}
		else
		{
			return ArrowHead.NONE;
		}
	}
}