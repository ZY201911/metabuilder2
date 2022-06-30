package ui;

import static resources.MetaBuilderResources.RESOURCES;
import java.util.Map;

import diagram.Element;
import diagram.Property;
import diagram.BClass;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 *  A layout that presents the properties of a Element
 *  and allow editing them.
 */
public class PropertySheet extends GridPane
{
    private static final KeyCombination STEREOTYPE_DELIMITER_TRIGGER = 
    		new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);

	/**
	 * A handler for whenever a property is being detected
	 * as being edited. This allows a more responsive UI,
	 * where properties can be shown as they are typed, as
	 * opposed to only when the value is entered.
	 */
	interface PropertyChangeListener
	{
		void propertyChanged();
	}
	
	private static final int TEXT_FIELD_WIDTH = 10;
	private static final int LAYOUT_SPACING = 10;
	private static final int LAYOUT_PADDING = 20;

	private final PropertyChangeListener aListener;
	private final Element aElement;

	/**
	 * Constructs a PropertySheet to show and support editing all the properties 
	 * for pElement.
	 * 
	 * @param pElement The element whose properties we wish to edit.
	 * @param pListener An object that responds to property change events.
	 * @pre pElement != null
	 */
	public PropertySheet(Element pElement, PropertyChangeListener pListener)
	{
		assert pElement != null;
		aListener = pListener;
		aElement = pElement;
		int row = 0;
		Property tempProperty = aElement.getProperties().get("name");
		if( tempProperty != null )
		{
			Control tempEditor = getEditorControl("name", tempProperty);
			if( tempEditor != null )
			{
				add(new Label(labelName(pElement, "name")), 0, row);
				add(tempEditor, 1, row);
				row++;
			}
		}
		tempProperty = aElement.getProperties().get("attributes");
		if( tempProperty != null )
		{
			Control tempEditor = getEditorControl("attributes", tempProperty);
			if( tempEditor != null )
			{
				add(new Label(labelName(pElement, "attributes")), 0, row);
				add(tempEditor, 1, row);
				row++;
			}
		}
		for(Map.Entry<String, Property> entry : aElement.getProperties().entrySet()) {
			if(entry.getKey() == "name" || entry.getKey() == "attributes") {
				continue;
			}
			Control editor = getEditorControl(entry.getKey(), entry.getValue());
			if( editor != null )
			{
				add(new Label(labelName(pElement, entry.getKey())), 0, row);
				add(editor, 1, row);
				row++;
			}
		}
		setVgap(LAYOUT_SPACING);
		setHgap(LAYOUT_SPACING);
		setPadding(new Insets(LAYOUT_PADDING));
	}
	
	/*
	 * Special case due to the poor decision in the past to declare NamedElementNamedElements as a 
	 * subclass of Name NamedElements, which means that its control would use the NAME
	 * property and thus be labeled with "Name" instead of "Text". Ideally, the 
	 * property for NamedElementNamedElements should be a new enum called TEXT, and NoteNamedElement should 
	 * not be children of NamedNamedElements. However, changing this would break backward
	 * compatibility.
	 * TODO: Next time we do a backward-incompatible release, we'll fix this.
	 */
	private static String labelName(Element pElement, String pPropertyName)
	{
		return RESOURCES.getString("property." + pPropertyName.toLowerCase());
	}
	
	/**
	 * @return aEmpty whether this PropertySheet has fields to edit or not.
	 */
	public boolean isEmpty()
	{
		return getChildren().isEmpty();
	}
	
	/**
	 * @return The element being edited.
	 */
	public Element getElement()
	{
		return aElement;
	}

	private Control getEditorControl(String type, Property pProperty)   
	{
		if(type == "name" || type == "startLabel" || type == "midLabel" || type == "endLabel")
		{
			return createStringEditor(type, pProperty);
		}
		else
		{
			return createExtendedStringEditor(type, pProperty);
		}
	}

	private Control createStringEditor(String type, Property pProperty)
	{
		TextField textField = new TextField((String) pProperty.getValue());
		textField.setPrefColumnCount(TEXT_FIELD_WIDTH);
		addStereotypeDelimiterFeature(textField);
		
		textField.textProperty().addListener((pObservable, pOldValue, pNewValue) -> 
		{
			pProperty.setValue(textField.getText());
			aListener.propertyChanged();
		});
		
		textField.setId(type + "Property");

		return textField;
	}
	
	private Control createExtendedStringEditor(String type, Property pProperty)
	{
		final int rows = 5;
		final int columns = 30;
		final TextArea textArea = new TextArea();
		textArea.setPrefRowCount(rows);
		textArea.setPrefColumnCount(columns);

		addTabbingFeature(textArea);
		addStereotypeDelimiterFeature(textArea);

		textArea.setText((String) pProperty.getValue());
		textArea.textProperty().addListener((pObservable, pOldValue, pNewValue) -> 
		{
		   pProperty.setValue(textArea.getText());
		   aListener.propertyChanged();
		});
		
		textArea.setId(type + "Property");
		
		return new ScrollPane(textArea);
	}
	
	/*
	 * Add a feature to the control that adds the stereotype delimiters to the input control
	 * if Ctrl-Q is typed, and position the caret between the delimiters.
	 */
	private static void addStereotypeDelimiterFeature(TextInputControl pTextInput)
	{
		pTextInput.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() 
		{
		    public void handle(KeyEvent pKeyEvent) 
		    {
		    	if(STEREOTYPE_DELIMITER_TRIGGER.match(pKeyEvent)) 
		    	{
		    		pTextInput.setText(pTextInput.getText() + "\u00AB\u00BB");
		    		pTextInput.end();
		    		pTextInput.backward();
		    		pKeyEvent.consume();
		    	}
		    }
		});
	}
	
	/*
	 * Make it possible to insert tab characters in a text area using Ctrl-Tab.
	 * The tab character is otherwise used to switch between fields.
	 */
	private static void addTabbingFeature(TextArea pTextArea)
	{
		pTextArea.addEventFilter(KeyEvent.KEY_PRESSED, pKeyEvent ->
		{
			final String aFocusEventText = "TAB_TO_FOCUS_EVENT";
			
			if (!KeyCode.TAB.equals(pKeyEvent.getCode()))
	        {
	            return;
	        }
	        if (pKeyEvent.isAltDown() || pKeyEvent.isMetaDown() || pKeyEvent.isShiftDown() || !(pKeyEvent.getSource() instanceof TextArea))
	        {
	            return;
	        }
	        final TextArea textAreaSource = (TextArea) pKeyEvent.getSource();
	        if (pKeyEvent.isControlDown())
	        {
	            if (!aFocusEventText.equalsIgnoreCase(pKeyEvent.getText()))
	            {
	            	pKeyEvent.consume();
	                textAreaSource.replaceSelection("\t");
	            }
	        }
	        else
	        {
	        	pKeyEvent.consume();
	            final KeyEvent tabControlEvent = new KeyEvent(pKeyEvent.getSource(), pKeyEvent.getTarget(), pKeyEvent.getEventType(), 
	            		pKeyEvent.getCharacter(), aFocusEventText, pKeyEvent.getCode(), pKeyEvent.isShiftDown(), true, pKeyEvent.isAltDown(),
	            		pKeyEvent.isMetaDown());
	            textAreaSource.fireEvent(tabControlEvent);
	        }
	    });
	}
}

