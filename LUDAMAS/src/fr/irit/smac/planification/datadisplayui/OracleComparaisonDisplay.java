package fr.irit.smac.planification.datadisplayui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

import fr.irit.smac.planification.system.CAV;
import javafx.application.Application;
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

public class OracleComparaisonDisplay {
	
	private Stage primaryStage;
	private GridPane gridOracles;
	private GridPane gridResultats;
	private CAV cav;
	
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	
	public static void main(String[] args) {
		Application.launch(args);
		
		/* DEBUT TESTS */
		List<Float> oracles = new ArrayList<>();
		oracles.add(20.0F);
		oracles.add(1.0F);
		oracles.add(55.3F);
		oracles.add(25.1F);
		oracles.add(0.0F);
		String variable1 = "var1";
		String variable2 = "var2";
		String variable3 = "var3";
		List<List<String>> variables = new ArrayList<>();
		for(int i=0; i<5; i++) {
			List<String> listeVariables = new ArrayList<>();
			listeVariables.add(variable1);
			listeVariables.add(variable2);
			listeVariables.add(variable3);
			variables.add(listeVariables);
		}

		//launchView(5, oracles, oracles, variables, variables);
		/* FIN TESTS */
	}
	
	public OracleComparaisonDisplay(CAV cav) {
		this.cav = cav;
		this.primaryStage = new Stage();
		initFrame();
	}
	
	public void initFrame() {
		primaryStage.setTitle("Comparaison oracles");
		
		
		VBox root = new VBox();
		gridOracles = new GridPane();
		gridOracles.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
				null, new BorderWidths(0.5), null)));
		gridOracles.setPadding(new Insets(20, 20, 20, 20));
		
		gridResultats = new GridPane();
		gridResultats.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
				null, new BorderWidths(0.5), null)));
		gridResultats.setPadding(new Insets(20, 20, 20, 20));


		root.setPadding(new Insets(10, 0, 0, 0));
		root.getChildren().add(gridOracles);
		root.getChildren().add(gridResultats);
		primaryStage.setScene(new Scene(root, 400, 450));
		primaryStage.show();
		
	}

	public void buildFirstLigne(int nbCars, int gridType) {
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
	
	public void buildLigneUn(List<Float> oracles, int gridType) {
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
	
	public void buildLigneVariables(List<List<String>> variables, int gridType) {
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
	
	public void initGrids() {
		
	}
	
	public void launchView(int nbCars, List<Float> oracles, List<Float> resultats, List<List<String>> variablesOracles, List<List<String>> variablesResultats) {
		Thread taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						buildFirstLigne(nbCars, 0);
						buildLigneUn(oracles, 0);
						buildLigneVariables(variablesOracles, 0);
						
						buildFirstLigne(nbCars, 1);
						buildLigneUn(resultats, 1);
						buildLigneVariables(variablesResultats, 1);
					}
				});
			}
		});
		
		taskThread.start();
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
	
	public CAV getCav() {
		return cav;
	}
}
