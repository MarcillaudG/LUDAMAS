package fr.irit.smac.planification.datadisplay.model;

import java.util.ArrayList;
import java.util.List;

import fr.irit.smac.planification.datadisplay.interfaces.Modifiable;
import fr.irit.smac.planification.system.CAV;

public class CAVModel {
	
	/* Data access and experiment */
	private CAV cav;
	/* List of opened updatable frames */
	private List<Modifiable> modifiables;
	/* period between two cycles (ms)*/
	private int cyclePeriod = 1000;
	private int cycle = 0;
	private boolean stop = false;
	
	public CAVModel() {
		this.modifiables = new ArrayList<>();
	}
	
	public CAVModel(CAV cav) {
		this.cav = cav;
		this.modifiables = new ArrayList<>();
	}
	
	/* UpdateFrames
	 * Informe toutes les fenetres modifiables qu'un cycle a ete 
	 * termine et donc les donnees ont ete modifiees 
	 */
	public void updateFrames() {
		for(Modifiable modifiable : modifiables) {
			modifiable.update();
		}
	}
	
	/* RunExperiment
	 * Demarre l'experience via un nouveau thread pour ne pas bloquer l'UI et 
	 * les autres calculs
	 * A chaque tour de boucle: si l'experience n'est pas en pause
	 * alors on effectue un cycle
	 */
	public void runExperiment() {

        Thread taskThread = new Thread(new Runnable() {

            @Override
            public void run() {
                cav.generateNewValues(cycle);
                while(cycle<1000) {
                	if(!stop) {
                        oneCycle();
                	}
                	try {
                        Thread.sleep(cyclePeriod);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        taskThread.start();
    }
	
	/* OneCycle
	 * One cycle of the experiment
	 */
	public void oneCycle() {
        cav.manageSituation(cycle);
        cycle++;
        updateFrames();
        cav.generateNewValues(cycle);
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
	
	public void setCyclePeriod(int value) {
		cyclePeriod = value;
	}
	
	public int getCyclePeriod() {
		return cyclePeriod;
	}
	
	public int getCycle() {
		return cycle;
	}
	
	public void setStopValue(boolean value) {
		this.stop = value;
	}
	
	public boolean isStopped() {
		return stop;
	}
	
	public void setCav(CAV cav) {
		this.cav = cav;
	}
	public CAV getCav() {
		return cav;
	}

}
