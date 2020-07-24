package fr.irit.smac.planification.datadisplay.ui;


import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChartDisplay implements Modifiable{
	
	private CAVModel cavModel;
	private VBox root;
	private Stage primaryStage;
	private Series<Number, Number> seriesMeanDiff;
	private Series<Number, Number> seriesMaxDiff;
	private Slider borneInfMean;
	private Slider borneSupMean;
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
	
	
	public ChartDisplay(CAVModel cavModel) {
		this.cavModel = cavModel;
		primaryStage = new Stage();
		start();
	}
	
	public void start() {
		
		primaryStage.setTitle("Affichage graphique");
		/* MEAN DIFF CHART */
		yAxisMeanDiff.setAutoRanging(true);
		xAxisMeanDiff.setLabel("Cycle");
		yAxisMeanDiff.setLabel("MeanDiff");
		lineChartMeanDiff = new LineChart<>(xAxisMeanDiff, yAxisMeanDiff);
		seriesMeanDiff = new XYChart.Series<>();
		seriesMeanDiff.setName("MeanDiff");
		lineChartMeanDiff.getData().add(seriesMeanDiff);
		lineChartMeanDiff.setPadding(new Insets(10, 0, 0, 0));
		
		//Label labelBorneInf = new Label("Inferior bound");
		borneInfMean = new Slider();
		borneInfMean.setId("infBoundID");
		borneInfMean.setPrefWidth(600);
		borneInfMean.setMin(0);
		borneInfMean.setMax(cavModel.getCycle());
		borneInfMean.setValue(0);
		borneInfMean.setBlockIncrement(5);
		borneInfMean.setShowTickLabels(true);
		borneInfMean.setPadding(new Insets(30, 100, 0, 100));
		borneInfMean.setShowTickMarks(true);
		//borneInfMean.valueProperty().addListener(new ChartDisplayController(cavModel, borneInfMean, this));
		
		//Label labelBorneSupp = new Label("Superior bound");
		borneSupMean = new Slider();
		borneSupMean.setId("supBoundID");
		borneSupMean.setPrefWidth(600);
		borneSupMean.setMin(0);
		borneSupMean.setMax(cavModel.getCycle());
		borneSupMean.setValue(0);
		borneSupMean.setBlockIncrement(5);
		borneSupMean.setShowTickLabels(true);
		borneSupMean.setPadding(new Insets(30, 100, 0, 100));
		borneSupMean.setShowTickMarks(true);
		//borneSupMean.valueProperty().addListener(new ChartDisplayController(cavModel, borneSupMean, this));

		
		/* MAX DIFF CHART */	
		
		yAxisMaxDiff.setAutoRanging(true);
		xAxisMaxDiff.setLabel("Cycle");
		yAxisMaxDiff.setLabel("MaxDiff");
		lineChartMaxDiff = new LineChart<>(xAxisMaxDiff, yAxisMaxDiff);
		seriesMaxDiff = new XYChart.Series<>();
		seriesMaxDiff.setName("MaxDiff");
		lineChartMaxDiff.getData().add(seriesMaxDiff);	
		
		root = new VBox();
		root.getChildren().addAll(lineChartMeanDiff, lineChartMaxDiff, borneInfMean, borneSupMean);
		primaryStage.setScene(new Scene(root, 700, 500));
		primaryStage.show();
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
						
						XYChart.Data<Number, Number> newMeanData = new XYChart.Data<>(cycle, meanDiff);
						XYChart.Data<Number, Number> newMaxData = new XYChart.Data<>(cycle, maxDiff);
						allMeanData.add(newMeanData);
						allMaxData.add(newMaxData);
						
						if(borneSup==borneSupMean.getValue()) {
							seriesMeanDiff.getData().add(newMeanData);
							seriesMaxDiff.getData().add(newMaxData);
						}
					

						borneInfMean.setMax(borneInfMean.getMax()+1);
						borneSupMean.setMax(borneSupMean.getMax()+1);
						
						if(borneSup==borneSupMean.getValue()) {
							borneSupMean.setValue(borneSupMean.getMax());
						}
						
					}
				});
			}
		});
		taskThread.start();
	}
	
	public void updateByBounds() {
		
		Thread taskThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						//System.out.println("Inf: " + borneInf + " - Sup: " + borneSup);
						List<XYChart.Data<Number, Number>> newMeanData = new ArrayList<>();
						List<XYChart.Data<Number, Number>> newMaxData = new ArrayList<>();
						
						/* mean */
						for(XYChart.Data<Number, Number> data : allMeanData) {
							int x = data.getXValue().intValue();
							if(x>=borneInf && x<=borneSup) {
								newMeanData.add(data);
								//System.out.println("Step: " + x + " valid");
							}
						}
						/* max */
						for(XYChart.Data<Number, Number> data : allMaxData) {
							int x = data.getXValue().intValue();
							if(x>=borneInf && x<=borneSup) {
								newMaxData.add(data);
							}
						}
						
						lineChartMeanDiff.setVisible(false);
						lineChartMaxDiff.setVisible(false);
						root.getChildren().remove(lineChartMeanDiff);
						root.getChildren().remove(lineChartMaxDiff);
						root.getChildren().removeAll(borneInfMean, borneSupMean);
						
						xAxisMeanDiff.setLowerBound(borneInf);
						lineChartMeanDiff = new LineChart<>(xAxisMeanDiff, yAxisMeanDiff);
						seriesMeanDiff = new XYChart.Series<>();
						seriesMeanDiff.setName("MeanDiff");
						seriesMeanDiff.getData().addAll(newMeanData);
						lineChartMeanDiff.getData().add(seriesMeanDiff);
						lineChartMeanDiff.setPadding(new Insets(10, 0, 0, 0));
						
						xAxisMaxDiff.setLowerBound(borneInf);
						lineChartMaxDiff = new LineChart<>(xAxisMaxDiff, yAxisMaxDiff);
						seriesMaxDiff = new XYChart.Series<>();
						seriesMaxDiff.setName("MaxDiff");
						seriesMaxDiff.getData().addAll(newMaxData);
						lineChartMaxDiff.getData().add(seriesMaxDiff);
						lineChartMaxDiff.setPadding(new Insets(10, 0, 0, 0));						
						
						root.getChildren().addAll(lineChartMeanDiff, lineChartMaxDiff, borneInfMean, borneSupMean);
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
