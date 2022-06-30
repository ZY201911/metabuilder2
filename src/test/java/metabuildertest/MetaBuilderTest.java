package metabuildertest;

import static resources.MetaBuilderResources.RESOURCES;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.*;

import geom.Rectangle;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import ui.EditorFrame;
import ui.GuiUtils;

public class MetaBuilderTest extends ApplicationTest {
	Button createDiagramFromWelcomeTab;
	
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
    public void testCreationDiagramFromWelcomeTab() {
    	createDiagramFromWelcomeTab = find("#createDiagram");
    	clickOn(createDiagramFromWelcomeTab);
    }
}
