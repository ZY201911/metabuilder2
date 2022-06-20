package ui;

/**
 * Objects interested in changes to the state of a SelectionModel.
 */
public interface SelectionObserver
{
	/**
	 * Called whenever the state of pModel changes.
	 */
	void selectionModelChanged();
}
