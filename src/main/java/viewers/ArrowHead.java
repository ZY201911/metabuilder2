package viewers;

/**
 * This class defines arrowheads of various shapes.
 */
public enum ArrowHead
{
	NONE, TRIANGLE, BLACK_TRIANGLE, V, HALF_V, DIAMOND, BLACK_DIAMOND;
	
	private final ArrowHeadView aView = new ArrowHeadView(this);
	
	/**
	 * @return An object that can draw this arrowhead.
	 */
	public ArrowHeadView view()
	{
		return aView;
	}
}
