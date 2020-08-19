package fr.irit.smac.planification.datadisplay.main;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fr.irit.smac.planification.Planing;

import fr.irit.smac.planification.datadisplay.controller.ChartDisplayController;
import fr.irit.smac.planification.datadisplay.controller.CoalitionAgentDisplayController;
import fr.irit.smac.planification.datadisplay.controller.DataAgentDisplayController;
import fr.irit.smac.planification.datadisplay.interfaces.Modifiable;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.CoalitionAgentDisplay;
import fr.irit.smac.planification.datadisplay.ui.DataAgentDisplay;
import fr.irit.smac.planification.datadisplay.ui.HoveredChartData;
import fr.irit.smac.planification.datadisplay.ui.PlaningsDisplay;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CentralPanel implements Modifiable {

	/* UI */
	private Stage primaryStage;
	private CAVModel cavModel;
	private VBox root;

	/* TitledPanes */
	TitledPane titledDataAgent;
	TitledPane titledCoalitionAgent;
	TitledPane titledCharts;
	TitledPane titledPlanings;
	int nbCreatedDataMorphPanes = 0;
	int nbCreatedAvtPanes = 0;

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

	public CentralPanel(CAVModel cavModel) {
		this.cavModel = cavModel;
		this.primaryStage = new Stage();
		this.primaryStage.getIcons().add(new Image("./fr/irit/smac/img/icon.png"));
		start();
	}

	/*
	 * Start Construction des composants de la fenetre
	 */
	public void start() {

		/* Header build - images */
		VBox start = new VBox();
		start.setPadding(new Insets(15, 0, 0, 0));
		Class<?> clazz = this.getClass();
		InputStream input = clazz.getResourceAsStream("/fr/irit/smac/img/luda.jpg");
		Image image = new Image(input);
		ImageView imageView = new ImageView(image);
		imageView.setPreserveRatio(true);
		imageView.setFitWidth(300);
		imageView.setFitHeight(275);
		InputStream inputTitle = clazz.getResourceAsStream("/fr/irit/smac/img/ludamastitle.png");
		Image imageTitle = new Image(inputTitle);
		ImageView imageTitleView = new ImageView(imageTitle);
		start.setAlignment(Pos.BASELINE_CENTER);
		Separator sepStart = new Separator(Orientation.HORIZONTAL);
		sepStart.setPadding(new Insets(10, 0, 0, 0));
		start.getChildren().addAll(imageView, imageTitleView);

		/* Components build - construction des graphes */
		startCharts();

		/* Construction des onglets pour chaque affichage */
		titledCharts = new TitledPane("Charts", rootCharts);
		PlaningsDisplay planingsDisplay = new PlaningsDisplay(cavModel);
		cavModel.addModifiables(planingsDisplay);
		titledPlanings = new TitledPane("Planings", planingsDisplay.getScrollPane());
		titledPlanings.setExpanded(false);

		DataAgentDisplay dataAgentDisplay = new DataAgentDisplay(cavModel);
		DataAgentDisplayController dataAgentController = new DataAgentDisplayController(cavModel, this);
		dataAgentDisplay.setController(dataAgentController);
		cavModel.addModifiables(dataAgentDisplay);
		titledDataAgent = new TitledPane("Data Agents", dataAgentDisplay.getScrollPane());
		titledDataAgent.setExpanded(false);

		CoalitionAgentDisplay coalitionDisplay = new CoalitionAgentDisplay(cavModel);
		CoalitionAgentDisplayController coalitionController = new CoalitionAgentDisplayController(cavModel, this);
		coalitionDisplay.setController(coalitionController);
		cavModel.addModifiables(coalitionDisplay);
		titledCoalitionAgent = new TitledPane("Coalition Agents", coalitionDisplay.getScrollPane());
		titledCoalitionAgent.setExpanded(false);

		root = new VBox();
		root.getChildren().addAll(start, sepStart, titledDataAgent, titledCoalitionAgent, titledCharts, titledPlanings);

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
	 * StartCharts construction des graphes qui seront mis à jour a chaque cycle
	 * Construction des sliders des bornes puis des graphes
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

	/*
	 * Update Implente depuis Modifiable Met a jour les deux graphes depuis les
	 * donnees collectees depuis le cav Mis a jour a chaque cycle de l'experience
	 */
	public void update() {

		Thread taskThread = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("debut centralPanel run thread update");
				/* Collecte des donnees */
				CAV cav = cavModel.getCav();
				int cycle = cavModel.getCycle();
				Planing truePlaning = cav.getTruePlaning();
				Planing situationPlaning = cav.getPlaningSituation();
				float meanDiff = truePlaning.computeMeanDifference(situationPlaning);
				float maxDiff = truePlaning.computeMaxDifference(situationPlaning);

				Platform.runLater(new Runnable() {
					@Override
					public synchronized void run() {
						/* Construction des nouveaux points des graphes */
						/*
						 * Ajout des points dans les collections correspondantes pour les affichages par
						 * bornes (voir updateChartsByBounds)
						 */
						XYChart.Data<Number, Number> newMeanData = new XYChart.Data<>(cycle, meanDiff);
						XYChart.Data<Number, Number> newMaxData = new XYChart.Data<>(cycle, maxDiff);
						newMeanData.setNode(new HoveredChartData(cavModel, meanDiff));
						newMaxData.setNode(new HoveredChartData(cavModel, maxDiff));

						allMeanData.add(newMeanData);
						allMaxData.add(newMaxData);
						/*
						 * Si la borne superieure est placee au maximum possible, alors le graphe
						 * continue d'accueillir les nouveaux points Sinon, les points sont stockes mais
						 * pas affiches
						 */
						if (borneSup == borneSupSlider.getValue()) {
							seriesMeanDiff.getData().add(newMeanData);
							seriesMaxDiff.getData().add(newMaxData);
						}
						borneInfSlider.setMax(borneInfSlider.getMax() + 1);
						borneSupSlider.setMax(borneSupSlider.getMax() + 1);
						if (borneSup == borneSupSlider.getValue()) {
							borneSupSlider.setValue(borneSupSlider.getMax() + 1);
						}

						System.out.println("fin runlater");
					}
				});
				/* Le travail est termine, on rend un token a la semaphore de cavModel */
				cavModel.V();
			}
		});
		taskThread.start();
		System.out.println("fin update chart");
	}

	/*
	 * UpdateChartsByBounds Methode declenchee par le ChartDisplayController des que
	 */
	public void updateChartsByBounds() {

		Thread taskThread = new Thread(new Runnable() {

			@Override
			public void run() {
				List<XYChart.Data<Number, Number>> newMeanData = new ArrayList<>();
				List<XYChart.Data<Number, Number>> newMaxData = new ArrayList<>();
				/*
				 * Calculs pour savoir quels sont les points demandes dans l'encadrement
				 * souhaite
				 */
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

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
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

	public void setBorneInf(int value) {
		this.borneInf = value;
	}

	public void setBorneSup(int value) {
		this.borneSup = value;
	}

	public void incNbCreatedAvtPanes() {
		nbCreatedAvtPanes++;
	}

	public void incNbCreatedDataMorphPanes() {
		nbCreatedDataMorphPanes++;
	}

	public int getBorneInf() {
		return borneInf;
	}

	public int getBorneSup() {
		return borneSup;
	}

	public VBox getRoot() {
		return root;
	}

	public int getNbCreatedDataMorphPanes() {
		return nbCreatedDataMorphPanes;
	}

	public int getNbCreatedAvtPanes() {
		return nbCreatedAvtPanes;
	}
}
