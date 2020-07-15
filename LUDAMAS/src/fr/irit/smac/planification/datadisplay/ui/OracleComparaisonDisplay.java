package fr.irit.smac.planification.datadisplay.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.List;

import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.datadisplay.controller.OracleComparaisonDisplayController;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class OracleComparaisonDisplay implements Modifiable{

	private Stage primaryStage;
	private CAVModel cavModel;
	private GridPane gridOracles;
	private GridPane gridResultats;
	private VBox root;
	private Label oraclesLabel;
	private Label resultatsLabel;
	private int nbColumnUsedOracles = 1;
	private int nbColumnUsedResults = 1;

	/* CONSTANTES */
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";

	public OracleComparaisonDisplay(CAVModel cavModel) {
		this.cavModel = cavModel;
		this.primaryStage = new Stage();
		start();
	}

	public void start() {

		primaryStage.setTitle("Planings comparaison");
		root = new VBox();
		initGrids();
		//buildFirstLigneOracle();
		buildFirstLigneResultats();
		oraclesLabel = new Label("Tableau oracles");
		resultatsLabel = new Label("Tableau résultats");
		resultatsLabel.setPadding(new Insets(10, 0, 0, 0));
		
		VBox vboxButton = new VBox();
		vboxButton.setAlignment(Pos.BASELINE_CENTER);
		Button pauseButton = new Button("PAUSE");
		pauseButton.setId("pauseID");
		pauseButton.setPrefSize(90, 60);
		pauseButton.setOnAction(new OracleComparaisonDisplayController(cavModel));
		vboxButton.getChildren().add(pauseButton);
		
		Label labelSliderPeriod = new Label("Select period between two steps:");
		labelSliderPeriod.setPadding(new Insets(20, 0, 0, 60));
		Slider periodSlider = new Slider();
		periodSlider.setId("periodSliderID");
		periodSlider.setPrefWidth(600);
		periodSlider.setMin(0);
		periodSlider.setMax(1000);
		periodSlider.setValue(1000);
		periodSlider.setBlockIncrement(100);
		periodSlider.setShowTickLabels(true);
		periodSlider.setPadding(new Insets(30, 60, 0, 60));
		periodSlider.setShowTickMarks(true);
		periodSlider.valueProperty().addListener(new OracleComparaisonDisplayController(cavModel));
		
		Label labelStepSpeedSlider = new Label("Select step speed:");
		labelStepSpeedSlider.setPadding(new Insets(20, 0, 0, 60));
		Slider stepSpeed = new Slider();
		stepSpeed.setId("stepSpeedID");
		stepSpeed.setPrefWidth(600);
		stepSpeed.setMin(0);
		stepSpeed.setMax(1000);
		stepSpeed.setValue(0);
		stepSpeed.setBlockIncrement(100);
		stepSpeed.setShowTickLabels(true);
		stepSpeed.setPadding(new Insets(30, 60, 10, 60));
		stepSpeed.setShowTickMarks(true);
		//stepSpeed.valueProperty().addListener(new OracleComparaisonDisplayController(cavModel));
		
		root.getChildren().addAll(labelSliderPeriod, periodSlider, labelStepSpeedSlider, stepSpeed, vboxButton, resultatsLabel, gridResultats, oraclesLabel, gridOracles);
		
		
		
		StackPane stack = new StackPane();
		stack.getChildren().addAll(root);
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(stack);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		stack.minWidthProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getWidth(),
				scrollPane.viewportBoundsProperty()));

		primaryStage.setScene(new Scene(scrollPane, 1070, 700));
		primaryStage.show();
	}

	private void initGrids() {

		GridPane newGridOracles = new GridPane();
		newGridOracles.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
				null, new BorderWidths(0.5), null)));
		newGridOracles.setPadding(new Insets(20, 20, 20, 20));

		GridPane newGridResultats = new GridPane();
		newGridResultats.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
				null, new BorderWidths(0.5), null)));
		newGridResultats.setPadding(new Insets(20, 20, 20, 20));

		gridOracles = newGridOracles;
		gridResultats = newGridResultats;
	}

	private void buildCellule(VBox box) {

		box.setPrefSize(105, 50);
		box.setAlignment(Pos.CENTER);
		box.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(0.5), null)));
		//box.setPadding(new Insets(0, 5, 5, 5));
	}

	private void buildBoldLabel(Label label) {

		label.setAlignment(Pos.CENTER);
		label.setStyle(BOLDSTYLE);
		label.setPrefSize(105, 70);
		label.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(1), null)));
	}
	

	private void buildColumn(int step, Result result, Result oracleRes, int gridType) {
		
		/* step*/
		Label labelStep = new Label(String.valueOf(step));
		buildBoldLabel(labelStep);
		if(gridType==0) {
			gridOracles.add(labelStep, 0, nbColumnUsedOracles);
		} else {
			gridResultats.add(labelStep, 0, nbColumnUsedResults);
		}
		
		/* result float */
		VBox vbox = new VBox();
		buildCellule(vbox);
		Label labelData = new Label(String.valueOf("SITU: " + result.getValue() + "\nTRUE: " + String.valueOf(oracleRes.getValue())));
		vbox.getChildren().add(labelData);
		if (gridType==0) {
			gridOracles.add(vbox, 1, nbColumnUsedOracles);
		} else {
			gridResultats.add(vbox, 1, nbColumnUsedResults);
		}
		
		/* result variables */
		List<String> variables = result.getDataChosen();
		buildVariablesCell(variables, gridType);
	}
	
	private void buildVariablesCell(List<String> variables, int gridType) {
		
		VBox cellule = new VBox();
		buildCellule(cellule);
		cellule.setPrefSize(800, 50);
		for (String variable : variables) {
			Label labelVariable = new Label("- " + variable);
			labelVariable.setPadding(new Insets(0, 5, 5, 5));
			cellule.getChildren().add(labelVariable);
		}
		if (gridType == 0) {
			gridOracles.add(cellule, 2, nbColumnUsedOracles);
			nbColumnUsedOracles++;
		} else {
			gridResultats.add(cellule, 2, nbColumnUsedResults);
			nbColumnUsedResults++;
		}
	}
	
	private void buildFirstLigneResultats() {
		
		VBox vboxNum = new VBox();
		buildCellule(vboxNum);
		Label labelNum = new Label("Number");
		buildBoldLabel(labelNum);
		vboxNum.getChildren().add(labelNum);
		gridResultats.add(vboxNum, 0, 0);
		
		VBox vboxValue = new VBox();
		buildCellule(vboxValue);
		Label labelValue = new Label("Value");
		buildBoldLabel(labelValue);
		vboxValue.getChildren().add(labelValue);
		gridResultats.add(vboxValue, 1, 0);
		
		VBox vboxVariables = new VBox();
		buildCellule(vboxValue);
	
		Label labelVariables = new Label("Variables");
		buildBoldLabel(labelVariables);
		labelVariables.setPrefSize(800, 50);
		vboxVariables.getChildren().add(labelVariables);
		gridResultats.add(vboxVariables, 2, 0);
	}
	
//	private void buildFirstLigneOracle() {
//		
//		VBox vboxNum = new VBox();
//		buildCellule(vboxNum);
//		Label labelNum = new Label("Number");
//		buildBoldLabel(labelNum);
//		vboxNum.getChildren().add(labelNum);
//		gridOracles.add(vboxNum, 0, 0);
//		
//		VBox vboxValue = new VBox();
//		buildCellule(vboxValue);
//		Label labelValue = new Label("Value");
//		buildBoldLabel(labelValue);
//		vboxValue.getChildren().add(labelValue);
//		gridOracles.add(vboxValue, 1, 0);
//	}

	public void update() {

		Thread taskThread = new Thread(new Runnable() {

			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {

						root.getChildren().removeAll(oraclesLabel, resultatsLabel, gridOracles, gridResultats);
						gridOracles.setVisible(false);
						gridResultats.setVisible(false);
						nbColumnUsedOracles=1;
						nbColumnUsedResults=1;
						initGrids();
						buildFirstLigneResultats();
						
						CAV cav = cavModel.getCav();
						Planing truePlaning = cav.getTruePlaning();
						Planing situationPlaning = cav.getPlaningSituation();
						List<Result> trueResults = truePlaning.getPlan();
						List<Result> situationResults = situationPlaning.getPlan();
						
						
						for(int i=0; i<situationPlaning.getNbRes(); i++) {
							Result result = situationResults.get(i);
							buildColumn(i, result, trueResults.get(i), 1);
						}

						root.getChildren().addAll(resultatsLabel, gridResultats, oraclesLabel, gridOracles);
					}
				});
			}
		});
		taskThread.start();
	}

	public void setCavModel(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	public CAVModel getCavModel() {
		return cavModel;
	}
	
}
