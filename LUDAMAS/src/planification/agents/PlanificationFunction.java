package planification.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


import fr.irit.smac.planification.Objective;
import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Situation;

public class PlanificationFunction {

	
	private String name;
	
	private Objective myObjective;
	
	private int nbObjectiveStates;
	
	private int nbEffectors;
	
	private int nbSituation;
	
	private Map<String,EffectorAgent> effectors;

	private float[] internalState;
	
	private Situation[] situations;
	
	public PlanificationFunction(String name, int nbObjectiveStates, int nbEffectors, int nbSituation) {
		this.name = name;
		
		if(nbObjectiveStates >  nbEffectors) {
			System.err.println("ERROR creation PlanificationFunction");
		}
		
		this.nbObjectiveStates = nbObjectiveStates;
		this.nbEffectors = nbEffectors;
		this.nbSituation = nbSituation;
		this.internalState = new float[this.nbObjectiveStates];
		this.situations = new Situation[this.nbSituation];
		
		init();
	}
	
	private void init() {
		this.effectors = new TreeMap<String,EffectorAgent>();
		
		int i = 0;
		while(i < this.nbEffectors) {
			int j = 0;
			while(j < this.nbObjectiveStates && i < this.nbEffectors) {
				String ne = "EffAgent:"+j+(i/this.nbObjectiveStates);
				EffectorAgent eff = new EffectorAgent(ne,this, j);
				this.effectors.put(ne, eff);
				j++;
				i++;
			}
		}
		
		for(int k = 0; k < this.nbObjectiveStates;k++) {
			this.internalState[k] = 0.0f;
		}
		
		for(int k = 0; k < this.nbSituation;k++) {
			Situation s = new Situation(k, this.nbObjectiveStates);
			this.situations[k] = s;
		}
	}
	
	private void computeObjective() {
		Random rand = new Random();
		int idSituation = rand.nextInt(this.nbSituation);
		this.myObjective = this.situations[idSituation].getMyobjective();
	}
	
	private void planificationEffectors() {
		List<EffectorAgent> effShuffle = new ArrayList<EffectorAgent>(this.effectors.values());
		Collections.shuffle(effShuffle);
		Iterator<EffectorAgent> it = effShuffle.iterator();
		while(it.hasNext()) {
			it.next().start();
		}
	}

	/**
	 * Compute a planing with several information
	 * 
	 * @param internalState
	 * 			All the private information, as the objective
	 * @param externalState
	 * 			All the information perceived or communicated chosen
	 * @param effectorState
	 * 			The state of the effector
	 * @return
	 */
	public Planing computeDecision(String internalState, Map<String,Float> externalState, String effectorState) {
		Planing plan = new Planing();
		
		return plan;
	}

	public void manageSituation() {
		this.computeObjective();
		this.planificationEffectors();
		
	}
}
