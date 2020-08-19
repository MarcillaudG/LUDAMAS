package fr.irit.smac.planification.datadisplay.ui;

import fr.irit.smac.planification.datadisplay.controller.ChartDisplayController;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/* Charts nodes 
 * Permettent d'afficher la valeur exacte d'un point d'un graphe 
 * lorsque la souris est passee dessus
 */
public class HoveredChartData extends StackPane{
	
	private Label dataLabel;
	private CAVModel cavModel;
	private ChartDisplayController controller;
	
	public HoveredChartData(CAVModel cavModel, Number value) {
		this.cavModel = cavModel;
		this.controller = new ChartDisplayController(cavModel);
		setPrefSize(15, 15);
		dataLabel = new Label(String.valueOf(value.intValue()));
	    dataLabel.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
		dataLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
		dataLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
		
		this.setOnMouseEntered(controller);
		this.setOnMouseExited(controller);		
	}
	
	public Label getDataLabel() {
		return dataLabel;
	}
	
	public CAVModel getCavModel() {
		return cavModel;
	}
	
}