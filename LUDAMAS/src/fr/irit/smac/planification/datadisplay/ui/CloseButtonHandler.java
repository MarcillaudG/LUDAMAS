package fr.irit.smac.planification.datadisplay.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CloseButtonHandler implements EventHandler<ActionEvent>{

	public void handle(ActionEvent actionEvent) {
		Button actionButton = (Button) actionEvent.getSource();
		Stage stageCorresp = (Stage) actionButton.getScene().getWindow();
		stageCorresp.close();
	}

}
