package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.ChartDisplay;
import fr.irit.smac.planification.datadisplay.ui.DataAgentDisplay;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class AgentDisplayChoiceController implements EventHandler<ActionEvent> {
	
	private CAVModel cavModel;
	
	public AgentDisplayChoiceController(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	@Override
	public void handle(ActionEvent actionEvent) {
		Button buttonSource = (Button) actionEvent.getSource();
		String idButton = buttonSource.getId();
		if (idButton.equals("dataDisplay1ID")) {
			runAgentDisplay(1);
		} else if (idButton.equals("dataDisplay2ID")) {
			runAgentDisplay(2);
		} else if (idButton.equals("dataDisplay3ID")) {
			runAgentDisplay(3);
		} else if (idButton.equals("chartDisplayID")) {
			runChartDisplay();
		}
	}
	
	
	private void runAgentDisplay(int agentType) {
		DataAgentDisplay agentDisplay = new DataAgentDisplay(agentType, cavModel);
		cavModel.addModifiables(agentDisplay);
		agentDisplay.buildWindow();
	}
	
	private void runChartDisplay() {
		ChartDisplay chartDisplay = new ChartDisplay(cavModel);
		cavModel.addModifiables(chartDisplay);
	}
	
	public void setCavModel(CAVModel cavModel) {
		this.cavModel = cavModel;
	}
	
	public CAVModel getCavModel() {
		return cavModel;
	}
	
}
