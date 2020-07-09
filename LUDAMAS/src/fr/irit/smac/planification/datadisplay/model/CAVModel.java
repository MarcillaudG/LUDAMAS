package fr.irit.smac.planification.datadisplay.model;

import java.util.ArrayList;
import java.util.List;

import fr.irit.smac.planification.datadisplay.ui.Modifiable;

public class CAVModel {
	
	private List<Modifiable> modifiables;
	
	public CAVModel() {
		this.modifiables = new ArrayList<>();
	}
	
	public void updateFrames() {
		for(Modifiable modifiable : modifiables) {
			modifiable.update();
		}
	}
	
	public void addModifiables(Modifiable modifiable) {
		modifiables.add(modifiable);
	}
	
	public List<Modifiable> getModifiables() {
		return modifiables;
	}
}
