package fr.irit.smac.planification.datadisplay.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class OpenDataMorphDisplayHandler implements EventHandler<ActionEvent> {

	public void handle(ActionEvent actionEvent) {
		DataAgentDisplay dataAgentDisplay = new DataAgentDisplay();
		dataAgentDisplay.buildWindow(2);
	}

}
