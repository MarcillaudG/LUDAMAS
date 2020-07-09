package fr.irit.smac.planification.datadisplay.model;

import java.util.ArrayList;
import java.util.List;

import fr.irit.smac.planification.datadisplay.ui.Modifiable;
import fr.irit.smac.planification.system.CAV;

public class CAVModel {
	
	private CAV cav;
	private List<Modifiable> modifiables;
	
	public CAVModel() {
		this.modifiables = new ArrayList<>();
	}
	
	public CAVModel(CAV cav) {
		this.cav = cav;
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
	
	public void setCav(CAV cav) {
		this.cav = cav;
	}
	public CAV getCav() {
		return cav;
	}
}
