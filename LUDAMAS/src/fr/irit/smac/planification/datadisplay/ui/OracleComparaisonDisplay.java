package fr.irit.smac.planification.datadisplay.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class OracleComparaisonDisplay implements Modifiable {

	private Stage primaryStage;
	private CAVModel cavModel;
	private GridPane gridOracles;
	private GridPane gridResultats;
	private VBox root;
	private Label oraclesLabel;
	private Label resultatsLabel;
	private int nbColumnUsedOracles = 0;
	private int nbColumnUsedResults = 0;

	/* CONSTANTES */
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";

	public OracleComparaisonDisplay(CAVModel cavModel) {
		this.cavModel = cavModel;
		this.primaryStage = new Stage();
		initFrame();
	}

	public void initFrame() {

		primaryStage.setTitle("Comparaison oracles");
		root = new VBox();
		initGrids();
		root.setPadding(new Insets(10, 0, 0, 0));
		oraclesLabel = new Label("Tableau oracles");
		resultatsLabel = new Label("Tableau résultats");
		resultatsLabel.setPadding(new Insets(10, 0, 0, 0));
		
		Button pauseButton = new Button("PAUSE");
		pauseButton.setId("pauseID");
		pauseButton.setPrefSize(120, 70);
		pauseButton.setOnAction(new OracleComparaisonDisplayController(cavModel));
		
		root.getChildren().addAll(oraclesLabel, gridOracles, resultatsLabel, gridResultats);
		
		StackPane stack = new StackPane();
		stack.getChildren().add(root);
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(stack);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		stack.minWidthProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getWidth(),
				scrollPane.viewportBoundsProperty()));

		
		primaryStage.setScene(new Scene(scrollPane, 600, 450));
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

		box.setPrefSize(105, 40);
		box.setAlignment(Pos.CENTER);
		box.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(0.5), null)));
		box.setPadding(new Insets(0, 5, 5, 5));
	}

	private void buildBoldLabel(Label label) {

		label.setAlignment(Pos.CENTER);
		label.setStyle(BOLDSTYLE);
		label.setPrefSize(105, 40);
		label.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(1), null)));
	}
	
	//TODO remove string list parameter (will be accessible from result)
	private void buildColumn(int step, Result result, List<String> variables, int gridType) {
		
		/* first line: step */
		Label labelStep = new Label(String.valueOf(step));
		buildBoldLabel(labelStep);
		if(gridType==0) {
			gridOracles.add(labelStep, nbColumnUsedOracles, 0);
		} else {
			gridResultats.add(labelStep, nbColumnUsedResults, 0);
		}
		
		/* second line: result float */
		VBox vbox = new VBox();
		buildCellule(vbox);
		Label labelData = new Label(String.valueOf(result.getValue()));
		vbox.getChildren().add(labelData);
		if (gridType==0) {
			gridOracles.add(vbox, nbColumnUsedOracles, 1);
		} else {
			gridResultats.add(vbox,nbColumnUsedResults, 1);
		}
		
		/* last line: result variables */
		//List<String> variables = result.getVariables();
		buildVariablesCell(variables, gridType);
	}
	
	private void buildVariablesCell(List<String> variables, int gridType) {
		
		VBox cellule = new VBox();
		buildCellule(cellule);
		cellule.setAlignment(Pos.BASELINE_LEFT);
		for (String variable : variables) {
			Label labelVariable = new Label(variable);
			labelVariable.setPadding(new Insets(0, 5, 5, 5));
			cellule.getChildren().add(labelVariable);
		}
		if (gridType == 0) {
			gridOracles.add(cellule, nbColumnUsedOracles, 2);
			nbColumnUsedOracles++;
		} else {
			gridResultats.add(cellule, nbColumnUsedResults, 2);
			nbColumnUsedResults++;
		}
	}

	public void update() {

		Thread taskThread = new Thread(new Runnable() {

			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						/* Destruction des tableaux actuel */
						root.getChildren().removeAll(oraclesLabel, resultatsLabel, gridOracles, gridResultats);
						gridOracles.setVisible(false);
						gridResultats.setVisible(false);
						nbColumnUsedOracles=0;
						nbColumnUsedResults=0;

						/* OLD ----------------------------------------------------
						CAV cav = cavModel.getCav();
						Planing truePlaning = cav.getTruePlaning();
						Planing situationPlaning = cav.getPlaningSituation();
						List<Result> trueResults = truePlaning.getPlan();
						List<Result> situationResults = situationPlaning.getPlan();
						int nbTrueResults = trueResults.size();
						int nbSituationResults = situationResults.size();
						
						List<Float> trueFloats = new ArrayList<>();
						for (Result res : trueResults) {
							trueFloats.add(res.getValue());
						}
						List<Float> situationFloats = new ArrayList<>();
						for (Result res : situationResults) {
							situationFloats.add(res.getValue());
						}
						
						System.out.println("Variables truePlaning");
						for(String key : truePlaning.getExteroChosen().keySet()) {
							System.out.println(key + " : " + truePlaning.getExteroChosen().get(key));
						}
						
						System.out.println("Variables situationPlaning");
						for(String key : situationPlaning.getExteroChosen().keySet()) {
							System.out.println(key + " : " + situationPlaning.getExteroChosen().get(key));
						}
						*/
						
						/* NEW----------------------------------------------------
						CAV cav = cavModel.getCav();
						List<Result> truePlaning = cav.getTruePlaning();
						List<Result> situationPlaning = cav.getPlaningSituation();
						for(int i=0; i<truePlaning.size(); i++){
							Result trueResult = truePlaning.get(i);
							buildColumn(i, trueResult, 0);
							Result situationResult = situationPlaning.get(i);
							buildColumn(i, situationResult, 1);
						}
						 */
						
						Random rnd = new Random();
						List<String> stringList = new ArrayList<>();
						String var1 = "var1"; stringList.add(var1);
						String var2 = "var2"; stringList.add(var2);
						String var3 = "var3"; stringList.add(var3);
						String var4 = "var4"; stringList.add(var4);
						List<Result> results = new ArrayList<>();
						for(int i=0; i<5; i++) {
							Result res = new Result(i, rnd.nextFloat());
							results.add(res);
						}
						
						//int nbTrueResults = results.size();
						//int nbSituationResults = nbTrueResults;
						initGrids();
						for(int i=0; i< results.size(); i++) {
							buildColumn(i, results.get(i), stringList, 0);
							buildColumn(i, results.get(i), stringList, 1);
						}
						
//						buildFirstLigne(nbTrueResults, 0);
//						buildLigneUn(trueFloats, 0);
//						buildFirstLigne(nbSituationResults, 1);
//						buildLigneUn(situationFloats, 1);

						root.getChildren().addAll(oraclesLabel, gridOracles, resultatsLabel, gridResultats);
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
