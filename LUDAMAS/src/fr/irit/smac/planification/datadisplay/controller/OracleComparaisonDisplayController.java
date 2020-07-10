package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class OracleComparaisonDisplayController implements EventHandler<ActionEvent> {

	private CAVModel cavModel;
	
	public OracleComparaisonDisplayController(CAVModel cavModel) {
		this.cavModel = cavModel;
	}
	
	@Override
	public void handle(ActionEvent actionEvent) {
		Button buttonSource = (Button) actionEvent.getSource();
		if(buttonSource.getId().equals("pauseID")) {
			cavModel.setPause();
			buttonSource.setText("RESUME");
			buttonSource.setId("resumeID");
		} else if(buttonSource.getId().equals("resumeID")) {
			cavModel.setPause();
			buttonSource.setText("PAUSE");
			buttonSource.setId("pauseID");
		}
	}
	
	public void setCavModel(CAVModel cavModel) {
		this.cavModel = cavModel;
	}
	
	public CAVModel getCavModel() {
		return cavModel;
	}

}
