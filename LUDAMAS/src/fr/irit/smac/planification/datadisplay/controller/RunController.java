package fr.irit.smac.planification.datadisplay.controller;

import java.io.File;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.AgentDisplayChoice;
import fr.irit.smac.planification.datadisplay.ui.ChartDisplay;
import fr.irit.smac.planification.datadisplay.ui.MainUI;
import fr.irit.smac.planification.datadisplay.ui.OracleComparaisonDisplay;
import fr.irit.smac.planification.datadisplay.ui.ToolsDisplay;
import fr.irit.smac.planification.system.CAV;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


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
			runHandle(buttonSource);
		} else if (buttonSource.getId().equals("fileChooserID")) {
			fileChoosing();
		}
	}

	private void fileChoosing() {
		
		try {
			File selectedFile = mainApp.getFileChooser().showOpenDialog(mainApp.getPrimaryStage());
			filePath = selectedFile.getAbsolutePath();
			Label textSelectedFile = mainApp.getTextSelectedLabel();
			textSelectedFile.setText(filePath);
		} catch(NullPointerException e) {
			//TODO: label text: "please select a file"
			System.out.println("Please select a file");
		}
	}

	private void runHandle(Button source) {

		int nbEffectors = mainApp.getValueSpinEffector();
		int nbSituations = mainApp.getValueSpinSituations();
		int nbVarEff = mainApp.getValueSpinVarEff();
		int nbCopy = mainApp.getValueSpinCopy();
		if(filePath!=null) {
			this.cav = new CAV("cavtest", nbEffectors, nbSituations, nbVarEff, nbCopy, filePath);
			this.cavModel.setCav(cav);
			OracleComparaisonDisplay oracleDisplay = new OracleComparaisonDisplay(cavModel);
			cavModel.addModifiables(oracleDisplay);
			new AgentDisplayChoice(cavModel);
			new ToolsDisplay(cavModel);
			ChartDisplay chartDisplay = new ChartDisplay(cavModel);
			cavModel.addModifiables(chartDisplay);
			cavModel.runExperiment();
			Stage stageCorresp = (Stage) source.getScene().getWindow();
			stageCorresp.close();
		}
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
