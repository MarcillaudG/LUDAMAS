package fr.irit.smac.planification.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.Situation;
import fr.irit.smac.planification.matrix.DataUnicityConstraint;
import fr.irit.smac.planification.matrix.InputConstraint;
import fr.irit.smac.planification.matrix.Matrix;

/**
 * 
 * @author gmarcill
 * 
 * Cette classe représente un sous processus de décision
 *
 */
public class Effector {
	
	private CAV cav;
	
	private String name;
	
	private int myObjectiveState;
	
	private Planing myPlaning;

	private Planing lastPlaning;
	
	private Map<Situation,DecisionProcess> decisionProcess;

	private Situation currentSituation;

	private List<String> effectorsBefore;
	
	private int currentStep;


	
	public Effector(String name,CAV pf, int objState) {
		this.cav = pf;
		this.name = name;
		this.myObjectiveState = objState;

		init();
	}


	private void init() {
		this.decisionProcess = new HashMap<>();
		this.effectorsBefore = new ArrayList<>();

	}


	public String getName() {
		return this.name;
	}


	public void addDP(DecisionProcess dp, Situation s) {
		this.decisionProcess.put(s, dp);		
	}

	/**
	 * demarre la situation
	 */
	public void initSituation() {
		this.myPlaning = new Planing();
		this.lastPlaning = null;

	}
	
	/**
	 * recupere la situation en cours
	 */
	public void perceive() {
		this.lastPlaning = this.myPlaning;
		this.myPlaning = new Planing();
		this.currentSituation = this.cav.getCurrentSituation();
		this.currentStep = this.cav.getCurrentTime();
	}
	
	/**
	 * applique le rresultat des donnees
	 */
	public void decide() {
		for(String input : this.decisionProcess.get(this.currentSituation).getExtero()) {
			this.decisionProcess.get(this.currentSituation).setValueOfInitInput(input, this.cav.getValueForInput(input));
		}
	}
	
	/**
	 * calcul un plan sur une fenetre de taille window
	 */
	public void act() {
		for(int i=0; i < CAV.WINDOW;i++) {
			Result res = new Result(this.cav.getCurrentTime()+i, this.decisionProcess.get(this.currentSituation).computeMorph(effectorsBefore, this.cav.getCurrentTime()+i));
			this.myPlaning.addRes(res);
		}
		
		this.cav.sendPlanning(this.name,this.myPlaning);
	}




	public DecisionProcess getDecisionProcess(Situation currentSituation) {
		return this.decisionProcess.get(currentSituation);
	}


	public void cycle() {
		this.perceive();
		this.decide();
		this.act();
	}


	public void cycleOracle() {
		this.perceive();
		for(String input : this.decisionProcess.get(this.currentSituation).getExtero()) {
			this.decisionProcess.get(this.currentSituation).setValueOfInitInput(input, this.cav.getTrueValueForInput(input));
		}
		for(int i=0; i < this.cav.getCurrentSituation().getTime();i++) {
			Result res = new Result(i, this.decisionProcess.get(this.currentSituation).computeMorph(effectorsBefore, i));
			this.myPlaning.addRes(res);
		}

		this.cav.sendPlanning(this.name,this.myPlaning);
	}
	
	

}
