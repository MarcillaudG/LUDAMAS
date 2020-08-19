package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.main.CentralPanel;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.DataMorphAgentDisplay;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;

/*
 * Controller pour le DataAgentDisplay
 * Permet d'afficher les DataMorphAgents associes au DataAgent voulu 
 */
public class DataAgentDisplayController implements EventHandler<ActionEvent> {

	private CAVModel cavModel;
	private CentralPanel centralPanel;

	public DataAgentDisplayController(CAVModel cavModel, CentralPanel centralPanel) {
		this.cavModel = cavModel;
		this.centralPanel = centralPanel;
	}

	/*
	 * Button Handler Recupere le nom du DataAgent selectionne depuis l'id du bouton
	 * pour creer le nouveau TitledPane associe et l'ajoute dans le root container du panel 
	 * central
	 */
	@Override
	public void handle(ActionEvent actionEvent) {
		Button sourceButton = (Button) actionEvent.getSource();
		String idButton = sourceButton.getId();
		DataMorphAgentDisplay morphAgentDisplay = new DataMorphAgentDisplay(cavModel, idButton);
		TitledPane morphAgentPane = new TitledPane("MorphAgent: " + idButton, morphAgentDisplay.getScrollPane());
		centralPanel.getRoot().getChildren().add(3 + centralPanel.getNbCreatedDataMorphPanes(), morphAgentPane);
		centralPanel.incNbCreatedDataMorphPanes();
		cavModel.addModifiables(morphAgentDisplay);
		if (cavModel.getCycle() != 0) {
			morphAgentDisplay.update();
		}
	}

}
