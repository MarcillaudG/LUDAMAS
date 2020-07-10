package fr.irit.smac.planification.datadisplay.controller;

import java.io.File;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.AgentDisplayChoice;
import fr.irit.smac.planification.datadisplay.ui.MainUI;
import fr.irit.smac.planification.datadisplay.ui.OracleComparaisonDisplay;
import fr.irit.smac.planification.system.CAV;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class RunController implements EventHandler<ActionEvent> {
	
	private CAVModel cavModel;
	private CAV cav;
	private String filePath;
	private MainUI mainApp;

	public RunController() {
		this.cavModel = new CAVModel();
	}
	
	@Override
	public void handle(ActionEvent actionEvent) {

		Button buttonSource = (Button) actionEvent.getSource();
		if (buttonSource.getId().equals("runID")) {
			runHandle();
		} else if (buttonSource.getId().equals("fileChooserID")) {
			fileChoosing();
		}
	}

	private void fileChoosing() {

		File selectedFile = mainApp.getFileChooser().showOpenDialog(mainApp.getPrimaryStage());
		filePath = selectedFile.getAbsolutePath();
		Label textSelectedFile = mainApp.getTextSelectedLabel();
		textSelectedFile.setText(filePath);
	}

	private void runHandle() {

		int nbEffectors = mainApp.getValueSpinEffector();
		int nbSituations = mainApp.getValueSpinSituations();
		int nbVarEff = mainApp.getValueSpinVarEff();
		int nbCopy = mainApp.getValueSpinCopy();
		this.cav = new CAV("cavtest", nbEffectors, nbSituations, nbVarEff, nbCopy, filePath);
		this.cavModel.setCav(cav);
		
		OracleComparaisonDisplay oracleDisplay = new OracleComparaisonDisplay(cavModel);
		cavModel.addModifiables(oracleDisplay);
		new AgentDisplayChoice(cavModel);

		cavModel.runExperiment();
	}
	
	
	public CAVModel getCavModel() {
		return cavModel;
	}

	public void setCav(CAV cav) {
		this.cav = cav;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setMainApp(MainUI mainApp) {
		this.mainApp = mainApp;
	}

	public CAV getCav() {
		return cav;
	}

	public String getFilePath() {
		return filePath;
	}

	public MainUI getMainApp() {
		return mainApp;
	}
}
