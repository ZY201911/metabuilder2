package diagram.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An operation that is composed of other operations, following
 * the Composite Design Pattern.
 * 
 * Executing a compound operation executes all the sub-operations
 * in the order they were added. Undoing a compound operation
 * undoes all the sub-operation in the reverse order in which 
 * they were added.
 */
public class CompoundOperation implements DiagramOperation
{
	private List<DiagramOperation> aOperations = new ArrayList<>();
	
	/**
	 * Adds a sub-operation.
	 * 
	 * @param pOperation The operation to add. 
	 * @pre pOperation != null;
	 */
	public void add(DiagramOperation pOperation)
	{
		aOperations.add(pOperation);
	}

	@Override
	public void execute()
	{
		for( DiagramOperation operation : aOperations)
		{
			operation.execute();
		}
	}

	@Override
	public void undo()
	{
		ArrayList<DiagramOperation> reverse = new ArrayList<>(aOperations);
		Collections.reverse(reverse);
		for( DiagramOperation operation : reverse)
		{
			operation.undo();
		}
	}
	
	/**
	 * @return True if this CompoundOperation contains
	 *     no sub-operation.
	 */
	public boolean isEmpty()
	{
		return aOperations.isEmpty();
	}
}
