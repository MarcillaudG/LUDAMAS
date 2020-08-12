package fr.irit.smac.planification.datadisplay.controller;

import fr.irit.smac.planification.datadisplay.main.CentralPanel;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.datadisplay.ui.HoveredChartData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;

/*
 * Controller pour les graphiques du CentralPanel
 * Defini les actions:
 * - Changement d'une valeur d'un des deux sliders (borne inf ou borne sup)
 * - Affichage de la valeur exacte d'un noeud des graphes (mouse event)
 */
public class ChartDisplayController implements ChangeListener<Number>, EventHandler<MouseEvent>{
	
	private CAVModel cavModel;
	private Slider associateSlider;
	private CentralPanel chartDisplay;
	
	/* Connaît un slider ainsi que le CentralPanel pour pouvoir changer des valeurs (voir changed) */
	public ChartDisplayController(CAVModel cavModel, Slider associateSlider, CentralPanel chartDisplay) {
		this.cavModel = cavModel;
		this.associateSlider = associateSlider;
		this.chartDisplay = chartDisplay;
	}
	
	public ChartDisplayController(CAVModel cavModel) {
		this.cavModel = cavModel;
	}

	/*
	 * Changed
	 * En fonction de l'id du slider (et du slider associe au controller)
	 * Modifie les valeurs borneSup/borneInf du CentralPanel et lui indique de modifier les graphes
	 * en fonction des nouvelles valeurs
	 * Si une incoherence est trouvee (borne inferieure > borne sup) alors la borne inferieure
	 * est placee a une valeur inferieure a la borne superieure
	 */
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		String sliderId = associateSlider.getId();
		if(sliderId.equals("infBoundID")) {
			int borneSup = chartDisplay.getBorneSup();
			if(newValue.intValue()>=borneSup) {
				chartDisplay.setBorneInf(borneSup-1);
				associateSlider.setValue(chartDisplay.getBorneInf());
			} else {
				chartDisplay.setBorneInf(newValue.intValue());
			}
		} else if(sliderId.equals("supBoundID")) {
			int borneInf = chartDisplay.getBorneInf();
			if(newValue.intValue()<=borneInf) {
				chartDisplay.setBorneSup(borneInf+1);
				associateSlider.setValue(chartDisplay.getBorneSup());
			} else {
				chartDisplay.setBorneSup(newValue.intValue());
			}
		}
		chartDisplay.updateChartsByBounds();
	}
	
	/*
	 * MouseEvent
	 * Action declenchee par l'evenement du passage de la souris sur un noeud 
	 * d'un des graphes, permet d'afficher sa valeur exacte dans le noeud (voir
	 * HoveredChartData)
	 */
	@Override 
	public void handle(MouseEvent mouseEvent) {
		HoveredChartData sourceNode = (HoveredChartData) mouseEvent.getSource();
		if(sourceNode.getChildren().isEmpty()) {
			sourceNode.getChildren().setAll(sourceNode.getDataLabel());
			sourceNode.toFront();
		} else {
			sourceNode.getChildren().clear();
		}
	}
	
	
	public CAVModel getCavModel() {
		return cavModel;
	}


}
