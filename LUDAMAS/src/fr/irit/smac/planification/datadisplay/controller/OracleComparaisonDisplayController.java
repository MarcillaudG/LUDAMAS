package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class OracleComparaisonDisplayController implements EventHandler<ActionEvent>, ChangeListener<Number> {

	private CAVModel cavModel;

	public OracleComparaisonDisplayController(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	@Override
	public void handle(ActionEvent actionEvent) {
		Button buttonSource = (Button) actionEvent.getSource();
		if (buttonSource.getId().equals("pauseID")) {
			System.out.println("pause asked");
			cavModel.setPause(true);
			buttonSource.setText("RESUME");
			buttonSource.setId("resumeID");
		} else if (buttonSource.getId().equals("resumeID")) {
			System.out.println("resume asked");
			cavModel.setPause(false);
			buttonSource.setText("PAUSE");
			buttonSource.setId("pauseID");
		}
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		cavModel.setStepPeriod(newValue.intValue());
	}

	public void setCavModel(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	public CAVModel getCavModel() {
		return cavModel;
	}
}
