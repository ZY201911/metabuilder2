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

import com.jfinal.template.stat.Ctrl;

import geom.Rectangle;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
		
		pStage.setFullScreen(true);

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
//    @Order(2)
    public void testUseCase() {
    	int useCase_x = 850;
    	int useCase_y = 250;
    	
    	int classifier_x = 1000;
    	int classifier_y = 80;
    	
    	int behavioredClassifier_x = 750;
    	int behavioredClassifier_y = classifier_y;
    	
    	int actor_x = 700;
    	int actor_y = 180;
    	
    	int extensionPoint_x = 200;
    	int extensionPoint_y = useCase_y;
    	
    	int redefinableElement_x = extensionPoint_x;
    	int redefinableElement_y = 120;
    	
    	int extend_x = (useCase_x + extensionPoint_x)/2;
    	int extend_y = extensionPoint_y + 150;
    	
    	int include_x = useCase_x;
    	int include_y = extend_y;
    	
    	int directedRelationship_x = (extend_x + include_x)/2;
    	int directedRelationship_y = extend_y + 100;
    	
    	int namedElement_x = (extend_x + include_x)/2 + 20;
    	int namedElement_y = extend_y + 220;
    	
    	int constraint_x = extend_x;
    	int constraint_y = extend_y + 150;
    	
    	int padding_x = 20;
    	int padding_y = 10;
    	
    	Button createDiagram = find("#createDiagram");
    	clickOn(createDiagram);
    	
    	SelectableToolButton createClass = find("#createClass");
    	clickOn(createClass);
    	clickOn(useCase_x, useCase_y);
    	
    	doubleClickOn(useCase_x, useCase_y);
    	TextField editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("UseCase");
    	clickOn("OK");

    	clickOn(classifier_x, classifier_y);
    	
    	doubleClickOn(classifier_x, classifier_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Classifier");
    	clickOn("OK");
    	
    	SelectableToolButton createComposition = find("#createComposition");
    	clickOn(createComposition);
    	drag(classifier_x + padding_x, classifier_y + padding_y);
    	dropTo(useCase_x + padding_x, useCase_y + padding_y);
    	
    	doubleClickOn((useCase_x + classifier_x)/2 + 20, (useCase_y + classifier_y)/2 + 20);
    	TextField editStartLabelProperty = find("#startLabelProperty");
    	clickOn(editStartLabelProperty).write("classifier  *");
    	TextField editEndLabelProperty = find("#endLabelProperty");
    	clickOn(editEndLabelProperty).write("ownedUseCase  *");
    	clickOn("OK");
    	
    	SelectableToolButton createAssociation = find("#createAssociation");
//    	clickOn(createAssociation);
//    	drag(useCase_x + padding_x, useCase_y + padding_y);
//    	dropTo(classifier_x + padding_x, classifier_y + padding_y);
//    	
//    	doubleClickOn(useCase_x + 50, (useCase_y + classifier_y)/2 + 20);
//    	editStartLabelProperty = find("#startLabelProperty");
//    	clickOn(editStartLabelProperty).write("useCase  *");
//    	editEndLabelProperty = find("#endLabelProperty");
//    	clickOn(editEndLabelProperty).write("subject  *");
//    	clickOn("OK");
    	
    	clickOn(createClass);
    	clickOn(behavioredClassifier_x, behavioredClassifier_y);
    	
    	doubleClickOn(behavioredClassifier_x, behavioredClassifier_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("BehavioredClassifier");
    	clickOn("OK");
    	
    	clickOn(actor_x, actor_y);
    	
    	doubleClickOn(actor_x, actor_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Actor");
    	clickOn("OK");

    	SelectableToolButton createGeneralization = find("#createGeneralization");
    	clickOn(createGeneralization);
    	drag(useCase_x + padding_x, useCase_y + padding_y);
    	dropTo(behavioredClassifier_x + padding_x, behavioredClassifier_y + padding_y);
    	
    	drag(actor_x + padding_x, actor_y + padding_y);
    	dropTo(behavioredClassifier_x + padding_x, behavioredClassifier_y + padding_y);
    	
    	clickOn(createClass);
    	clickOn(extensionPoint_x, extensionPoint_y);
    	
    	doubleClickOn(extensionPoint_x, extensionPoint_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("ExtensionPoint");
    	clickOn("OK");
    	
    	clickOn(redefinableElement_x, redefinableElement_y);
    	
    	doubleClickOn(redefinableElement_x, redefinableElement_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("RedefinableElement");
    	clickOn("OK");

    	clickOn(createGeneralization);
    	drag(extensionPoint_x + padding_x, extensionPoint_y + padding_y);
    	dropTo(redefinableElement_x + padding_x, redefinableElement_y + padding_y);
    	
    	clickOn(createComposition);
    	drag(useCase_x + padding_x, useCase_y + padding_y);
    	dropTo(extensionPoint_x + padding_x, extensionPoint_y + padding_y);
    	
    	doubleClickOn((useCase_x + extensionPoint_x)/2 + 20, (useCase_y + extensionPoint_y)/2 + 20);
    	editStartLabelProperty = find("#startLabelProperty");
    	clickOn(editStartLabelProperty).write("useCase  1");
    	editEndLabelProperty = find("#endLabelProperty");
    	clickOn(editEndLabelProperty).write("extensionPoint  *");
    	clickOn("OK");
    	
    	clickOn(createClass);
    	clickOn(extend_x, extend_y);
    	
    	doubleClickOn(extend_x, extend_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Extend");
    	clickOn("OK");
    	
//    	clickOn(createComposition);
//    	drag(useCase_x + padding_x, useCase_y + padding_y);
//    	dropTo(extend_x + padding_x, extend_y + padding_y);
//    	
//    	doubleClickOn((useCase_x + extend_x)/2 + 20, (useCase_y + extend_y)/2 + 20);
//    	editStartLabelProperty = find("#startLabelProperty");
//    	clickOn(editStartLabelProperty).write("extension  1");
//    	editEndLabelProperty = find("#endLabelProperty");
//    	clickOn(editEndLabelProperty).write("extend  *");
//    	clickOn("OK");

    	clickOn(createAssociation);
    	drag(extend_x + padding_x, extend_y + padding_y);
    	dropTo(useCase_x + padding_x, useCase_y + padding_y);
    	
    	doubleClickOn((useCase_x + extend_x)/2 + 30, (useCase_y + extend_y)/2 + 20);
    	editStartLabelProperty = find("#startLabelProperty");
    	clickOn(editStartLabelProperty).write("extend  1");
    	editEndLabelProperty = find("#endLabelProperty");
    	clickOn(editEndLabelProperty).write("extendedCase  *");
    	ComboBox<String> editDirection = find("#directionProperty");
    	clickOn(editDirection).clickOn("UniDirection");
    	clickOn("OK");
    	
    	clickOn(createAssociation);
    	drag(extend_x + padding_x, extend_y + padding_y);
    	dropTo(extensionPoint_x + padding_x, extensionPoint_y + padding_y);
    	
    	doubleClickOn((extend_x + extensionPoint_x)/2 + 30, (extend_y + extensionPoint_y)/2 + 20);
    	editStartLabelProperty = find("#startLabelProperty");
    	clickOn(editStartLabelProperty).write("extension  *");
    	editEndLabelProperty = find("#endLabelProperty");
    	clickOn(editEndLabelProperty).write("extensionLocation  1..*");
    	editDirection = find("#directionProperty");
    	clickOn(editDirection).clickOn("UniDirection");
    	clickOn("OK");
    	
    	clickOn(createClass);
    	clickOn(include_x, include_y);
    	
    	doubleClickOn(include_x, include_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Include");
    	clickOn("OK");
    	
    	clickOn(createComposition);
    	drag(useCase_x + padding_x, useCase_y + padding_y);
    	dropTo(include_x + padding_x, include_y + padding_y);
    	
    	doubleClickOn((useCase_x + include_x)/2 + 20, (useCase_y + include_y)/2 + 20);
    	editStartLabelProperty = find("#startLabelProperty");
    	clickOn(editStartLabelProperty).write("includingCase  1");
    	editEndLabelProperty = find("#endLabelProperty");
    	clickOn(editEndLabelProperty).write("include  *");
    	clickOn("OK");
    	
//    	clickOn(createAssociation);
//    	drag(include_x + padding_x, include_y + padding_y);
//    	dropTo(useCase_x + padding_x, useCase_y + padding_y);
//    	
//    	doubleClickOn((include_x + useCase_x)/2 + 30, (include_y + useCase_y)/2 + 20);
//    	editStartLabelProperty = find("#startLabelProperty");
//    	clickOn(editStartLabelProperty).write("include  *");
//    	editEndLabelProperty = find("#endLabelProperty");
//    	clickOn(editEndLabelProperty).write("addition  1");
//    	editDirection = find("#directionProperty");
//    	clickOn(editDirection).clickOn("UniDirection");
//    	clickOn("OK");
    	
    	clickOn(createClass);
    	clickOn(directedRelationship_x, directedRelationship_y);
    	
    	doubleClickOn(directedRelationship_x, directedRelationship_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("DirectedRelationship");
    	clickOn("OK");
    	
    	clickOn(createGeneralization);
    	drag(extend_x + padding_x, extend_y + padding_y);
    	dropTo(directedRelationship_x + padding_x, directedRelationship_y + padding_y);
    	
    	clickOn(createGeneralization);
    	drag(include_x + padding_x, include_y + padding_y);
    	dropTo(directedRelationship_x + padding_x, directedRelationship_y + padding_y);
    	
    	clickOn(createClass);
    	clickOn(namedElement_x, namedElement_y);
    	
    	doubleClickOn(namedElement_x, namedElement_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("NamedElement");
    	clickOn("OK");
    	
    	clickOn(createGeneralization);
    	drag(extend_x + padding_x, extend_y + padding_y);
    	dropTo(namedElement_x + padding_x, namedElement_y + padding_y);
    	
    	clickOn(createGeneralization);
    	drag(include_x + padding_x, include_y + padding_y);
    	dropTo(namedElement_x + padding_x, namedElement_y + padding_y);
    	
    	clickOn(createClass);
    	clickOn(constraint_x, constraint_y);
    	
    	doubleClickOn(constraint_x, constraint_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Constraint");
    	clickOn("OK");
    	
    	clickOn(createComposition);
    	drag(extend_x + padding_x, extend_y + padding_y);
    	dropTo(constraint_x + padding_x, constraint_y + padding_y);
    	
    	doubleClickOn((extend_x + constraint_x)/2 + 20, (extend_y + constraint_y)/2 + 20);
    	editStartLabelProperty = find("#startLabelProperty");
    	clickOn(editStartLabelProperty).write("extend  0..1");
    	editEndLabelProperty = find("#endLabelProperty");
    	clickOn(editEndLabelProperty).write("condition  0..1");
    	clickOn("OK");
    	
    	sleep(8000);
    }
    
//    @Test
//    @Order(1)
    public void testBasicFamily() {
    	int family_x = 100;
    	int family_y = 100;
    	
    	int person_x = 300;
    	int person_y = 300;
    	
    	int man_x = 100;
    	int man_y = 500;
    	
    	int woman_x = 500;
    	int woman_y = 500;
    	
    	int padding_x = 20;
    	int padding_y = 10;
    	
    	Button createDiagram = find("#createDiagram");
    	clickOn(createDiagram);
    	
    	SelectableToolButton createClass = find("#createClass");
    	clickOn(createClass);
    	
    	clickOn(family_x, family_y);
    	doubleClickOn(family_x, family_y);
    	TextField editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Family");
    	TextArea editAttriProperty = find("#attributesProperty");
    	clickOn(editAttriProperty).write("name:EString");
    	clickOn("OK");
    	    	
    	clickOn(man_x, man_y);
    	doubleClickOn(man_x, man_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Man");
    	clickOn("OK");
    	
    	clickOn(woman_x, woman_y);
    	doubleClickOn(woman_x, woman_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Woman");
    	clickOn("OK");
    	
    	SelectableToolButton createAbstrctClass = find("#createAbstractClass");
    	clickOn(createAbstrctClass);
    	
    	clickOn(person_x, person_y);
    	doubleClickOn(person_x, person_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Person");
    	editAttriProperty = find("#attributesProperty");
    	clickOn(editAttriProperty).write("name:EString");
    	clickOn("OK");
    	
    	SelectableToolButton createComposition = find("#createComposition");
    	clickOn(createComposition);
    	drag(family_x + padding_x, family_y + padding_y);
    	dropTo(person_x + padding_x, person_y + padding_y);
    	doubleClickOn((family_x + person_x)/2 + 20, (family_y + person_y)/2 + 10);
    	TextField editMidLabelProperty = find("#midLabelProperty");
    	clickOn(editMidLabelProperty).write("[0..*] members");
    	clickOn("OK");
    	
    	SelectableToolButton createAssociation = find("#createAssociation");
    	clickOn(createAssociation);
    	drag(person_x + padding_x, person_y + padding_y);
    	dropTo(person_x + 2 * padding_x, person_y + 2 * padding_y);
    	doubleClickOn(person_x + 100, person_y - 50);
    	TextField editStartLabelProperty = find("#startLabelProperty");
    	clickOn(editStartLabelProperty).write("[0..2] parents");
    	TextField editEndLabelProperty = find("#endLabelProperty");
    	clickOn(editEndLabelProperty).write("[0..*] children");
    	ComboBox<String> editDirection = find("#directionProperty");
    	clickOn(editDirection).clickOn("BiDirection");
    	clickOn("OK");
    	
    	clickOn(createAssociation);
    	drag(person_x + padding_x, person_y + padding_y);
    	dropTo(man_x + 2 * padding_x, man_y + 2 * padding_y);
    	doubleClickOn((man_x + person_x)/2 + 20, (man_y + person_y)/2 + 10);
    	editMidLabelProperty = find("#midLabelProperty");
    	clickOn(editMidLabelProperty).write("[0..1] /father");
    	editDirection = find("#directionProperty");
    	clickOn(editDirection).clickOn("UniDirection");
    	clickOn("OK");
    	
    	drag(person_x + padding_x, person_y + padding_y);
    	dropTo(woman_x + 2 * padding_x, woman_y + 2 * padding_y);
    	doubleClickOn((woman_x + person_x)/2 + 20, (woman_y + person_y)/2 + 10);
    	editMidLabelProperty = find("#midLabelProperty");
    	clickOn(editMidLabelProperty).write("[0..1] /mother");
    	editDirection = find("#directionProperty");
    	clickOn(editDirection).clickOn("UniDirection");
    	clickOn("OK");
    	
    	SelectableToolButton createGeneralization = find("#createGeneralization");
    	clickOn(createGeneralization);
    	drag(man_x + padding_x, man_y + padding_y);
    	dropTo(person_x + padding_x, person_y + padding_y);
    	
    	drag(woman_x + padding_x, woman_y + padding_y);
    	dropTo(person_x + padding_x, person_y + padding_y);
    	
    	sleep(5000);
    }
    
//    @Test
    public void testXmlEncoder() {
    	int padding_x = 20;
    	int padding_y = 10;
    	
    	int family_x = 100;
    	int family_y = 100;
    	
    	int package1_x = 600;
    	int package1_y = 100;
    	
    	int man_x = package1_x + padding_x;
    	int man_y = package1_y + padding_y;
    	
    	
    	Button createDiagram = find("#createDiagram");
    	clickOn(createDiagram);
    	
    	SelectableToolButton createClass = find("#createClass");
    	clickOn(createClass);
    	
    	clickOn(family_x, family_y);
    	doubleClickOn(family_x, family_y);
    	TextField editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Family");
    	TextArea editAttriProperty = find("#attributesProperty");
    	clickOn(editAttriProperty).write("name:String;\nage:Number;\naddress:String");
    	TextArea editMethodProperty = find("#methodsProperty");
    	clickOn(editMethodProperty).write("family1(name:String,age:Number):void;\nfamily2(name:String,age:Number,address:String):void;");
    	clickOn("OK");
    	
    	SelectableToolButton createPackage = find("#createPackage");
    	clickOn(createPackage);
    	
    	clickOn(package1_x, package1_y);
    	doubleClickOn(package1_x, package1_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Person");
    	clickOn("OK");
    	
    	clickOn(createClass);
    	clickOn(man_x, man_y);
    	doubleClickOn(man_x, man_y);
    	editNameProperty = find("#nameProperty");
    	clickOn(editNameProperty).write("Man");
    	clickOn("OK");
    	
    	press(KeyCode.CONTROL, KeyCode.S);
    	release(KeyCode.S, KeyCode.CONTROL);
    	
    	sleep(5000);
    }
}
