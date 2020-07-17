package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

public class OracleComparaisonDisplayController implements EventHandler<ActionEvent>, ChangeListener<Number> {

	private CAVModel cavModel;
	private Slider associateSlider;

	public OracleComparaisonDisplayController(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	public OracleComparaisonDisplayController(CAVModel cavModel, Slider associateSlider) {
		this.cavModel = cavModel;
		this.associateSlider = associateSlider;
	}

	@Override
	public void handle(ActionEvent actionEvent) {
		Button buttonSource = (Button) actionEvent.getSource();
		if (buttonSource.getId().equals("pauseID")) {
			cavModel.setPause(true);
			buttonSource.setText("RESUME");
			buttonSource.setId("resumeID");
		} else if (buttonSource.getId().equals("resumeID")) {
			cavModel.setPause(false);
			buttonSource.setText("PAUSE");
			buttonSource.setId("pauseID");
		} else if (buttonSource.getId().equals("oneCycleID")) {
			boolean isPaused = cavModel.getPaused();
			if (!isPaused) {
				cavModel.setPause(true);
				try {
					Thread.sleep(cavModel.getStepPeriod());
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			cavModel.oneCycle();
		}
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		String sliderId = associateSlider.getId();
		if (sliderId.equals("periodSliderID")) {
			cavModel.setStepPeriod(newValue.intValue());
		} else if (sliderId.equals("stepSpeedID")) {
			System.out.println("stepSpeed just changed");
		}
	}

	public void setAssociateSlider(Slider slider) {
		this.associateSlider = slider;
	}

	public Slider getAssociateSlider() {
		return associateSlider;
	}

	public void setCavModel(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	public CAVModel getCavModel() {
		return cavModel;
	}
}
