package fr.irit.smac.planification.datadisplay.main;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.datadisplay.controller.AgentDisplayChoiceController;
import fr.irit.smac.planification.datadisplay.controller.ChartDisplayController;
import fr.irit.smac.planification.datadisplay.interfaces.Modifiable;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.CoalitionAgentDisplay;
import fr.irit.smac.planification.datadisplay.ui.DataAgentDisplay;
import fr.irit.smac.planification.datadisplay.ui.HoveredChartData;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CentralPanelV2 implements Modifiable{

	/* UI */
	private Stage primaryStage;
	private CAVModel cavModel;
	
	/* TitledPanes */
	TitledPane titledDataAgent;
	TitledPane titledCoalitionAgent;
	TitledPane titledCharts;
	TitledPane titledPlanings;
	
	/* Planings display */
	private GridPane gridOracles;
	private GridPane gridResultats;
	private VBox root;
	private VBox rootPlanings;
	private Label oraclesLabel;
	private Label resultatsLabel;
	private int nbLineUsedOracles = 1;
	private int nbLineUsedResults = 1;

	/* Display choice (DataAgent + CoalitionAgent) */
	private HBox rootAgentChoice;
	private Separator separator;

	/* MeanDiff + MaxDiff chart displays */
	private VBox rootCharts;
	private Series<Number, Number> seriesMeanDiff;
	private Series<Number, Number> seriesMaxDiff;
	private Label labelBorneSupp = new Label("Superior bound (step)");
	private Label labelBorneInf = new Label("Inferior bound (step)");
	private Slider borneInfSlider;
	private Slider borneSupSlider;
	private List<XYChart.Data<Number, Number>> allMeanData = new ArrayList<>();
	private List<XYChart.Data<Number, Number>> allMaxData = new ArrayList<>();
	private LineChart<Number, Number> lineChartMeanDiff;
	private LineChart<Number, Number> lineChartMaxDiff;
	private int borneInf = 0;
	private int borneSup = 0;
	private NumberAxis xAxisMeanDiff = new NumberAxis();
	private NumberAxis yAxisMeanDiff = new NumberAxis();
	private NumberAxis xAxisMaxDiff = new NumberAxis();
	private NumberAxis yAxisMaxDiff = new NumberAxis();

	/* Constants */
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	
	public CentralPanelV2(CAVModel cavModel) {
		this.cavModel = cavModel;
		this.primaryStage = new Stage();
		this.primaryStage.getIcons().add(new Image("./fr/irit/smac/img/icon.png"));
		start();
	}

	public void start() {

		/* Header build */
		GridPane start = new GridPane();
		start.setPadding(new Insets(15, 0, 0, 0));
		Class<?> clazz = this.getClass();
		InputStream input = clazz.getResourceAsStream("/fr/irit/smac/img/luda.jpg");
		Image image = new Image(input);
		ImageView imageView = new ImageView(image);
		imageView.setPreserveRatio(true);
		imageView.setFitWidth(300);
		imageView.setFitHeight(275);

		start.setAlignment(Pos.BASELINE_CENTER);
		Label ludamasLabel = new Label("LUDAMAS");
		ludamasLabel.setPadding(new Insets(0, 0, 0, 15));
		ludamasLabel.setFont(new Font("Verdana", 60));
		Separator sepStart = new Separator(Orientation.HORIZONTAL);
		sepStart.setPadding(new Insets(20, 0, 0, 0));
		start.add(imageView, 0, 0);
		start.add(ludamasLabel, 1, 0);

		/* Components build */
		startAgentDisplayChoice();
		startCharts();
		startPlaningGrids();
		
		titledCharts = new TitledPane("Charts", rootCharts);
		titledCharts.setExpanded(false);
		titledPlanings = new TitledPane("Planings", rootPlanings);
		titledPlanings.setExpanded(false);
		
		DataAgentDisplay dataAgentDisplay = new DataAgentDisplay(cavModel);
		cavModel.addModifiables(dataAgentDisplay);
		titledDataAgent = new TitledPane("Data Agents", dataAgentDisplay.getScrollPane());
		titledDataAgent.setExpanded(false);
		
		CoalitionAgentDisplay coalitionDisplay = new CoalitionAgentDisplay(cavModel);
		cavModel.addModifiables(coalitionDisplay);
		titledCoalitionAgent = new TitledPane("Coalition Agents", coalitionDisplay.getScrollPane());
		titledCoalitionAgent.setExpanded(false);

		root = new VBox();
		root.getChildren().addAll(start, sepStart, rootAgentChoice, separator,titledDataAgent, titledCoalitionAgent, titledCharts, titledPlanings);

		/* Scene and main container build */
		ScrollPane scrollRoot = new ScrollPane();
		scrollRoot.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollRoot.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollRoot.setContent(root);
		primaryStage.setTitle("LUDAMAS - Main panel");
		Scene scene = new Scene(scrollRoot, 1115, 900);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/*
	 * StartAgentDisplayChoice
	 * Creating buttons to display DataAgents and CoalitionAgents
	 */
	private void startAgentDisplayChoice() {

		rootAgentChoice = new HBox();
		rootAgentChoice.setSpacing(20);
		rootAgentChoice.setAlignment(Pos.BASELINE_CENTER);
		rootAgentChoice.setPadding(new Insets(15, 0, 0, 0));

		Button agentTypeOne = new Button();
		agentTypeOne.setText("DataAgents");
		agentTypeOne.setId("dataDisplayID");
		agentTypeOne.setPrefSize(120, 70);
		agentTypeOne.setOnAction(new AgentDisplayChoiceController(cavModel));

		Button agentTypeTwo = new Button();
		agentTypeTwo.setText("CoalitionAgents");
		agentTypeTwo.setId("coalitionAgentDisplayID");
		agentTypeTwo.setPrefSize(120, 70);
		agentTypeTwo.setOnAction(new AgentDisplayChoiceController(cavModel));

		separator = new Separator(Orientation.HORIZONTAL);
		separator.setPadding(new Insets(15, 0, 0, 0));
		rootAgentChoice.getChildren().addAll(agentTypeOne, agentTypeTwo);
	}
	
	/*
	 * StartCharts
	 * Creating charts to display MeanDiff and MaxDiff from planings every cycle
	 */
	private void startCharts() {

		/* SLIDERS BOUNDS */
		labelBorneInf.setPadding(new Insets(0, 0, 0, 30));
		labelBorneSupp.setPadding(new Insets(0, 0, 0, 30));
		borneInfSlider = new Slider();
		borneInfSlider.setId("infBoundID");
		borneInfSlider.setPrefWidth(600);
		borneInfSlider.setMin(0);
		borneInfSlider.setMax(cavModel.getCycle());
		borneInfSlider.setValue(0);
		borneInfSlider.setBlockIncrement(5);
		borneInfSlider.setShowTickLabels(true);
		borneInfSlider.setPadding(new Insets(30, 100, 0, 100));
		borneInfSlider.setShowTickMarks(true);
		borneInfSlider.valueProperty().addListener(new ChartDisplayController(cavModel, borneInfSlider, this));

		borneSupSlider = new Slider();
		borneSupSlider.setId("supBoundID");
		borneSupSlider.setPrefWidth(600);
		borneSupSlider.setMin(0);
		borneSupSlider.setMax(cavModel.getCycle());
		borneSupSlider.setValue(0);
		borneSupSlider.setBlockIncrement(5);
		borneSupSlider.setShowTickLabels(true);
		borneSupSlider.setPadding(new Insets(30, 100, 0, 100));
		borneSupSlider.setShowTickMarks(true);
		borneSupSlider.valueProperty().addListener(new ChartDisplayController(cavModel, borneSupSlider, this));

		/* MEAN DIFF CHART */
		rootCharts = new VBox();
		yAxisMeanDiff.setAutoRanging(true);

		xAxisMeanDiff.setLabel("Cycle");
		yAxisMeanDiff.setLabel("MeanDiff");
		lineChartMeanDiff = new LineChart<>(xAxisMeanDiff, yAxisMeanDiff);
		seriesMeanDiff = new XYChart.Series<>();
		seriesMeanDiff.setName("MeanDiff");
		lineChartMeanDiff.getData().add(seriesMeanDiff);

		/* MAX DIFF CHART */
		yAxisMaxDiff.setAutoRanging(true);
		xAxisMaxDiff.setLabel("Cycle");
		yAxisMaxDiff.setLabel("MaxDiff");
		lineChartMaxDiff = new LineChart<>(xAxisMaxDiff, yAxisMaxDiff);
		seriesMaxDiff = new XYChart.Series<>();
		seriesMaxDiff.setName("MaxDiff");
		lineChartMaxDiff.getData().add(seriesMaxDiff);

		rootCharts.getChildren().addAll(lineChartMeanDiff, lineChartMaxDiff, labelBorneInf, borneInfSlider,
				labelBorneSupp, borneSupSlider);
	}

	/* StartPlaningGrids 
	 * Creating grids to display planings data
	 */
	private void startPlaningGrids() {

		initGrids();
		buildFirstLigneOracle();
		buildFirstLigneResultats();
		oraclesLabel = new Label("Tableau oracles");
		resultatsLabel = new Label("Tableau résultats");
		resultatsLabel.setPadding(new Insets(10, 0, 0, 0));
		rootPlanings = new VBox();
		rootPlanings.getChildren().addAll(oraclesLabel, gridOracles, resultatsLabel, gridResultats);
	}

	/*
	 * initGrids
	 * init grids attributes like border
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
		cellule.setPrefSize(700, 50);
		for (String variable : variables) {
			Label labelVariable = new Label("- " + variable);
			labelVariable.setPadding(new Insets(0, 5, 5, 5));
			cellule.getChildren().add(labelVariable);
		}
		gridResultats.add(cellule, 2, nbLineUsedResults);
		nbLineUsedResults++;
	}

	/* BuildFirstLigneResultats
	 * is part of grid init, puts labels in result grid
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
		labelVariables.setPrefSize(700, 50);
		vboxVariables.getChildren().add(labelVariables);
		gridResultats.add(vboxVariables, 2, 0);
	}

	/* BuildFirstLigneOracle
	 * is part of grid init, puts labels in true grid
	 */
	private void buildFirstLigneOracle() {

		VBox vboxValue = new VBox();
		buildCellule(vboxValue);
		vboxValue.setPrefWidth(700);
		Label labelValue = new Label("Value");
		buildBoldLabel(labelValue);
		labelValue.setPrefWidth(700);
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
		vbox.setPrefWidth(700);
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
	public void updateGrids() {

		Thread taskThread = new Thread(new Runnable() {

			@Override
			public void run() {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {

						rootPlanings.getChildren().removeAll(oraclesLabel, resultatsLabel, gridOracles, gridResultats);
						gridOracles.setVisible(false);
						gridResultats.setVisible(false);
						nbLineUsedOracles = 1;
						nbLineUsedResults = 1;
						initGrids();

						/* Resultats 
						 * - Recupere les resultats du planing situation depuis le CAVModel
						 */
						buildFirstLigneResultats();
						CAV cav = cavModel.getCav();
						Planing truePlaning = cav.getTruePlaning();
						Planing situationPlaning = cav.getPlaningSituation();
						List<Result> trueResults = truePlaning.getPlan();
						List<Result> situationResults = situationPlaning.getPlan();

						for (int i = 0; i < situationPlaning.getNbRes(); i++) {
							Result result = situationResults.get(i);
							buildResultLine(i, result, trueResults.get(i));
						}

						/* Oracles 
						 * - Recupere les resultats attendus depuis le CAV
						 */
						buildFirstLigneOracle();
						Collection<? extends String> datas = cav.getInputInSituation();
						int step = 0;
						for (String data : datas) {
							buildOracleLine(step, cav.getTrueValueForInput(data), data);
							step++;
						}

						rootPlanings.getChildren().addAll(oraclesLabel, gridOracles, resultatsLabel, gridResultats);
					}
				});
			}
		});
		taskThread.start();
	}
	
	/*
	 * UpdateCharts
	 * Add new values to chart depending on MeanDiff and MaxDiff from cav
	 */
	public void updateCharts() {

		Thread taskThread = new Thread(new Runnable() {

			@Override
			public void run() {

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						CAV cav = cavModel.getCav();
						int cycle = cavModel.getCycle();
						Planing truePlaning = cav.getTruePlaning();
						Planing situationPlaning = cav.getPlaningSituation();
						float meanDiff = truePlaning.computeMeanDifference(situationPlaning);
						float maxDiff = truePlaning.computeMaxDifference(situationPlaning);

						XYChart.Data<Number, Number> newMeanData = new XYChart.Data<>(cycle, meanDiff);
						XYChart.Data<Number, Number> newMaxData = new XYChart.Data<>(cycle, maxDiff);
						newMeanData.setNode(new HoveredChartData(cavModel, meanDiff));
						newMaxData.setNode(new HoveredChartData(cavModel, maxDiff));

						allMeanData.add(newMeanData);
						allMaxData.add(newMaxData);
						
						System.out.println("borneSup = " + borneSup + " sliderSupValue = " + borneSupSlider.getValue());
						if (borneSup == borneSupSlider.getValue()) {
							seriesMeanDiff.getData().add(newMeanData);
							seriesMaxDiff.getData().add(newMaxData);
						}
						borneInfSlider.setMax(borneInfSlider.getMax() + 1);
						borneSupSlider.setMax(borneSupSlider.getMax() + 1);
						if(borneSup == borneSupSlider.getValue()) {
							borneSupSlider.setValue(borneSupSlider.getMax()+1);
						}
						
					}
				});
			}
		});
		taskThread.start();
	}

	/* Implemented from Modifiable
	 * @see fr.irit.smac.planification.datadisplay.interfaces.Modifiable#update()
	 * Updates charts and grids 
	 */
	@Override
	public void update() {

		updateCharts();
		updateGrids();
	}

	/* UpdateChartsByBounds
	 * When the user select bounds to display a special part of the charts, 
	 * this method rebuilds charts the with wanted values
	 */
	public void updateChartsByBounds() {

		Thread taskThread = new Thread(new Runnable() {

			@Override
			public void run() {

				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						List<XYChart.Data<Number, Number>> newMeanData = new ArrayList<>();
						List<XYChart.Data<Number, Number>> newMaxData = new ArrayList<>();

						/* mean */
						for (XYChart.Data<Number, Number> data : allMeanData) {
							int x = data.getXValue().intValue();
							if (x >= borneInf && x <= borneSup) {
								newMeanData.add(data);
							}
						}
						/* max */
						for (XYChart.Data<Number, Number> data : allMaxData) {
							int x = data.getXValue().intValue();
							if (x >= borneInf && x <= borneSup) {
								newMaxData.add(data);
							}
						}
						
						/* Suppression des graphes actuels */
						lineChartMeanDiff.setVisible(false);
						lineChartMaxDiff.setVisible(false);
						rootCharts.getChildren().remove(lineChartMeanDiff);
						rootCharts.getChildren().remove(lineChartMaxDiff);
						rootCharts.getChildren().removeAll(borneInfSlider, borneSupSlider, labelBorneInf,
								labelBorneSupp);

						/* Recreation des graphes */
						xAxisMeanDiff.setLowerBound(borneInf);
						lineChartMeanDiff = new LineChart<>(xAxisMeanDiff, yAxisMeanDiff);
						seriesMeanDiff = new XYChart.Series<>();
						seriesMeanDiff.setName("Planings MeanDiff");
						seriesMeanDiff.getData().addAll(newMeanData);
						lineChartMeanDiff.getData().add(seriesMeanDiff);
						lineChartMeanDiff.setPadding(new Insets(10, 0, 0, 0));

						xAxisMaxDiff.setLowerBound(borneInf);
						lineChartMaxDiff = new LineChart<>(xAxisMaxDiff, yAxisMaxDiff);
						seriesMaxDiff = new XYChart.Series<>();
						seriesMaxDiff.setName("Planings MaxDiff");
						seriesMaxDiff.getData().addAll(newMaxData);
						lineChartMaxDiff.getData().add(seriesMaxDiff);
						lineChartMaxDiff.setPadding(new Insets(10, 0, 0, 0));

						rootCharts.getChildren().addAll(lineChartMeanDiff, lineChartMaxDiff, labelBorneInf,
								borneInfSlider, labelBorneSupp, borneSupSlider);
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

	public void setBorneInf(int value) {
		this.borneInf = value;
	}

	public void setBorneSup(int value) {
		this.borneSup = value;
	}

	public int getBorneInf() {
		return borneInf;
	}

	public int getBorneSup() {
		return borneSup;
	}
	
	
}
