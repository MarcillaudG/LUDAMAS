package fr.irit.smac.planification.datadisplay.model;

import java.util.ArrayList;
import java.util.List;

import fr.irit.smac.planification.datadisplay.ui.Modifiable;
import fr.irit.smac.planification.system.CAV;

public class CAVModel {
	
	private CAV cav;
	private List<Modifiable> modifiables;
	private int stepPeriod = 1000;
	private int cycle = 0;
	private boolean pause = false;
	
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
	
	public void runExperiment() {
		
		Thread taskThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				cav.generateNewValues(cycle);
				while(cycle<1000) {
					if(!pause) {
						cav.manageSituation(cycle);
						cycle++;
						updateFrames();
						cav.generateNewValues(cycle);
					}
					try {
						Thread.sleep(stepPeriod);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		taskThread.start();
	}
	
	public void addModifiables(Modifiable modifiable) {
		modifiables.add(modifiable);
	}
	
	public void removeModifiables(Modifiable modifiable) {
		modifiables.remove(modifiable);
	}
	
	public List<Modifiable> getModifiables() {
		return modifiables;
	}
	
	public void setStepPeriod(int value) {
		stepPeriod = value;
	}
	
	public int getStepPeriod() {
		return stepPeriod;
	}
	
	public int getCycle() {
		return cycle;
	}
	
	public void setPause(boolean value) {
		this.pause = value;
	}
	
	public boolean getPaused() {
		return pause;
	}
	
	public void setCav(CAV cav) {
		this.cav = cav;
	}
	public CAV getCav() {
		return cav;
	}

}
