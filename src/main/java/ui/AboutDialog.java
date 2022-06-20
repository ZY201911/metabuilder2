package ui;

import static resources.MetaBuilderResources.RESOURCES;

import metabuilder.MetaBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A modal dialog that provides information about JetUML.
 */
public class AboutDialog
{
	private final Stage aStage = new Stage();
	
	/**
	 * Creates a new dialog.
	 * 
	 * @param pOwner The stage that owns this stage.
	 */
	public AboutDialog( Stage pOwner )
	{
		prepareStage(pOwner);
		aStage.setScene(createScene());
	}
	
	private void prepareStage(Stage pOwner) 
	{
		aStage.setResizable(false);
		aStage.initModality(Modality.WINDOW_MODAL);
		aStage.initOwner(pOwner);
		aStage.setTitle(String.format("%s %s", RESOURCES.getString("dialog.about.title"),
				RESOURCES.getString("application.name")));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
	}
	
	private Scene createScene() 
	{
		final int verticalSpacing = 5;
		
		VBox info = new VBox(verticalSpacing);
		Text name = new Text(RESOURCES.getString("application.name"));
		name.setStyle("-fx-font-size: 18pt;");
		
		Text version = new Text(String.format("%s %s", RESOURCES.getString("dialog.about.version"), 
				MetaBuilder.VERSION));
		
		Text copyright = new Text(RESOURCES.getString("application.copyright"));
		
		Text license = new Text(RESOURCES.getString("dialog.about.license"));
		
		Text quotes = new Text(RESOURCES.getString("quotes.copyright"));
		
		Hyperlink link = new Hyperlink(RESOURCES.getString("dialog.about.link"));
		link.setBorder(Border.EMPTY);
		link.setPadding(new Insets(0));
		link.setOnMouseClicked(e -> MetaBuilder.openBrowser(RESOURCES.getString("dialog.about.url")));
		link.setUnderline(true);
		link.setFocusTraversable(false);
		
		info.getChildren().addAll(name, version, copyright, license, link, quotes);
		
		final int padding = 15;
		HBox layout = new HBox(padding);
		layout.setStyle("-fx-background-color: gainsboro;");
		layout.setPadding(new Insets(padding));
		layout.setAlignment(Pos.CENTER_LEFT);
		
		ImageView logo = new ImageView(RESOURCES.getString("application.icon"));
		logo.setEffect(new BoxBlur());
		layout.getChildren().addAll(logo, info);
		layout.setAlignment(Pos.TOP_CENTER);
		
		aStage.requestFocus();
		aStage.addEventHandler(KeyEvent.KEY_PRESSED, pEvent -> 
		{
			if (pEvent.getCode() == KeyCode.ENTER) 
			{
				aStage.close();
			}
		});
		
		return new Scene(layout);
	}
	
	/**
	 * Shows the dialog and blocks the remainder of the UI
	 * until it is closed.
	 */
	public void show() 
	{
        aStage.showAndWait();
    }
}