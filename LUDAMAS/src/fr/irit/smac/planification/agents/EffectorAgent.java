package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.planification.Matrix;
import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;

public class EffectorAgent {

	private String name;

	private String dataInfluenced;

	// inputs
	private Set<String> dataUsed;

	// Data gathered by sensors
	private Set<String> dataPerceived;

	// Data gathered by communication
	private Set<String> dataCommunicated;

	// Objectives during situation
	private Map<Integer,Float> objectives;

	/**
	 * the value of the data chosen which can be modify
	 */
	private Map<String,Float> dpercom;

	/**
	 * The list of chosen data
	 */
	private List<String> chosen;

	// The function used to plan the step
	private CAV cav;

	private int myObjectiveState;

	private Matrix myMatrix;

	private Matrix subMatrix;

	private Planing myPlaning;

	private Planing lastPlaning;

	private float cost;

	private float bestAction;

	private int currentStep;

	public EffectorAgent(String name,CAV pf, int objState, float actionOpt) {
		this.cav = pf;
		this.name = name;
		this.myObjectiveState = objState;
		this.myMatrix = new Matrix(pf.getExteroceptiveData());
		this.bestAction = actionOpt;
		init();
	}


	private void init() {
		this.dataCommunicated = new TreeSet<String>();
		this.dataPerceived = new TreeSet<String>();
		this.dataUsed = new TreeSet<String>();
		this.objectives = new TreeMap<Integer,Float>();
		this.dpercom = new TreeMap<>();
		this.chosen = new ArrayList<>();
	}

	public void initSituation() {
		this.myPlaning = null;
		this.lastPlaning = null;
		this.cost = 0.0f;
	}

	public void perceive() {
		// Recuperation des donnees percues
		this.dataPerceived.clear();
		this.dataPerceived.addAll(this.cav.getDataPerceivedInSituation());

		// Recuperation des donnees communiquees
		this.dataCommunicated.clear();
		this.dataCommunicated.addAll(this.cav.getDataComInSituation());
	}

	public void decide() {
		System.out.println("Decide");
		// Creation of the matrix DataUsed minus dataPerceived / dataCommunicated
		this.subMatrix = this.myMatrix.constructSubmatrix(this.dataPerceived,this.dataCommunicated);
		this.dpercom.clear();
		this.chosen.clear();
		// Choix des exteroceptives
		// TEST
		for(String var: this.cav.getDataExteroceptiveInSituation()) {
			this.chosen.add(var);
		}
		// Decision des objectifs en fonction des donnees choisies
		this.lastPlaning = this.myPlaning;

		this.myPlaning = new Planing();
		this.planActions();


	}


	private void planActions() {
		// Use the CAV function to get the planing using 
		Result res = this.cav.computeDecision(this.chosen,this.dpercom, this.myObjectiveState).getLastRes();
		int nbStep = res.getStep() - this.currentStep;
		System.out.println("NBStep:"+nbStep);
		float valueRemaining = res.getValue() - this.cav.getValueOfState(this.myObjectiveState);
		
		// Too complicated, TODO
		/*if(valueRemaining > 0) {
			planActionSup(valueRemaining, nbStep);
		}
		else {
			planActionInf(valueRemaining,nbStep);
		}*/
		
		float action = valueRemaining / nbStep;
		for(int i = 0; i < nbStep;i++) {
			Result resTmp =new Result(this.currentStep+i, action);
			this.myPlaning.setResAtTime(this.currentStep+i, resTmp);
		}
		System.out.println(myPlaning);
	}


	private void planActionInf(float valueRemaining, int nbStep) {
		float factor = valueRemaining >0 ? -1.f:1.0f;
		System.out.println("valueRemaining:"+valueRemaining);
		float center = valueRemaining / nbStep;
		System.out.println("center:"+center);
		float decoupage = nbStep % 2 ==1 ?  (nbStep-1)/2 : (nbStep-2)/2;
		System.out.println("decoupage:"+decoupage);
		float distanceToZero = center / decoupage;
		System.out.println("distanceToZero:"+distanceToZero);
		float optimAction = (this.cav.getValueOfEffect(this.myObjectiveState)-center)/decoupage;
		System.out.println("optimAction:"+optimAction);
		float firstValue = center + decoupage * distanceToZero;
		System.out.println("firstValue:"+firstValue);
		float firstAction = this.cav.getValueOfEffect(this.myObjectiveState) - firstValue;
		System.out.println("firstAction:"+firstAction);
		this.myPlaning.setResAtTime(this.currentStep, new Result(currentStep, firstAction*factor));
		for(int i = 1; i < nbStep;i++) {
			Result resTmp =null;
			if(nbStep % 2 ==0 && i == nbStep /2) {
				resTmp = new Result(this.currentStep+i, 0.0f);
			}
			else {
				resTmp = new Result(this.currentStep+i, optimAction*factor);
			}
			this.myPlaning.setResAtTime(this.currentStep+i, resTmp);
		}
		System.out.println(myPlaning);

	}


	private void planActionSup(float valueRemaining, int nbStep) {
		float factor = valueRemaining >0 ? -1.f:1.0f;
		System.out.println("valueRemaining:"+valueRemaining);
		float center = valueRemaining / nbStep;
		System.out.println("center:"+center);
		float decoupage = nbStep % 2 ==1 ?  (nbStep-1)/2 : (nbStep-2)/2;
		System.out.println("decoupage:"+decoupage);
		float distanceToZero = center / decoupage;
		System.out.println("distanceToZero:"+distanceToZero);
		float optimAction = (this.cav.getValueOfEffect(this.myObjectiveState)-center)/decoupage;
		System.out.println("optimAction:"+optimAction);
		float firstValue = center + decoupage * distanceToZero;
		System.out.println("firstValue:"+firstValue);
		float firstAction = this.cav.getValueOfEffect(this.myObjectiveState) - firstValue;
		System.out.println("firstAction:"+firstAction);
		this.myPlaning.setResAtTime(this.currentStep, new Result(currentStep, firstAction*factor));
		for(int i = 1; i < nbStep;i++) {
			Result resTmp =null;
			if(nbStep % 2 ==0 && i == nbStep /2) {
				resTmp = new Result(this.currentStep+i, 0.0f);
			}
			else {
				resTmp = new Result(this.currentStep+i, optimAction*factor);
			}
			this.myPlaning.setResAtTime(this.currentStep+i, resTmp);
		}
		System.out.println(myPlaning);
	}


	public void act(Integer time) {
		System.out.println("Act");
		// Faire l'action suivante
		// TEST
		while(this.myPlaning.size() >time) {
			this.cav.effect(this.myObjectiveState,this.myPlaning.getResAtTime(time));
			System.out.println("Avancement:"+time+"::"+this.cav.getValueOfState(myObjectiveState));
			this.cost += this.evaluateAction(this.myPlaning.getResAtTime(time));
			time++;
		}
	}

	/**
	 * Evaluate the cost of the action
	 * @param resAtTime
	 * @return
	 */
	private float evaluateAction(Result resAtTime) {
		return (float) Math.exp(Math.abs(resAtTime.getValue()));
	}


	public String getName() {
		return name;
	}


	public String getDataInfluenced() {
		return dataInfluenced;
	}


	public Set<String> getDataUsed() {
		return dataUsed;
	}


	public Set<String> getDataPerceived() {
		return dataPerceived;
	}


	public Set<String> getDataCommunicated() {
		return dataCommunicated;
	}


	public Map<Integer, Float> getObjectives() {
		return objectives;
	}


	public CAV getPlanFun() {
		return cav;
	}


	public int getMyObjectiveState() {
		return myObjectiveState;
	}


	public void start(Integer time) {
		this.currentStep = time;
		this.perceive();
		this.decide();
		this.act(time);
	}

	public float getCostOfSituation() {
		return this.cost;
	}

}
