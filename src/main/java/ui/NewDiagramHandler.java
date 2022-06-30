package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * An event handler whose callback has the effect of opening a new 
 * diagram in the EditorFrame.
 */
public class NewDiagramHandler implements EventHandler<ActionEvent>
{
	private final EventHandler<ActionEvent> aHandler;
	
	/**
	 * Creates a new handler.
	 * 
	 * @param pDiagramType The type of diagram to open. Must be a subtype of Diagram.
	 * @param pHandler The function that opens a new diagram of this type.
	 */
	public NewDiagramHandler( EventHandler<ActionEvent> pHandler)
	{
		assert pHandler != null;
		aHandler = pHandler;
	}
	
	@Override
	public void handle(ActionEvent pEvent)
	{
		aHandler.handle(pEvent);
	}
}
