package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.ToolsDisplay;
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
public class ToolsController implements EventHandler<ActionEvent>, ChangeListener<Number> {

	private CAVModel cavModel;
	private Slider associateSlider;
	private ToolsDisplay display;

	/* Constructeur pour les actions des boutons, pas de slider associe */
	public ToolsController(CAVModel cavModel, ToolsDisplay display) {
		this.cavModel = cavModel;
		this.display = display;
	}

	/*
	 * Constructeur pour les actions des sliders Prends en parametre le slider
	 * associe au controller pour savoir de quel slider provient l'evenement
	 */
	public ToolsController(CAVModel cavModel, Slider associateSlider, ToolsDisplay display) {
		this.cavModel = cavModel;
		this.associateSlider = associateSlider;
		this.display = display;
	}

	/*
	 * Handle Actions des boutons: - Bouton pause/resume: met en pause/reprend
	 * l'experimentation - Bouton One cycle: attend la fin du cycle actuel puis met
	 * pause et effectue un cycle - Bouton One step: effectue une etape d'un cycle
	 * (TODO)
	 */
	@Override
	public void handle(ActionEvent actionEvent) {
		Button buttonSource = (Button) actionEvent.getSource();
		if (buttonSource.getId().equals("pauseID")) {
			cavModel.setStopValue(true);
			buttonSource.setText("RESUME");
			buttonSource.setId("resumeID");
			display.getOneCycleButton().setDisable(false);
		} else if (buttonSource.getId().equals("resumeID")) {
			cavModel.setStopValue(false);
			buttonSource.setText("PAUSE");
			buttonSource.setId("pauseID");
			display.getOneCycleButton().setDisable(true);
		} else if (buttonSource.getId().equals("oneCycleID")) {
			Thread taskThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						cavModel.getSemTools().acquire();
						cavModel.oneCycle();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			taskThread.start();
		}
	}

	/*
	 * Changed Evenement changement d'une valeur du slider associe - Slider
	 * cyclePeriod: defini la periode entre deux cycles - Slider stepPeriod: defini
	 * la periode entre deux etapes d'un cycle TODO
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
