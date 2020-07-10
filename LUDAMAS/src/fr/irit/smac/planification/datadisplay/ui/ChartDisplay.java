package fr.irit.smac.planification.datadisplay.ui;


import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChartDisplay implements Modifiable{
	
	private CAVModel cavModel;
	private Stage primaryStage;
	private Series<Number, Number> seriesMeanDiff;
	private Series<Number, Number> seriesMaxDiff;
	
	public ChartDisplay(CAVModel cavModel) {
		this.cavModel = cavModel;
		primaryStage = new Stage();
		start();
	}
	
	public void start() {
		primaryStage.setTitle("Affichage graphique");
		
		/* MEAN DIFF CHART */
		NumberAxis xAxisMeanDiff = new NumberAxis();
		NumberAxis yAxisMeanDiff = new NumberAxis();
		xAxisMeanDiff.setLabel("Cycle");
		yAxisMeanDiff.setLabel("MeanDiff");
		LineChart<Number, Number> lineChartMeanDiff = new LineChart<>(xAxisMeanDiff, yAxisMeanDiff);
		seriesMeanDiff = new XYChart.Series<>();
		seriesMeanDiff.setName("MeanDiff every cycle");
		lineChartMeanDiff.getData().add(seriesMeanDiff);
		lineChartMeanDiff.setPadding(new Insets(10, 0, 0, 0));
		
		/* MAX DIFF CHART */
		NumberAxis xAxisMaxDiff = new NumberAxis();
		NumberAxis yAxisMaxDiff = new NumberAxis();
		xAxisMaxDiff.setLabel("Cycle");
		yAxisMaxDiff.setLabel("MaxDiff");
		LineChart<Number, Number> lineChartMaxDiff = new LineChart<>(xAxisMaxDiff, yAxisMaxDiff);
		seriesMaxDiff = new XYChart.Series<>();
		seriesMaxDiff.setName("MaxDiffEveryCycle");
		lineChartMaxDiff.getData().add(seriesMaxDiff);	
		
		VBox root = new VBox();
		root.getChildren().addAll(lineChartMeanDiff, lineChartMaxDiff);
		primaryStage.setScene(new Scene(root, 700, 500));
		primaryStage.show();
		
		update();
	}
	
	public void update() {
		
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
						seriesMeanDiff.getData().add(new XYChart.Data<>(cycle, meanDiff));
						seriesMaxDiff.getData().add(new XYChart.Data<>(cycle, maxDiff));
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
