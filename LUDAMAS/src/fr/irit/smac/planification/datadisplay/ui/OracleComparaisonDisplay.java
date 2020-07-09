package fr.irit.smac.planification.datadisplay.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class OracleComparaisonDisplay implements Modifiable{
	
	private Stage primaryStage;
	private GridPane gridOracles;
	private GridPane gridResultats;
	private CAV cav;
	private VBox root;
	private Label oraclesLabel;
	private Label resultatsLabel;
	
	/* CONSTANTES */
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	
	public OracleComparaisonDisplay(CAV cav) {
		this.cav = cav;
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
		root.getChildren().addAll(oraclesLabel, gridOracles, resultatsLabel, gridResultats);
		primaryStage.setScene(new Scene(root, 400, 450));
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

	private void buildFirstLigne(int nbCars, int gridType) {
		
		for(int i=1; i<=nbCars; i++) {
			Label labelNumero = new Label(String.valueOf(i));
			buildBoldLabel(labelNumero);
			if(gridType==0) {
				gridOracles.add(labelNumero,  i-1,  0);
			} else {
				gridResultats.add(labelNumero,  i-1,  0);
			}
		}
	}
	
	private void buildLigneUn(List<Float> oracles, int gridType) {
		
		for(int i=0; i<oracles.size(); i++) {
			float oracle = oracles.get(i);
			VBox vbox = new VBox();
			buildCellule(vbox);
			Label labelData = new Label(String.valueOf(oracle));
			vbox.getChildren().add(labelData);
			if(gridType==0) {
				gridOracles.add(vbox, i, 1);
			} else {
				gridResultats.add(vbox,  i, 1);
			}
		}
	}
	
	private void buildLigneVariables(List<List<String>> variables, int gridType) {
		
		for(int i=0; i<variables.size(); i++) {
			List<String> listeVariables = variables.get(i);
			VBox cellule = new VBox();
			buildCellule(cellule);
			cellule.setAlignment(Pos.BASELINE_LEFT);
			for(String variable : listeVariables) {
				Label labelVariable = new Label(variable);
				labelVariable.setPadding(new Insets(0, 5, 5, 5));
				cellule.getChildren().add(labelVariable);
			}
			if(gridType==0) {
				gridOracles.add(cellule, i, 2);
			} else {
				gridResultats.add(cellule, i, 2);
			}
		}
	}
	
	private void buildCellule(VBox box) {

		box.setPrefSize(75, 40);
		box.setAlignment(Pos.CENTER);
		box.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(0.5), null)));
		box.setPadding(new Insets(0, 5, 5, 5));
	}
	
	private void buildBoldLabel(Label label) {
		
		label.setAlignment(Pos.CENTER);
		label.setStyle(BOLDSTYLE);
		label.setPrefSize(75, 40);
		label.setBorder(new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
							BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(1), null)));
	}
	
	public void launchView() {
		
		Thread taskThread = new Thread(new Runnable () {
			
			@Override
			public void run() {
				for(int i=0; i<10; i++) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							/* Destruction des tableaux actuel */
							root.getChildren().removeAll(oraclesLabel, resultatsLabel, gridOracles, gridResultats);
							gridOracles.setVisible(false);
							gridResultats.setVisible(false);
							
							
							/* récupération des données oracle */
							Planing resultsPlaning = cav.getMyPlaning();
							List<Result> results = resultsPlaning.getPlan();
							int nbResults = results.size();
							List<Float> resultsFloat = new ArrayList<>();
							for(Result res : results) {
								resultsFloat.add(res.getValue());
							}
								
							initGrids();
							buildFirstLigne(nbResults, 0);
							buildLigneUn(resultsFloat, 0);
							buildFirstLigne(nbResults, 1);
							buildLigneUn(resultsFloat, 1);
							
							root.getChildren().addAll(oraclesLabel, gridOracles, resultatsLabel, gridResultats);
						}
					});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		taskThread.start();
	}
	
	public void update() {
		//TODO update method
		System.out.println("Update method called from OracleComparaisonDisplay");
	}
}
