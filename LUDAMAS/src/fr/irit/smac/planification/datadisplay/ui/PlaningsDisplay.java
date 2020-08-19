package fr.irit.smac.planification.datadisplay.ui;

import java.util.Collection;
import java.util.List;

import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.datadisplay.interfaces.Modifiable;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class PlaningsDisplay implements Modifiable {

	private CAVModel cavModel;
	private GridPane gridOracles;
	private GridPane gridResultats;
	private ScrollPane scrollPanePlanings;
	private VBox rootPlanings;
	private Label oraclesLabel;
	private Label resultatsLabel;
	/* Nombre de lignes utilisees pour chaque gridpane */
	/* Commencent a 1 en raison de la premiere ligne qui affiche les noms
	 * des proprietes affichees 
	 */
	private int nbLineUsedOracles = 1;
	private int nbLineUsedResults = 1;
	
	/* Constantes */
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	
	public PlaningsDisplay(CAVModel cavModel) {
		this.cavModel = cavModel;
		start();
	}
	
	/* Start
	 * Construction des composants des deux gridpanes et des principaux containeurs de
	 * l'affichage
	 */
	public void start() {
		initGrids();
		buildFirstLigneOracle();
		buildFirstLigneResultats();
		oraclesLabel = new Label("Tableau oracles");
		resultatsLabel = new Label("Tableau résultats");
		resultatsLabel.setPadding(new Insets(10, 0, 0, 0));
		rootPlanings = new VBox();
		rootPlanings.getChildren().addAll(oraclesLabel, gridOracles, resultatsLabel, gridResultats);
		
		StackPane stack = new StackPane();
		stack.getChildren().add(rootPlanings);

		scrollPanePlanings = new ScrollPane();
		scrollPanePlanings.setContent(stack);
		scrollPanePlanings.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPanePlanings.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		stack.minWidthProperty().bind(Bindings.createDoubleBinding(() -> scrollPanePlanings.getViewportBounds().getWidth(),
				scrollPanePlanings.viewportBoundsProperty()));
		
		scrollPanePlanings.setPrefSize(1100, 500);
	}
	
	/*
	 * initGrids
	 * Construction des gridPanes oracles et resultats
	 */
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
	
	/* BuildCellule 
	 * Param in: VBox to build
	 * Sets size, alignment and border of the vbox given
	 */
	private void buildCellule(VBox box) {

		box.setPrefSize(105, 50);
		box.setAlignment(Pos.CENTER);
		box.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(0.5), null)));
	}

	/* BuildBoldLabel
	 * Param in: Label to build
	 * Sets size, bold style, alignment and border of the label given
	 */
	private void buildBoldLabel(Label label) {

		label.setAlignment(Pos.CENTER);
		label.setStyle(BOLDSTYLE);
		label.setPrefSize(105, 70);
		label.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(1), null)));
	}
	
	/*
	 *  BuildResultLine(int step, Result result, Result oracleRes)
	 *  Params in - step: line number - result: result to display - oracleRes: associate true value
	 *  Builds a line in the result grid
	 */
	private void buildResultLine(int step, Result result, Result oracleRes) {

		/* step */
		Label labelStep = new Label(String.valueOf(step));
		buildBoldLabel(labelStep);
		gridResultats.add(labelStep, 0, nbLineUsedResults);

		/* result float */
		VBox vbox = new VBox();
		buildCellule(vbox);
		Label labelData = new Label(
				String.valueOf("SITU: " + result.getValue() + "\nTRUE: " + String.valueOf(oracleRes.getValue())));
		vbox.getChildren().add(labelData);
		gridResultats.add(vbox, 1, nbLineUsedResults);

		/* result variables */
		List<String> variables = result.getDataChosen();
		VBox cellule = new VBox();
		buildCellule(cellule);
		cellule.setPrefSize(833, 50);
		for (String variable : variables) {
			Label labelVariable = new Label("- " + variable);
			labelVariable.setPadding(new Insets(0, 5, 5, 5));
			cellule.getChildren().add(labelVariable);
		}
		gridResultats.add(cellule, 2, nbLineUsedResults);
		nbLineUsedResults++;
	}

	/* BuildFirstLigneResultats
	 * Construit la premiere ligne du tableau des resultats
	 * Proprietes affichees: step/Value/Variables
	 */
	private void buildFirstLigneResultats() {

		VBox vboxNum = new VBox();
		buildCellule(vboxNum);
		Label labelNum = new Label("Step");
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
		labelVariables.setPrefSize(833, 50);
		vboxVariables.getChildren().add(labelVariables);
		gridResultats.add(vboxVariables, 2, 0);
	}

	/* BuildFirstLigneOracle
	 * Construit la premiere ligne du tableau des oracles
	 * Proprietes affichees: value/ variable
	 */
	private void buildFirstLigneOracle() {

		VBox vboxValue = new VBox();
		buildCellule(vboxValue);
		vboxValue.setPrefWidth(833);
		Label labelValue = new Label("Value");
		buildBoldLabel(labelValue);
		labelValue.setPrefWidth(833);
		vboxValue.getChildren().add(labelValue);
		gridOracles.add(vboxValue, 1, 0);

		VBox vboxVariables = new VBox();
		buildCellule(vboxVariables);
		Label labelVariables = new Label("Variable");
		buildBoldLabel(labelVariables);
		labelVariables.setPrefSize(210, 50);
		vboxVariables.getChildren().add(labelVariables);
		gridOracles.add(vboxVariables, 0, 0);
	}

	/*
	 *  BuildOracleLine(int step, float value, String data)
	 *  Params in - step: line number - value: true value to display - data: used variables
	 *  Builds a line in the oracle grid
	 */
	private void buildOracleLine(int step, float value, String data) {

		/* oracle float */
		VBox vbox = new VBox();
		buildCellule(vbox);
		vbox.setPrefWidth(833);
		Label labelData = new Label(String.valueOf(value));
		vbox.getChildren().add(labelData);
		gridOracles.add(vbox, 1, nbLineUsedOracles);

		/* oracle variables */
		VBox vboxVar = new VBox();
		buildCellule(vboxVar);
		vboxVar.setPrefSize(210, 50);
		Label labelVar = new Label(data);
		vboxVar.getChildren().add(labelVar);
		gridOracles.add(vboxVar, 0, nbLineUsedOracles);
		nbLineUsedOracles++;
	}

	/*
	 * updateGrids
	 * Re-build grids with new values (will be called every cycle)
	 */
	public void update() {

		Thread taskThread = new Thread(new Runnable() {

			@Override
			public void run() {
				CAV cav = cavModel.getCav();
				Planing truePlaning = cav.getTruePlaning();
				Planing situationPlaning = cav.getPlaningSituation();
				List<Result> trueResults = truePlaning.getPlan();
				List<Result> situationResults = situationPlaning.getPlan();
				Collection<? extends String> datas = cav.getInputInSituation();
				
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						rootPlanings.getChildren().removeAll(oraclesLabel, resultatsLabel, gridOracles, gridResultats);
						gridOracles.setVisible(false);
						gridResultats.setVisible(false);
						nbLineUsedOracles = 1;
						nbLineUsedResults = 1;
						initGrids();

						buildFirstLigneResultats();

						for (int i = 0; i < situationPlaning.getNbRes(); i++) {
							Result result = situationResults.get(i);
							buildResultLine(i, result, trueResults.get(i));
						}

						buildFirstLigneOracle();
						int step = 0;
						for (String data : datas) {
							buildOracleLine(step, cav.getTrueValueForInput(data), data);
							step++;
						}

						rootPlanings.getChildren().addAll(oraclesLabel, gridOracles, resultatsLabel, gridResultats);
					}
				});
				/* Le travail du thread est termine, on rend un token
				 * a la semaphore du cavModel
				 */
				cavModel.V();
			}
		});
		taskThread.start();
	}
	
	public ScrollPane getScrollPane() {
		return scrollPanePlanings;
	}

}
