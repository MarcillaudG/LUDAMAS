package fr.irit.smac.planification.datadisplayui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class OpenDataDisplayHandler implements EventHandler<ActionEvent> {

	public void handle(ActionEvent actionEvent) {
		DataAgentDisplay dataAgentDisplay = new DataAgentDisplay();
		dataAgentDisplay.buildWindow(1);
	}

}