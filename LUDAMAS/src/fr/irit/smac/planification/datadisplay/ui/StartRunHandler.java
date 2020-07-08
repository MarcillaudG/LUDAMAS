package fr.irit.smac.planification.datadisplay.ui;

import fr.irit.smac.planification.system.CAV;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class StartRunHandler implements EventHandler<ActionEvent> {
	
	private CAV cav;
	
	@Override
	public void handle(ActionEvent actionEvent) {
		this.cav = new CAV("cavtest", 1, 1, 3, 3, "C:\\Users\\shino\\Desktop\\dataset_mock_20_enhanced_Noise.csv");
		int i=0;
		while(i<1000) {
			cav.manageSituation();
			cav.generateNewValues(i);
			i++;
		}
	}
	
	public CAV getCav() {
		return cav;
	}
	
}
