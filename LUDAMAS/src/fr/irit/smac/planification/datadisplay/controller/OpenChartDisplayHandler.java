package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.ui.ChartDisplay;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class OpenChartDisplayHandler implements EventHandler<ActionEvent>{
	
	@Override
	public void handle(ActionEvent actionEvent) {
		new ChartDisplay();
	}
}
