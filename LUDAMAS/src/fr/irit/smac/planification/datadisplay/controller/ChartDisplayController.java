package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.main.CentralPanel;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;

public class ChartDisplayController implements ChangeListener<Number>{
	
	private CAVModel cavModel;
	private Slider associateSlider;
	private CentralPanel chartDisplay;
	
	public ChartDisplayController(CAVModel cavModel, Slider associateSlider, CentralPanel chartDisplay) {
		this.cavModel = cavModel;
		this.associateSlider = associateSlider;
		this.chartDisplay = chartDisplay;
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		String sliderId = associateSlider.getId();
		if(sliderId.equals("infBoundID")) {
			chartDisplay.setBorneInf(newValue.intValue());
			int borneSup = chartDisplay.getBorneSup();
			if(newValue.intValue()>borneSup) {
				chartDisplay.setBorneInf(borneSup-1);
				associateSlider.setValue(chartDisplay.getBorneInf());
			}
		} else if(sliderId.equals("supBoundID")) {
			chartDisplay.setBorneSup(newValue.intValue());
		}
		chartDisplay.updateChartsByBounds();
	}
	
	
	public CAVModel getCavModel() {
		return cavModel;
	}

}
