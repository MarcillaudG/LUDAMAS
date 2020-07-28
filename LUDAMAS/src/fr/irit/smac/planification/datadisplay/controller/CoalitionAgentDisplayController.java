package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.AVTAgentDisplay;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CoalitionAgentDisplayController implements EventHandler<ActionEvent> {
	
	private CAVModel cavModel;
	
	public CoalitionAgentDisplayController(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	@Override
	public void handle(ActionEvent actionEvent) {
		Button sourceButton = (Button) actionEvent.getSource();
		String idButton = sourceButton.getId();
		if(idButton.equals("closeID")) {
			closeAction(sourceButton);
		} else {
			AVTAgentDisplay avtDisplay = new AVTAgentDisplay(cavModel, idButton);
			cavModel.addModifiables(avtDisplay);
		}
	}
	
	private void closeAction(Button source) {
		Stage stageCorresp = (Stage) source.getScene().getWindow();
		stageCorresp.close();
	}
	
	public void setCavModel(CAVModel cavModel) {
		this.cavModel = cavModel;
	}
	
	public CAVModel getCavModel() {
		return cavModel;
	}
}
