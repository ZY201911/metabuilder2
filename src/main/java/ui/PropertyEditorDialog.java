package ui;

import static resources.MetaBuilderResources.RESOURCES;

import utils.PropertyChangeTracker;
import diagram.Element;
import diagram.manager.CompoundOperation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A modal dialog that allows the user to edit the 
 * properties of a Element.
 */
public class PropertyEditorDialog
{
	private static final int LAYOUT_PADDING = 20;
	
	private final Stage aStage = new Stage();
	private final Element aElement;
	
	/**
	 * Creates a new dialog.
	 * 
	 * @param pOwner The stage that owns this stage.
	 * @param pElement The element to edit with this dialog.
	 * @param pPropertyChangeListener A callback to run whenever a property changes.
	 */
	public PropertyEditorDialog( Stage pOwner, Element pElement, 
			PropertySheet.PropertyChangeListener pPropertyChangeListener )
	{
		aElement = pElement;
		prepareStage(pOwner);
		aStage.setScene(createScene(pPropertyChangeListener));
	}
	
	private void prepareStage(Stage pOwner) 
	{
		aStage.setResizable(false);
		aStage.initModality(Modality.APPLICATION_MODAL);
		aStage.initOwner(pOwner);
		aStage.setTitle(RESOURCES.getString("dialog.properties"));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
	}
	
	private Scene createScene(PropertySheet.PropertyChangeListener pPropertyChangeListener) 
	{
		PropertySheet sheet = new PropertySheet(aElement, pPropertyChangeListener);
				
		BorderPane layout = new BorderPane();
		Button button = new Button(RESOURCES.getString("dialog.diagram_size.ok"));
		
		// The line below allows to click the button by using the "Enter" key, 
		// by making it the default button, but only when it has the focus.
		button.defaultButtonProperty().bind(button.focusedProperty());
		button.setOnAction(pEvent -> aStage.close());
		BorderPane.setAlignment(button, Pos.CENTER_RIGHT);
		
		layout.setPadding(new Insets(LAYOUT_PADDING));
		layout.setCenter(sheet);
		layout.setBottom(button);
		
		return new Scene(layout);
	}
	
	private PropertySheet getPropertySheet()
	{
		return (PropertySheet)((BorderPane) aStage.getScene().getRoot()).getCenter();
	}

	/**
	 * Shows the dialog and blocks the remainder of the UI
	 * until it is closed.
	 * 
	 * @return A compound operation that represents the modification of
	 *     each property modified through this dialog.
	 */
	public CompoundOperation show() 
	{
		if(!getPropertySheet().isEmpty())
		{
			PropertyChangeTracker tracker = new PropertyChangeTracker(getPropertySheet().getElement());
			tracker.startTracking();
			aStage.showAndWait();
			return tracker.stopTracking();
		}
		else
		{
			return new CompoundOperation();
		}
    }
}