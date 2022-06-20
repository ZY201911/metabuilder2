package diagram.manager;

/**
 * Represents an operation to change a diagram, that
 * can be undone. Operations are only required to be valid
 * for a single execution and a single undoing in a row.
 */
public interface DiagramOperation
{
	/**
	 * Executes the operation.
	 */
	void execute();
	
	/**
	 * Undoes the operation.
	 */
	void undo();
}
