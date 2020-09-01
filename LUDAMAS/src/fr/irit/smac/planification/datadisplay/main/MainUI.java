package fr.irit.smac.planification.datadisplay.main;

import java.io.File;

import fr.irit.smac.planification.datadisplay.controller.RunController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainUI extends Application {

	private Stage primaryStage;
	private File selectedFile;
	private RunController runController;
	private FileChooser fileChooser;
	private FileChooser fileChooserOracle;
	private Spinner<Integer> spinNbEffector;
	private Spinner<Integer> spinNbSituations;
	private Spinner<Integer> spinNbVarEff;
	private Spinner<Integer> spinNbCopy;
	private Label textSelectedFile;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.runController = new RunController();
		this.runController.setMainApp(this);
		this.primaryStage.getIcons().add(new Image("./fr/irit/smac/img/icon.png"));
		initFrame();
	}

	public void initFrame() {

		primaryStage.setTitle("LUDAMAS - Experiment parameters");
		VBox root = new VBox();
		root.setAlignment(Pos.BASELINE_CENTER);
		fileChooser = new FileChooser();
		fileChooser.setTitle("Open resource file");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));

		Button fileChooserButton = new Button("Choose");
		fileChooserButton.setId("fileChooserID");
		fileChooserButton.setOnAction(runController);
		
		fileChooserOracle = new FileChooser();
		fileChooserOracle.setTitle("Open resource oracle file");
		fileChooserOracle.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));

		Button fileChooserOracleButton = new Button("Choose Oracle");
		fileChooserOracleButton.setId("fileChooserOracleID");
		fileChooserOracleButton.setOnAction(runController);
		
		Button runButton = new Button("RUN");
		runButton.setPadding(new Insets(10, 10, 10, 10));
		runButton.setPrefSize(75, 40);
		runButton.setId("runID");
		runButton.setOnAction(runController);

		textSelectedFile = new Label("No data file choosen");
		Separator separator = new Separator(Orientation.HORIZONTAL);
		separator.setPadding(new Insets(15, 0, 10, 0));

		spinNbEffector = new Spinner<>();
		SpinnerValueFactory<Integer> valueFactoryEffector = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1);
		spinNbEffector.setValueFactory(valueFactoryEffector);

		spinNbSituations = new Spinner<>();
		SpinnerValueFactory<Integer> valueFactorySituations = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5,
				1);
		spinNbSituations.setValueFactory(valueFactorySituations);

		spinNbVarEff = new Spinner<>();
		SpinnerValueFactory<Integer> valueFactoryVarEff = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3);
		spinNbVarEff.setValueFactory(valueFactoryVarEff);

		spinNbCopy = new Spinner<>();
		SpinnerValueFactory<Integer> valueFactoryCopy = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3);
		spinNbCopy.setValueFactory(valueFactoryCopy);

		Label labelNbEffector = new Label("Nombre d'effecteurs");
		Label labelNbSituations = new Label("Nombre de situations");
		Label labelNbVarEff = new Label("Nombre Var Eff");
		Label labelNbCopy = new Label("Nombre de copies");

		root.getChildren().addAll(textSelectedFile, fileChooserButton, fileChooserOracleButton, separator);
		root.getChildren().addAll(labelNbEffector, spinNbEffector, labelNbSituations, spinNbSituations, labelNbVarEff,
				spinNbVarEff, labelNbCopy, spinNbCopy);
		root.getChildren().add(runButton);

		primaryStage.setScene(new Scene(root, 270, 320));
		primaryStage.show();
	}

	public File getSelectedFile() {
		return selectedFile;
	}

	public FileChooser getFileChooser() {
		return fileChooser;
	}
	
	public FileChooser getFileChooserOracle() {
		return fileChooserOracle;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public RunController getRunController() {
		return runController;
	}

	public Integer getValueSpinEffector() {
		return spinNbEffector.getValue();
	}

	public Integer getValueSpinSituations() {
		return spinNbSituations.getValue();
	}

	public Integer getValueSpinVarEff() {
		return spinNbVarEff.getValue();
	}

	public Integer getValueSpinCopy() {
		return spinNbCopy.getValue();
	}

	public Label getTextSelectedLabel() {
		return textSelectedFile;
	}

}
