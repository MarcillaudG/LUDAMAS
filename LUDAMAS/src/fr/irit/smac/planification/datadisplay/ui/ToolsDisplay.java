package fr.irit.smac.planification.datadisplay.ui;


import fr.irit.smac.planification.datadisplay.controller.ToolsController;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ToolsDisplay {
	
	private CAVModel cavModel;
	private Stage primaryStage;
	private VBox root;
	private Button oneCycleButton;
	private Slider periodSlider;
	private Slider stepSpeed;
	
	public ToolsDisplay(CAVModel cavModel) {
		this.cavModel = cavModel;
		this.primaryStage = new Stage();
		start();
	}
	
	public void start() {
		primaryStage.setTitle("Tools");
		
		root = new VBox();
		VBox vboxButtonPause = new VBox();
		vboxButtonPause.setAlignment(Pos.BASELINE_CENTER);
		vboxButtonPause.setPadding(new Insets(0, 0, 0, 10));
		Button pauseButton = new Button("PAUSE");
		pauseButton.setId("pauseID");
		pauseButton.setPrefSize(90, 50);
		pauseButton.setOnAction(new ToolsController(cavModel));
		vboxButtonPause.getChildren().add(pauseButton);
		
		VBox vboxButtonCycle = new VBox();
		vboxButtonCycle.setPadding(new Insets(0, 0, 0, 10));
		vboxButtonCycle.setAlignment(Pos.BASELINE_CENTER);
		oneCycleButton = new Button("ONE CYCLE");
		oneCycleButton.setId("oneCycleID");
		oneCycleButton.setPrefSize(90, 50);
		oneCycleButton.setOnAction(new ToolsController(cavModel));
		vboxButtonCycle.getChildren().add(oneCycleButton);
		
		VBox vboxButtonStep = new VBox();
		vboxButtonStep.setPadding(new Insets(0, 0, 0, 10));
		vboxButtonStep.setAlignment(Pos.BASELINE_CENTER);
		Button oneStepButton = new Button("ONE STEP");
		oneStepButton.setId("oneStepID");
		oneStepButton.setPrefSize(90, 50);
		oneStepButton.setOnAction(new ToolsController(cavModel));
		vboxButtonStep.getChildren().add(oneStepButton);

		Label labelSliderPeriod = new Label("Select period between two steps (ms):");
		labelSliderPeriod.setPadding(new Insets(20, 0, 0, 60));
		periodSlider = new Slider();
		periodSlider.setId("periodSliderID");
		periodSlider.setPrefWidth(600);
		periodSlider.setMin(0);
		periodSlider.setMax(1000);
		periodSlider.setValue(1000);
		periodSlider.setBlockIncrement(100);
		periodSlider.setShowTickLabels(true);
		periodSlider.setPadding(new Insets(30, 100, 0, 100));
		periodSlider.setShowTickMarks(true);
		periodSlider.valueProperty().addListener(new ToolsController(cavModel, periodSlider));
		
		Label labelStepSpeedSlider = new Label("Select step speed (ms):");
		labelStepSpeedSlider.setPadding(new Insets(20, 0, 0, 60));
		stepSpeed = new Slider();
		stepSpeed.setId("stepSpeedID");
		stepSpeed.setPrefWidth(600);
		stepSpeed.setMin(0);
		stepSpeed.setMax(1000);
		stepSpeed.setValue(0);
		stepSpeed.setBlockIncrement(100);
		stepSpeed.setShowTickLabels(true);
		stepSpeed.setPadding(new Insets(30, 100, 10, 100));
		stepSpeed.setShowTickMarks(true);
		stepSpeed.valueProperty().addListener(new ToolsController(cavModel, periodSlider));
		
		root.getChildren().addAll(labelSliderPeriod, periodSlider, labelStepSpeedSlider, stepSpeed, vboxButtonPause, vboxButtonCycle, vboxButtonStep);
		
		primaryStage.setScene(new Scene(root, 500, 400));
		primaryStage.show();
	}
}
