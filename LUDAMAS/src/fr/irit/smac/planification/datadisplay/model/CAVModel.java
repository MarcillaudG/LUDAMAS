package fr.irit.smac.planification.datadisplay.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import fr.irit.smac.planification.datadisplay.interfaces.Modifiable;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;

public class CAVModel {

	/* Data access and experiment */
	private CAV cav;
	/* Liste des composants modifibles (qui seront mis a jours
	 * a chaque cycle de l'experience
	 */
	private List<Modifiable> modifiables;
	/* period between two cycles (ms)*/
	private int cyclePeriod = 1000;
	
	private final int nbCycleExp = 200;
	private int cycle = 0;
	private boolean stop = false;
	private boolean end = false;

	/* Semaphore permettant d'attendre la fin des mises a jours des composants
	 * modifiables
	 * Chaque composant modifiable rend un token a la fin de son update 
	 */
	private Semaphore semaphore;
	private Semaphore semTools;

	public CAVModel() {
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
				while(cycle<nbCycleExp) {
					if(end) {
						endExp();
					}
					else {
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
				endExp();
			}
		});
		taskThread.start();
	}

	/* OneCycle
	 * Deroulement d'un cycle:
	 * ManageSituation - incrementation du nombre de cycle - 
	 * mise a jour des composants modifiables - generation de nouvelles valeurs
	 */
	public void oneCycle() {
		cav.manageSituation(cycle);
		semaphore = new Semaphore(0);
		semTools = new Semaphore(0);
		updateFrames();
		/* Permet d'attendre la fin des mises a jour des
		 * composants modifiables pour continuer le cycle
		 */
		for(int i=0; i<modifiables.size(); i++) {
			P();
		}
		cycle++;
		cav.generateNewValues(cycle);
		semTools.release();
	}

	public void P() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void V() {
		semaphore.release();
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

	public Semaphore getSemTools() {
		return semTools;
	}

	public void endExp() {
		this.cav.endExp();
		Platform.exit();
	}

	public void setEndValue(boolean b) {
		this.end  = b;
	}
}
