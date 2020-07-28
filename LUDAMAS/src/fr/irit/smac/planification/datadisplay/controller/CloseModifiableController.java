package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.interfaces.Modifiable;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class CloseModifiableController implements EventHandler<WindowEvent> {

	private CAVModel cavModel;
	private Modifiable modifiable;

	public CloseModifiableController(CAVModel cavModel, Modifiable modifiable) {
		this.cavModel = cavModel;
		this.modifiable = modifiable;
	}

	@Override
	public void handle(WindowEvent windowEvent) {
		cavModel.removeModifiables(modifiable);
	}

}
