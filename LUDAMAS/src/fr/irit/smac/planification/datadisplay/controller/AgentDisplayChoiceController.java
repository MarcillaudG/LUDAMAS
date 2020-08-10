package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.CoalitionAgentDisplay;
import fr.irit.smac.planification.datadisplay.ui.DataAgentDisplay;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/*
 * Controller pour l'ouverture des fenetres de DataDisplay depuis le
 * CentralPanel
 */
public class AgentDisplayChoiceController implements EventHandler<ActionEvent> {
	
	private CAVModel cavModel;
	
	public AgentDisplayChoiceController(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	/*
	 * Button Handler
	 * Recupere l'id du bouton et appelle la methode associe
	 * pour ouvrir un affichage de donnees
	 */
	@Override
	public void handle(ActionEvent actionEvent) {
		Button buttonSource = (Button) actionEvent.getSource();
		String idButton = buttonSource.getId();
		if (idButton.equals("dataDisplayID")) {
			runAgentDisplay();
		} else if (idButton.equals("coalitionAgentDisplayID")) {
			runCoalitionAgentDisplay();
		}
	}
	
	
	private void runAgentDisplay() {
		DataAgentDisplay agentDisplay = new DataAgentDisplay(cavModel);
		cavModel.addModifiables(agentDisplay);
		if(cavModel.getCycle()!=0) {
			agentDisplay.update();
		}
	}
	
	private void runCoalitionAgentDisplay() {
		CoalitionAgentDisplay coalitionDisplay = new CoalitionAgentDisplay(cavModel);
		cavModel.addModifiables(coalitionDisplay);
		if(cavModel.getCycle()!=0) {
			coalitionDisplay.update();
		}
	}
	
	
	public void setCavModel(CAVModel cavModel) {
		this.cavModel = cavModel;
	}
	
	public CAVModel getCavModel() {
		return cavModel;
	}
	
}
