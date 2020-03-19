package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.planification.Input;
import fr.irit.smac.planification.Matrix;
import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.Situation;

public class EffectorAgent {

	private String name;

	private String dataInfluenced;

	// inputs
	private Set<String> dataUsed;

	// Data gathered by sensors
	private List<String> dataPerceived;

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

	private List<ComposedFunction> composedFunctions;

	private Map<Situation,DecisionProcess> decisionProcess;

	private Situation currentSituation;

	private List<String> effectorsBefore;

	private final int window = 5;

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
		this.dataPerceived = new ArrayList<String>();
		this.dataUsed = new TreeSet<String>();
		this.objectives = new TreeMap<Integer,Float>();
		this.dpercom = new TreeMap<>();
		this.chosen = new ArrayList<>();
		this.composedFunctions = new ArrayList<>();
		this.decisionProcess = new HashMap<>();
		this.effectorsBefore = new ArrayList<>();
	}

	public void initSituation() {
		this.myPlaning = new Planing();
		this.lastPlaning = null;
		this.cost = 0.0f;
	}



	public void perceive() {
		// Recuperation des donnees percues
		this.dataPerceived.clear();
		//this.dataPerceived.addAll(this.cav.getDataPerceivedInSituation());
		//this.dataPerceived.addAll(this.cav.getInformationAvailable(this.myObjectiveState));
		this.currentSituation = this.cav.getCurrentSituation();
		this.dataPerceived.addAll(this.currentSituation.getInformationAvailable(this.currentStep));

		// Recuperation des donnees communiquees
		this.dataCommunicated.clear();
		this.dataCommunicated.addAll(this.cav.getDataComInSituation());

		this.lastPlaning = this.myPlaning;

	}

	public void decide() {
		System.out.println("Decide");
		// Creation of the matrix DataUsed minus dataPerceived / dataCommunicated
		this.subMatrix = this.myMatrix.constructSubmatrix(this.dataPerceived, this.decisionProcess.get(this.currentSituation).getExtero());
		System.out.println(this.decisionProcess.get(this.currentSituation).getExtero());
		System.out.println(subMatrix);
		this.dpercom.clear();
		this.chosen.clear();
		// Choix des exteroceptives
		// TEST
		/*for(int i = 0; i < this.cav.NB_EXTEROCEPTIVES;i++) {
			this.chosen.add(this.dataPerceived.get(i));
		}*/

		for(Input in: this.subMatrix.getMatrix().keySet()) {
			float max = 0.0f;
			String data = "";
			for(String s : this.subMatrix.getMatrix().get(in).keySet()) {
				if(this.subMatrix.getMatrix().get(in).get(s) > max) {
					data = s;
					max = this.subMatrix.getMatrix().get(in).get(s);
				}
			}
			this.chosen.add(data);
			this.myPlaning.addExteroData(in.getData(), data);
		}


		// Decision des objectifs en fonction des donnees choisies
		this.lastPlaning = this.myPlaning;


		this.planActions();


	}


	private void planActions() {
		// Use the CAV function to get the planing using 
		System.out.println("CHOSEN :"+this.chosen);
		//Result res = this.cav.computeDecision(this.chosen,this.dpercom, this.myObjectiveState).getLastRes();
		for(int i=0; i < this.window;i++) {
			Result res = new Result(this.currentStep+i, this.decisionProcess.get(this.currentSituation).compute(chosen, effectorsBefore, this.currentStep+i));
			this.myPlaning.setResAtTime(this.currentStep+i, res);
		}

		/*Result res = new Result(this.currentStep, this.decisionProcess.get(this.currentSituation).compute(chosen, effectorsBefore, this.currentStep));
		int nbStep = res.getStep() - this.currentStep;
		System.out.println("NBStep1:"+res.getStep());
		System.out.println("NBStep2:"+nbStep);
		float valueRemaining = res.getValue() - this.cav.getValueOfState(this.myObjectiveState);

		// Too complicated, TODO
		/*if(valueRemaining > 0) {
			planActionSup(valueRemaining, nbStep);
		}
		else {
			planActionInf(valueRemaining,nbStep);
		}*/

		/*float action = valueRemaining / nbStep;
		for(int i = 0; i < nbStep;i++) {
			Result resTmp =new Result(this.currentStep+i, action);
			this.myPlaning.setResAtTime(this.currentStep+i, resTmp);
		}*/
		System.out.println(myPlaning);

		if(!this.myPlaning.isIdenticalToLast(this.lastPlaning)) {
			this.learn();
		}
	}


	private void learn() {
		for(String in: this.myPlaning.getExteroChosen().keySet()) {
			if(!this.myPlaning.getExteroChosen().get(in).equals(this.lastPlaning.getExteroChosen().get(in))) {
				this.myMatrix.setWeight(in, this.lastPlaning.getExteroChosen().get(in), 0.0f);
			}
		}
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
		this.cav.effect(this.myObjectiveState,this.myPlaning.getResAtTime(time));
		System.out.println("Avancement:"+time+"::"+this.cav.getValueOfState(myObjectiveState));
		this.cost += this.evaluateAction(this.myPlaning.getResAtTime(time));
		time++;


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


	public List<String> getDataPerceived() {
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


	public void addDP(DecisionProcess dp, Situation s) {
		this.decisionProcess.put(s, dp);
	}

}
