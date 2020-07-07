package fr.irit.smac.planification.datadisplayui;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainUI extends Application {

	private Stage primaryStage;
	private File selectedFile;
	private RunController runController;
	private FileChooser fileChooser;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		runController = new RunController();
		runController.setMainApp(this);
		initFrame();
	}

	public void initFrame() {
		
		primaryStage.setTitle("LUDAMAS");
		VBox root = new VBox();
		root.setAlignment(Pos.BASELINE_CENTER);
		fileChooser = new FileChooser();
		fileChooser.setTitle("Open resource file");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));

		Button fileChooserButton = new Button("Choose");
		fileChooserButton.setId("fileChooserID");
		fileChooserButton.setOnAction(runController);


		Button runButton = new Button("RUN");
		runButton.setPadding(new Insets(10, 10, 10, 10));
		runButton.setPrefSize(75, 40);
		runButton.setId("runID");
		runButton.setOnAction(runController);

		root.getChildren().add(fileChooserButton);
		root.getChildren().add(runButton);

		primaryStage.setScene(new Scene(root, 300, 300));
		primaryStage.show();
	}
	
	public File getSelectedFile() {
		return selectedFile;
	}
	
	public FileChooser getFileChooser() {
		return fileChooser;
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public RunController getRunController() {
		return runController;
	}
}
