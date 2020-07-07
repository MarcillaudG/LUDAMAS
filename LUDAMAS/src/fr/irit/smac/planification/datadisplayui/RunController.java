package fr.irit.smac.planification.datadisplayui;

import java.io.File;

import fr.irit.smac.planification.system.CAV;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class RunController implements EventHandler<ActionEvent> {

	private CAV cav;
	private String filePath;
	private MainUI mainApp;

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
	}

	private void runHandle() {

		this.cav = new CAV("cavtest", 1, 1, 3, 3, filePath);
		OracleComparaisonDisplay oracleDisplay = new OracleComparaisonDisplay(cav);
		new AgentDisplayChoice();

		Thread taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					cav.manageSituation();
					cav.generateNewValues(i);
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
