package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.core.Links;
import fr.irit.smac.model.Entity;
import fr.irit.smac.model.Relation;
import fr.irit.smac.model.Snapshot;
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

	private Map<String,List<MorphingAgent>> morphlings;
	List<MorphingAgent> morphActifs;

	private Map<String,InputConstraint> inputsConstraints;

	private Map<String, DataUnicityConstraint> dataConstraint;

	// Links
	private Links links;

	private Snapshot currentSnapshot;

	private int nbCycle;

	public EffectorAgent(String name,CAV pf, int objState, float actionOpt) {
		this.cav = pf;
		this.name = name;
		this.myObjectiveState = objState;
		this.morphlings = new TreeMap<>();
		this.myMatrix = new Matrix(pf.getExteroceptiveData(),this );
		this.inputsConstraints = new TreeMap<>();
		for(String s : pf.getExteroceptiveData()) {
			this.inputsConstraints.put(s, new InputConstraint(this, s));
		}

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
		this.dataConstraint = new TreeMap<>();
		
		//Create a new experiment
		this.links = new Links(this.name,false);
	}

	public void initSituation() {
		this.myPlaning = new Planing();
		this.lastPlaning = null;
		this.cost = 0.0f;
		this.morphActifs = new ArrayList<>();


	}

	public void perceive() {
		// Recuperation des donnees percues
		this.dataPerceived.clear();
		this.morphActifs.clear();
		//this.dataPerceived.addAll(this.cav.getDataPerceivedInSituation());
		//this.dataPerceived.addAll(this.cav.getInformationAvailable(this.myObjectiveState));
		this.currentSituation = this.cav.getCurrentSituation();
		this.dataPerceived.addAll(this.currentSituation.getInformationAvailable(this.currentStep));

		// Recuperation des donnees communiquees
		this.dataCommunicated.clear();
		this.dataCommunicated.addAll(this.cav.getDataComInSituation());

		this.lastPlaning = new Planing(myPlaning);
		this.myPlaning= new Planing();
		this.currentSnapshot = new Snapshot();
		this.nbCycle = 1;
		//this.experiment.addSnapshot(currentSnapshot);
		this.links.addSnapshot(this.currentSnapshot);
	}

	public void decide() {
		System.out.println("Decide");
		// CreationofDataConstraint
		List<String> constraintToADD = new ArrayList<>(this.dataPerceived);
		constraintToADD.removeAll(this.dataConstraint.keySet());
		for(String data: constraintToADD) {
			this.dataConstraint.put(data, new DataUnicityConstraint(this, data));
			for(String input: this.inputsConstraints.keySet()) {
				this.addMorphingAgent(new MorphingAgent(data, input, this, this.myMatrix));
			}
			this.myMatrix.addNewData(data);
		}


		// Creation of the matrix DataUsed minus dataPerceived / dataCommunicated
		//this.subMatrix = this.myMatrix.constructSubmatrix(this.dataPerceived, this.decisionProcess.get(this.currentSituation).getExtero());
		//System.out.println(subMatrix);
		this.dpercom.clear();
		this.chosen.clear();


		this.findMorphling();
		System.out.println(this.morphlings);
		System.out.println(this.morphActifs);

		// Choix des exteroceptives
		int counter = 0;
		while(this.allConstraintNotSatisfied()) {
			this.currentSnapshot = new Snapshot();
			Collections.shuffle(this.morphActifs);

			// Links
			for(MorphingAgent morph : this.morphActifs) {
				Entity ent = this.currentSnapshot.addEntity(morph.getInput()+":"+morph.getData(), "Morph");
				//this.currentSnapshot.addEntity(ent);
			}
			for(String input : this.decisionProcess.get(this.currentSituation).getExtero()) {
				Entity ent = this.currentSnapshot.addEntity(input, "Input");
				//this.currentSnapshot.addEntity(ent);
			}
			for(String data : this.dataPerceived) {
				Entity ent = this.currentSnapshot.addEntity(data, "Data");
				//this.currentSnapshot.addEntity(ent);
			}
			for(int i =0; i < this.morphActifs.size();i++) {
				this.morphActifs.get(i).start(this.currentStep);
			}
			System.out.print("");
			this.currentSnapshot.addEntity("Counter:"+counter, "COUNT");
			//this.currentSnapshot.setSnapshotNumber(this.nbCycle);
			this.nbCycle++;
			this.links.addSnapshot(this.currentSnapshot);
			
			counter++;
		}


		// Decision des objectifs en fonction des donnees choisies


		this.planActions();


	}


	private boolean allConstraintNotSatisfied() {
		boolean satisfied = false;
		for(String input : this.decisionProcess.get(this.currentSituation).getExtero()) {
			if(!this.inputsConstraints.get(input).isSatisfied()) {
				System.out.println("IN NOT S:"+this.inputsConstraints.get(input)+"--"+this.inputsConstraints.get(input).getOffers());
				return true;
			}
		}
		for(String dataCom: this.dataPerceived) {
			if(!this.dataConstraint.get(dataCom).isSatisfied()) {
				System.out.println("DATA NOT S:"+this.dataConstraint.get(dataCom)+"--"+this.dataConstraint.get(dataCom).getOffers());
				return true;
			}
		}
		return satisfied;
	}


	private void findMorphling() {
		for(String in: this.decisionProcess.get(currentSituation).getExtero()) {
			for(MorphingAgent morph: this.morphlings.get(in)) {
				if(this.dataPerceived.contains(morph.getData()) ) {
					this.morphActifs.add(morph);
				}
			}
		}
	}


	private void planActions() {
		// Use the CAV function to get the planing using 
		//Result res = this.cav.computeDecision(this.chosen,this.dpercom, this.myObjectiveState).getLastRes();
		for(int i=0; i < this.window;i++) {
			Result res = new Result(this.currentStep+i, this.decisionProcess.get(this.currentSituation).computeMorph(effectorsBefore, this.currentStep+i));
			this.myPlaning.addRes(res);
		}

		System.out.println(myPlaning);
		System.out.println(this.lastPlaning);
		//this.myMatrix.updateMatrixFromSub(subMatrix);

		if(!this.myPlaning.isIdenticalToLast(this.lastPlaning)) {
			this.learn();
			System.out.println("LEARN");
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

		Snapshot snap = new Snapshot();
		this.links.addSnapshot(snap);
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


	/**
	 * Add the decision process with the situation
	 * @param dp
	 * @param s
	 */
	public void addDP(DecisionProcess dp, Situation s) {
		this.decisionProcess.put(s, dp);
	}

	/**
	 * 
	 * @param morph
	 */
	public void addMorphingAgent(MorphingAgent morph) {
		if(!this.morphlings.containsKey(morph.getInput())) {
			this.morphlings.put(morph.getInput(), new ArrayList<>());
		}
		this.morphlings.get(morph.getInput()).add(morph);
	}

	/**
	 * 
	 * @param dataName
	 * @return
	 */
	public Float askValue(String dataName) {

		if(this.dataPerceived.contains(dataName)) {
			return this.cav.getValueOfData(dataName);
		}
		else {
			return null;
		}
	}


	public List<String> getInputsInScenario() {
		return this.decisionProcess.get(this.currentSituation).getExtero();

	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cav == null) ? 0 : cav.hashCode());
		result = prime * result + myObjectiveState;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EffectorAgent other = (EffectorAgent) obj;
		if (cav == null) {
			if (other.cav != null)
				return false;
		} else if (!cav.equals(other.cav))
			return false;
		if (myObjectiveState != other.myObjectiveState)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	public DataUnicityConstraint getDataUnicityConstraint(String dataName) {
		return this.dataConstraint.get(dataName);
	}


	public InputConstraint getInputConstraint(String inputName) {
		return this.inputsConstraints.get(inputName);
	}


	public int getCurrentStep() {
		return this.currentStep;
	}


	public void sendValueToDecisionProcess(String inputName, float valueToSend) {

		this.decisionProcess.get(this.currentSituation).setValueOfInitInput(inputName, valueToSend);
	}

	public void sendValueToDecisionProcessLinks(MorphingAgent morph, float valueToSend) {
		//this.currentSnapshot.addRelation
		Relation r = this.currentSnapshot.addRelation(morph.getName(),morph.getInput(),morph.getName()+"To Input:"+morph.getInput(),  true, "applyToInput");
		
		Relation r2 = this.currentSnapshot.addRelation(morph.getName(),morph.getData(),morph.getName()+"To Data:"+morph.getData(),  true, "applyToData");
		
		//this.currentSnapshot.addRelation(r);
		//this.currentSnapshot.addRelation(r2);
		this.decisionProcess.get(this.currentSituation).setValueOfInitInput(morph.getInput(), valueToSend);
	}


	public List<MorphingAgent> getMorphlingActive() {
		return this.morphActifs;
	}


	public void saveExperiment() {
		/*
		//Helper to get connection with default parameters
		LinksConnection connection = LocalLinksConnection.getLocalConnexion();
		if(connection.experimentExist(this.name)) {
			connection.removeExperiment(this.name);
		}
		//Save the experiment 
		Link2DriverMarshaler.marshalling(connection, this.experiment, MarshallingMode.OVERRIDE_EXP_IF_EXISTING);
		//Don't forget to close the DB connection
		connection.close();*/
	}



}
