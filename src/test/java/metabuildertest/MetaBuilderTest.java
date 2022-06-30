package metabuildertest;

import static resources.MetaBuilderResources.RESOURCES;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.*;

import geom.Rectangle;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import ui.EditorFrame;
import ui.GuiUtils;
import ui.SelectableToolButton;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MetaBuilderTest extends ApplicationTest {	
	@Override
	public void start(Stage pStage) throws Exception 
	{
		setStageBoundaries(pStage);

		pStage.setTitle(RESOURCES.getString("application.name"));
		pStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));

		pStage.setScene(new Scene(new EditorFrame(pStage, Optional.empty())));
		pStage.getScene().getStylesheets().add(getClass().getResource("MetaBuilder.css").toExternalForm());

		pStage.setOnCloseRequest(pWindowEvent -> 
		{
			pWindowEvent.consume();
			((EditorFrame)((Stage)pWindowEvent.getSource()).getScene().getRoot()).exit();
		});
		pStage.show();
		pStage.toFront();
	}

    private void setStageBoundaries(Stage pStage)
	{
		Rectangle defaultStageBounds = GuiUtils.defaultStageBounds();
		pStage.setX(defaultStageBounds.getX());
		pStage.setY(defaultStageBounds.getY());
		pStage.setWidth(defaultStageBounds.getWidth());
		pStage.setHeight(defaultStageBounds.getHeight());
	}
	
	/* Just a shortcut to retrieve widgets in the GUI. */
    public <T extends Node> T find(final String query) {
        /* TestFX provides many operations to retrieve elements from the loaded GUI. */
        return lookup(query).query();
    }

//    @BeforeAll
//    public static void setUp() {
//        /* Just retrieving the tested widgets from the GUI. */
//    	createDiagramFromWelcomeTab = find("#createDiagram");
//    }
    
    /* IMO, it is quite recommended to clear the ongoing events, in case of. */
//    @AfterEach
//    public void tearDown() throws TimeoutException {
//        /* Close the window. It will be re-opened at the next test. */
//        FxToolkit.hideStage();
//        release(new KeyCode[] {});
//        release(new MouseButton[] {});
//    }
    
    @Test
    public void testGraphicModeling() {
    	Button createDiagramFromWelcomeTab = find("#createDiagram");
    	clickOn(createDiagramFromWelcomeTab);
    	
    	SelectableToolButton createAbstractClass = find("#createAbstractClass");
    	clickOn(createAbstractClass);
    	clickOn(200, 200);
    	
    	doubleClickOn(200, 200);
    	TextField editNameProperty = find("#nameProperty");
    	sleep(250);
    	clickOn(editNameProperty).write("Person");
    	TextArea editAttriPropertyArea = find("#attributesProperty");
    	sleep(250);
    	clickOn(editAttriPropertyArea).write("name:String;");
    	TextArea editMethodsPropertyArea = find("#methodsProperty");
    	sleep(250);
    	clickOn(editMethodsPropertyArea).write("getName():String;");
    	clickOn("OK");
    	
    	SelectableToolButton createClass = find("#createClass");
    	clickOn(createClass);
    	clickOn(200, 500);
    	
    	doubleClickOn(200, 500);
    	editNameProperty = find("#nameProperty");
    	sleep(250);
    	clickOn(editNameProperty).write("Man");
    	clickOn("OK");
    	
    	SelectableToolButton createGeneralization = find("#createGeneralization");
    	clickOn(createGeneralization);
    	drag(220, 520);
    	dropTo(250, 250);
    	
//    	createClass = find("#createClass");
    	clickOn(createClass);
    	clickOn(600, 200);
    	
    	doubleClickOn(600, 200);
    	editNameProperty = find("#nameProperty");
    	sleep(250);
    	clickOn(editNameProperty).write("Thing");
    	clickOn("OK");
    	
    	SelectableToolButton createAssociation = find("#createAssociation");
    	clickOn(createAssociation);
    	drag(220, 220);
    	dropTo(620, 220);
    	
    	doubleClickOn(420, 220);
    	TextField editMidLabelProperty = find("#midLabelProperty");
    	sleep(250);
    	clickOn(editMidLabelProperty).write("own");
    	TextField editStartLabelProperty = find("#startLabelProperty");
    	sleep(250);
    	clickOn(editStartLabelProperty).write("0..1");
    	TextField editEndLabelProperty = find("#endLabelProperty");
    	sleep(250);
    	clickOn(editEndLabelProperty).write("0..*");
    	clickOn("OK");
    	
    	sleep(5000);
    }
}
