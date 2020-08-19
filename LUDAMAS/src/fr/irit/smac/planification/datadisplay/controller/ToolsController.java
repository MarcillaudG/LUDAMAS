package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

/* Controller s'occupant des actions de l'utilisateur sur l'interface Tools
 * - Boutons
 * - Sliders
 */
public class ToolsController implements EventHandler<ActionEvent>, ChangeListener<Number>{
	
	private CAVModel cavModel;
	private Slider associateSlider;
	
	/* Constructeur pour les actions des boutons, pas de slider associe */
	public ToolsController(CAVModel cavModel) {
		this.cavModel = cavModel;
	}
	
	/* Constructeur pour les actions des sliders
	 * Prends en parametre le slider associe au controller pour savoir de quel slider 
	 * provient l'evenement
	 */
	public ToolsController(CAVModel cavModel, Slider associateSlider) {
		this.cavModel = cavModel;
		this.associateSlider = associateSlider;
	}
	
	/*
	 * Handle
	 * Actions des boutons:
	 * - Bouton pause/resume: met en pause/reprend l'experimentation
	 * - Bouton One cycle: attend la fin du cycle actuel puis met pause et effectue un cycle
	 * - Bouton One step: effectue une etape d'un cycle (TODO)
	 */
	@Override
	public void handle(ActionEvent actionEvent) {
		Button buttonSource = (Button) actionEvent.getSource();
		if (buttonSource.getId().equals("pauseID")) {
			cavModel.setStopValue(true);
			buttonSource.setText("RESUME");
			buttonSource.setId("resumeID");
		} else if (buttonSource.getId().equals("resumeID")) {
			cavModel.setStopValue(false);
			buttonSource.setText("PAUSE");
			buttonSource.setId("pauseID");
		} else if (buttonSource.getId().equals("oneCycleID")) {
			boolean isStopped = cavModel.isStopped();
			if (!isStopped) {
				cavModel.setStopValue(true);
				try {
					Thread.sleep(cavModel.getCyclePeriod());
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("yes");
			cavModel.oneCycle();
		}
	}
	
	/*
	 * Changed
	 * Evenement changement d'une valeur du slider associe
	 * - Slider cyclePeriod: defini la periode entre deux cycles
	 * - Slider stepPeriod: defini la periode entre deux etapes d'un cycle TODO
	 */
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		String sliderId = associateSlider.getId();
		if (sliderId.equals("periodSliderID")) {
			cavModel.setCyclePeriod(newValue.intValue());
		} else if (sliderId.equals("stepSpeedID")) {
			System.out.println("stepSpeed just changed");
		}
	}

	public void setAssociateSlider(Slider slider) {
		this.associateSlider = slider;
	}

	public Slider getAssociateSlider() {
		return associateSlider;
	}


	
}
