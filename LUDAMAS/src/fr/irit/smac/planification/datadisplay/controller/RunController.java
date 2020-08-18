package fr.irit.smac.planification.datadisplay.controller;

import java.io.File;

import fr.irit.smac.planification.datadisplay.main.CentralPanel;
import fr.irit.smac.planification.datadisplay.main.MainUI;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.ToolsDisplay;
import fr.irit.smac.planification.system.CAV;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/* Controller pour le demarrage de l'experience
 * Permet de choisir les differents parametres et de creer l'objet CAV
 */
public class RunController implements EventHandler<ActionEvent> {

	private CAVModel cavModel;
	private CAV cav;
	private String filePath;
	private MainUI mainApp;

	public RunController() {
		this.cavModel = new CAVModel();
	}

	/* Button action handler
	 * - Bouton run : demarre l'experience
	 * - Bouton fileChooser : ouvre le fileChooser pour choisir un dataset
	 */
	@Override
	public void handle(ActionEvent actionEvent) {
		Button buttonSource = (Button) actionEvent.getSource();
		if (buttonSource.getId().equals("runID")) {
			runHandle(buttonSource);
		} else if (buttonSource.getId().equals("fileChooserID")) {
			fileChoosing();
		}
	}

	/* Action declenchee par l'evenement du bouton fileChooser 
	 * Affiche le fileChooser et indique a l'utilisateur si aucun fichier n'a
	 * ete choisi
	 */
	private void fileChoosing() {
		try {
			File selectedFile = mainApp.getFileChooser().showOpenDialog(mainApp.getPrimaryStage());
			filePath = selectedFile.getAbsolutePath();
			Label textSelectedFile = mainApp.getTextSelectedLabel();
			textSelectedFile.setText(filePath);
		} catch (NullPointerException e) {
			System.out.println("Please select a file");
		}
	}

	/* Action declenchee par l'evenement du bouton run
	 * Lit les parametres fournis par l'utilisateur et demarre l'experience
	 * avec la creation de l'objet cav et l'affichage du CentralPanel
	 */
	private void runHandle(Button source) {
		int nbEffectors = mainApp.getValueSpinEffector();
		int nbSituations = mainApp.getValueSpinSituations();
		int nbVarEff = mainApp.getValueSpinVarEff();
		int nbCopy = mainApp.getValueSpinCopy();
		if (filePath != null) {
			this.cav = new CAV("cavtest", nbEffectors, nbSituations, nbVarEff, nbCopy, filePath);
			this.cavModel.setCav(cav);
			CentralPanel centralPanelV2 = new CentralPanel(cavModel);
			cavModel.addModifiables(centralPanelV2);
			new ToolsDisplay(cavModel);
			cavModel.runExperiment();
			Stage stageCorresp = (Stage) source.getScene().getWindow();
			stageCorresp.close();
		}
	}

	public CAVModel getCavModel() {
		return cavModel;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setMainApp(MainUI mainApp) {
		this.mainApp = mainApp;
	}

	public String getFilePath() {
		return filePath;
	}

	public MainUI getMainApp() {
		return mainApp;
	}
}
