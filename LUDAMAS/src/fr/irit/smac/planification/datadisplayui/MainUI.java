package fr.irit.smac.planification.datadisplayui;


import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainUI extends Application{
	
	private Stage primaryStage;
	private TextArea textArea;
	private File selectedFile;
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage)  {
		this.primaryStage = primaryStage;
		initFrame();
	}
	
	public void initFrame() {
		primaryStage.setTitle("LUDAMAS");
		VBox root = new VBox();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open resource file");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
		
		Button fileChooserButton = new Button("Choose");
		fileChooserButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				selectedFile = fileChooser.showOpenDialog(primaryStage);
			}
		});
		
		root.getChildren().add(fileChooserButton);
		
		
		primaryStage.setScene(new Scene(root, 300, 300));
		primaryStage.show();
	}
}
