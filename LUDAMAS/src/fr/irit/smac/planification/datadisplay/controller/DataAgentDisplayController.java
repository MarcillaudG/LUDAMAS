package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.DataMorphAgentDisplay;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DataAgentDisplayController implements EventHandler<ActionEvent>{
	
	private CAVModel cavModel;
	
	public DataAgentDisplayController(CAVModel cavModel) {
		this.cavModel = cavModel;
	}
	
	@Override
	public void handle(ActionEvent actionEvent) {
		Button sourceButton = (Button) actionEvent.getSource();
		String idButton = sourceButton.getId();
		/* cas du bouton close */
		if (idButton.equals("closeID")) {
			closeAction(sourceButton);
		/* cas des boutons dataMorph */
		} else {
			DataMorphAgentDisplay morphAgentDisplay = new DataMorphAgentDisplay(cavModel, idButton);
			cavModel.addModifiables(morphAgentDisplay);
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
