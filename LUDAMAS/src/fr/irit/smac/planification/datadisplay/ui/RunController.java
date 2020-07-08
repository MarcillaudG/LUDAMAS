package fr.irit.smac.planification.datadisplay.ui;

import java.io.File;

import fr.irit.smac.planification.system.CAV;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class RunController implements EventHandler<ActionEvent>, ChangeListener<Number> {

	private CAV cav;
	private String filePath;
	private MainUI mainApp;
	private int stepPeriod = 1000;

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
		OracleComparaisonDisplay oracleDisplay = new OracleComparaisonDisplay(cav);
		new AgentDisplayChoice(this);

		Thread taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					cav.manageSituation();
					cav.generateNewValues(i);
					try {
						Thread.sleep(stepPeriod);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(i);
				}
			}
		});
		taskThread.start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		oracleDisplay.launchView();
	}
	
	
	@Override 
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		stepPeriod = newValue.intValue();
		System.out.println("New value slider = " + stepPeriod);
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
