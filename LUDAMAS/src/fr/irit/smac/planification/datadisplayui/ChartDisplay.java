package fr.irit.smac.planification.datadisplayui;

import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChartDisplay extends Application{
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Affichage graphique");
		
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Étape");
		yAxis.setLabel("Donnée");
		LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
		lineChart.setTitle("Graphique données");
		Series<Number, Number> series = new XYChart.Series<>();
		series.setName("Données TEST");
		series.getData().add(new XYChart.Data<>(30, 20));
		series.getData().add(new XYChart.Data<>(20, 15));
		series.getData().add(new XYChart.Data<>(45, 25));
		series.getData().add(new XYChart.Data<>(50, 5));
		series.getData().add(new XYChart.Data<>(100, 10));
		lineChart.getData().add(series);
//		xAxis.setAutoRanging(false);
//		yAxis.setAutoRanging(false);
		
		VBox root = new VBox();
		root.getChildren().add(lineChart);
		primaryStage.setScene(new Scene(root, 500, 300));
		primaryStage.show();
		
		ajoutPeriodique(series);
	}
	
	
	public void ajoutPeriodique(Series<Number, Number> series) {
		Thread taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0; i<20; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.out.println("Exception interruption wait");
					}
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							Random rnd = new Random();
							int nextX = rnd.nextInt(20);
							int nextY = rnd.nextInt(20);
							series.getData().add(new XYChart.Data<>(nextX, nextY));
						}
					});
				}
			}
		});
		taskThread.start();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
