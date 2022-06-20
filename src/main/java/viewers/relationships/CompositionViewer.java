package viewers.relationships;

import diagram.Composition;
import viewers.ArrowHead;
import viewers.LineStyle;

/**
 * A straight solid line with diamond arrow decoration.
 */
public final class CompositionViewer extends SegmentedRelationshipViewer
{	
	/**
	 * Creates a viewer for Composition instances.
	 */
	public CompositionViewer()
	{
		super(SegmentationStyleFactory.createHVHStrategy(),
				e -> LineStyle.SOLID, e -> getStartArrowHead((Composition)e), e -> ArrowHead.NONE,
				e -> ((Composition)e).getStartLabel(), 
				e -> ((Composition)e).getMidLabel(), 
				e -> ((Composition)e).getEndLabel());
	}
	
	/**
	 * @return The start arrow head
	 */
	private static ArrowHead getStartArrowHead(Composition pRelationship)
	{
		return ArrowHead.BLACK_DIAMOND;
	}
}