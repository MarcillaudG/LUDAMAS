package fr.irit.smac.planification.datadisplay.ui;


import fr.irit.smac.planification.datadisplay.controller.ToolsController;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ToolsDisplay {
	
	private CAVModel cavModel;
	private Stage primaryStage;
	private VBox root;
	private Button oneCycleButton;
	private Button oneStepButton;
	private Button pauseButton;
	private Slider periodSlider;
	private Slider stepSpeed;
	
	public ToolsDisplay(CAVModel cavModel) {
		this.cavModel = cavModel;
		this.primaryStage = new Stage();
		this.primaryStage.getIcons().add(new Image("./fr/irit/smac/img/icon.png"));
		start();
	}
	
	public void start() {
		primaryStage.setTitle("Tools");
		
		root = new VBox();
		root.setAlignment(Pos.BASELINE_CENTER);
	
		HBox hboxButtons = new HBox();
		hboxButtons.setAlignment(Pos.BASELINE_CENTER);
		hboxButtons.setSpacing(20);
		hboxButtons.setPadding(new Insets(0, 0, 20, 0));
		
		pauseButton = new Button("PAUSE");
		pauseButton.setId("pauseID");
		pauseButton.setPrefSize(90, 30);
		pauseButton.setOnAction(new ToolsController(cavModel, this));
		pauseButton.setAlignment(Pos.BASELINE_CENTER);
		
		oneCycleButton = new Button("ONE CYCLE");
		oneCycleButton.setId("oneCycleID");
		oneCycleButton.setPrefSize(90, 30);
		oneCycleButton.setOnAction(new ToolsController(cavModel, this));
		oneCycleButton.setDisable(true);

		oneStepButton = new Button("ONE STEP");
		oneStepButton.setId("oneStepID");
		oneStepButton.setPrefSize(90, 30);
		oneStepButton.setOnAction(new ToolsController(cavModel, this));
		
		hboxButtons.getChildren().addAll(oneCycleButton, oneStepButton);

		Label labelSliderPeriod = new Label("Period between cycles (ms)");
		labelSliderPeriod.setPadding(new Insets(20, 0, 0, 0));
		periodSlider = new Slider();
		periodSlider.setId("periodSliderID");
		periodSlider.setPrefWidth(600);
		periodSlider.setMin(0);
		periodSlider.setMax(1000);
		periodSlider.setValue(1000);
		periodSlider.setBlockIncrement(100);
		periodSlider.setShowTickLabels(true);
		periodSlider.setPadding(new Insets(10, 50, 0, 50));
		periodSlider.setShowTickMarks(true);
		periodSlider.valueProperty().addListener(new ToolsController(cavModel, periodSlider, this));
		
		Label labelStepSpeedSlider = new Label("Period between steps (ms)");
		labelStepSpeedSlider.setPadding(new Insets(20, 0, 0, 0));
		stepSpeed = new Slider();
		stepSpeed.setId("stepSpeedID");
		stepSpeed.setPrefWidth(600);
		stepSpeed.setMin(0);
		stepSpeed.setMax(1000);
		stepSpeed.setValue(0);
		stepSpeed.setBlockIncrement(100);
		stepSpeed.setShowTickLabels(true);
		stepSpeed.setPadding(new Insets(0, 50, 10, 50));
		stepSpeed.setShowTickMarks(true);
		stepSpeed.valueProperty().addListener(new ToolsController(cavModel, periodSlider, this));
		
		root.getChildren().addAll(labelSliderPeriod, periodSlider, labelStepSpeedSlider, stepSpeed, hboxButtons, pauseButton);
		Scene scene = new Scene(root, 400, 300);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		/* Positionnement de la fenetre a l'ouverture en fonction de la taille de l'ecran */
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		double x = bounds.getMinX() + (bounds.getWidth() - scene.getWidth()) * 0.03;
		double y = bounds.getMinY() + (bounds.getHeight() - scene.getHeight()) * 0.03;
		primaryStage.setX(x);
		primaryStage.setY(y);
	}
	
	public Button getOneCycleButton() {
		return oneCycleButton;
	}
}
