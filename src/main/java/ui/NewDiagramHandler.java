/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
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
