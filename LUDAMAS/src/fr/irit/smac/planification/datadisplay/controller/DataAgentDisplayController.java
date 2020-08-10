package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.DataMorphAgentDisplay;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/*
 * Controller pour le DataAgentDisplay
 * Permet d'afficher les DataMorphAgents associes au DataAgent voulu 
 */
public class DataAgentDisplayController implements EventHandler<ActionEvent> {

	private CAVModel cavModel;

	public DataAgentDisplayController(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	/*
	 * Button Handler
	 * Recupere le nom du DataAgent selectionne depuis l'id du bouton pour 
	 * ouvrir la fenetre d'affichage des DataMorphAgents associes
	 */
	@Override
	public void handle(ActionEvent actionEvent) {
		Button sourceButton = (Button) actionEvent.getSource();
		String idButton = sourceButton.getId();

		DataMorphAgentDisplay morphAgentDisplay = new DataMorphAgentDisplay(cavModel, idButton);
		cavModel.addModifiables(morphAgentDisplay);
		if (cavModel.getCycle() != 0) {
			morphAgentDisplay.update();
		}
	}

	public void setCavModel(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	public CAVModel getCavModel() {
		return cavModel;
	}
}
