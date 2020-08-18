package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.main.CentralPanel;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.AVTAgentDisplay;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;

/*
 * Controller pour la fenetre CoalitionAgentDisplay
 * Permet d'afficher les AVTAgents correspondant au CoalitionAgent souhaite
 */
public class CoalitionAgentDisplayController implements EventHandler<ActionEvent> {

	private CAVModel cavModel;
	private CentralPanel centralPanel;
	
	public CoalitionAgentDisplayController(CAVModel cavModel, CentralPanel centralPanel) {
		this.cavModel = cavModel;
		this.centralPanel = centralPanel;
	}

	/*
	 * Button handler Recupere l'id du bouton clique qui est le nom du
	 * coalitionAgent correspondant pour permettre d'ouvrir une fenetre avec les
	 * AVTAgents associes
	 */
	@Override
	public void handle(ActionEvent actionEvent) {
		Button sourceButton = (Button) actionEvent.getSource();
		String idButton = sourceButton.getId();
		AVTAgentDisplay avtDisplay = new AVTAgentDisplay(cavModel, idButton);
		TitledPane avtPane = new TitledPane("AVT: " + idButton, avtDisplay.getScrollPane());
		int paneIndex = 4 + centralPanel.getNbCreatedDataMorphPanes() + centralPanel.getNbCreatedAvtPanes();
		centralPanel.getRoot().getChildren().add(paneIndex, avtPane);
		centralPanel.incNbCreatedAvtPanes();
		cavModel.addModifiables(avtDisplay);
		if (cavModel.getCycle() != 0) {
			avtDisplay.update();
		}
	}

}
