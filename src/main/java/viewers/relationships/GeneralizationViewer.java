package viewers.relationships;

import diagram.Generalization;
import viewers.ArrowHead;
import viewers.LineStyle;

/**
 * A straight solid line with triangle arrow decoration.
 */
public final class GeneralizationViewer extends SegmentedRelationshipViewer
{	
	/**
	 * Creates a viewer for Generalization instances.
	 */
	public GeneralizationViewer()
	{
		super(SegmentationStyleFactory.createVHVStrategy(),
				e -> getLineStyle((Generalization)e), e -> ArrowHead.NONE, e -> ArrowHead.TRIANGLE,
				e -> "", e -> "", 
				e -> "");
	}
	
	/**
	 * @return The line style for this Relationship.
	 */
	private static LineStyle getLineStyle(Generalization pRelationship)
	{
		return LineStyle.SOLID;
	}
}