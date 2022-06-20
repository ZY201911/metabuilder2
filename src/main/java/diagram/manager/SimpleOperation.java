package diagram.manager;

/**
 * Represents a simple (non-compound) operation. The operation
 * does no validation of the input method, so any code that 
 * constructs a SimpleOperation is responsible to ensure that,
 * when executed or undone, the operation will be valid.
 */
public class SimpleOperation implements DiagramOperation
{
	private final Runnable aOperation;
	private final Runnable aReverse;
	
	/**
	 * Creates an operation.
	 * 
	 * @param pOperation The code to run when the operation is executed.
	 * @param pReverse The code to run when the operation is undone.
	 * @pre pOperation != null
	 * @pre pReverse != null
	 */
	public SimpleOperation(Runnable pOperation, Runnable pReverse)
	{
		assert pOperation != null && pReverse != null;
		aOperation = pOperation;
		aReverse = pReverse;
	}

	@Override
	public void execute()
	{
		aOperation.run();
	}

	@Override
	public void undo()
	{
		aReverse.run();
	}
}
