package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.Situation;
import fr.irit.smac.planification.matrix.DataUnicityConstraint;
import fr.irit.smac.planification.matrix.Input;
import fr.irit.smac.planification.matrix.InputConstraint;
import fr.irit.smac.planification.matrix.Matrix;
import fr.irit.smac.planification.system.CAV;
import fr.irit.smac.planification.system.DecisionProcess;
import fr.irit.smac.planification.ui.MatrixUI;
import fr.irit.smac.planification.ui.MatrixUITable;

public class EffectorAgent {

	private String name;

	private String dataInfluenced;

	// inputs
	private Set<String> dataUsed;

	// Data gathered by sensors
	private List<String> dataPerceived;

	private List<String> dataPerceivedLastCycle;

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


	private int nbCycle;


	public EffectorAgent(String name,CAV pf, int objState, float actionOpt) {
		this.cav = pf;
		this.name = name;
		this.myObjectiveState = objState;
		this.morphlings = new TreeMap<>();
		this.myMatrix = new Matrix(pf.getExteroceptiveData(),this );
		this.inputsConstraints = new TreeMap<>();
		for(String s : pf.getExteroceptiveData()) {
			//this.inputsConstraints.put(s, new InputConstraint(this, s));
			this.inputsConstraints.put(s, new InputConstraint(s));
		}

		this.bestAction = actionOpt;
		init();
	}


	private void init() {
		this.dataCommunicated = new TreeSet<String>();
		this.dataPerceived = new ArrayList<String>();
		this.dataPerceivedLastCycle = new ArrayList<>();
		this.dataUsed = new TreeSet<String>();
		this.objectives = new TreeMap<Integer,Float>();
		this.dpercom = new TreeMap<>();
		this.chosen = new ArrayList<>();
		this.composedFunctions = new ArrayList<>();
		this.decisionProcess = new HashMap<>();
		this.effectorsBefore = new ArrayList<>();
		this.dataConstraint = new TreeMap<>();

	}

	public void initSituation() {
		this.myPlaning = new Planing();
		this.lastPlaning = null;
		this.cost = 0.0f;
		this.morphActifs = new ArrayList<>();

		for(DataUnicityConstraint data: this.dataConstraint.values()) {
			data.restart();
		}

		for(InputConstraint input: this.inputsConstraints.values()) {
			input.restart();
		}

		this.dataPerceivedLastCycle.clear();
		this.dataPerceived.clear();
	}

	public void perceive() {

		this.dataPerceivedLastCycle.clear();
		this.dataPerceivedLastCycle.addAll(this.dataPerceived);
		// Recuperation des donnees percues
		this.dataPerceived.clear();
		this.morphActifs.clear();
		//this.dataPerceived.addAll(this.cav.getDataPerceivedInSituation());
		//this.dataPerceived.addAll(this.cav.getInformationAvailable(this.myObjectiveState));
		this.currentSituation = this.cav.getCurrentSituation();
		this.dataPerceived.addAll(this.currentSituation.getInformationAvailable(this.currentStep));


		//System.out.println("LAST:"+this.dataPerceivedLastCycle);
		//System.out.println("CURRENT:"+this.dataPerceived);

		// Recuperation des donnees communiquees
		this.dataCommunicated.clear();
		this.dataCommunicated.addAll(this.cav.getDataComInSituation());

		this.lastPlaning = new Planing(myPlaning);
		this.myPlaning= new Planing();
		//this.currentSnapshot = new Snapshot();
		this.nbCycle = 1;
		//this.experiment.addSnapshot(currentSnapshot);
	}

	public void decide() {
		//System.out.println("Decide");
		// CreationofDataConstraint
		List<String> constraintToADD = new ArrayList<>(this.dataPerceived);
		constraintToADD.removeAll(this.dataConstraint.keySet());
		for(String data: constraintToADD) {
			//this.dataConstraint.put(data, new DataUnicityConstraint(this, data));
			this.dataConstraint.put(data, new DataUnicityConstraint(data));
			for(String input: this.inputsConstraints.keySet()) {
				float value = 0.5f;
				if(data.equals(input)) {
					value = 1.0f;
				}
				this.addMorphingAgent(new MorphingAgent(data, input, this, this.myMatrix, value));
			}
			this.myMatrix.addNewData(data);
		}
		//System.out.println(this.myMatrix);


		// Creation of the matrix DataUsed minus dataPerceived / dataCommunicated
		//this.subMatrix = this.myMatrix.constructSubmatrix(this.dataPerceived, this.decisionProcess.get(this.currentSituation).getExtero());
		//System.out.println(subMatrix);
		this.dpercom.clear();
		this.chosen.clear();


		this.findMorphling();

		// Choix des exteroceptives
		if(this.dataPerceivedLastCycle.isEmpty() || !this.dataPerceivedLastCycle.containsAll(this.dataPerceived)) {
			do{
				//Snapshot s = new Snapshot();
				Collections.shuffle(this.morphActifs);

				// Links
				for(MorphingAgent morph : this.morphActifs) {
					//Entity ent = s.addEntity(morph.getInput()+":"+morph.getData(), "Morph");
					double value = this.myMatrix.getValueOfMorph(morph.getInput(),morph.getData());
					//s.getEntity(morph.getInput()+":"+morph.getData()).addOneAttribute("Usefulness","usefulness", value);
					//this.currentSnapshot.addEntity(ent);
				}
				for(String input : this.decisionProcess.get(this.currentSituation).getExtero()) {
					//Entity ent = s.addEntity("IN:"+input, "Input");
					//this.currentSnapshot.addEntity(ent);
				}
				for(String data : this.dataPerceived) {
					//Entity ent = s.addEntity("Data:"+data, "Data");
					//this.currentSnapshot.addEntity(ent);
				}
				for(int i =0; i < this.morphActifs.size();i++) {
					this.morphActifs.get(i).start(this.currentStep);
				}
				//System.out.print("");
				//s.addEntity("Counter:"+this.currentStep, "COUNT");
				//this.currentSnapshot.setSnapshotNumber(this.nbCycle);
				this.nbCycle++;

				//this.sendOfferToLinks(s);

				//this.links.addSnapshot(s);

			}while(this.allConstraintNotSatisfied());
		}
		for(String input : this.decisionProcess.get(this.currentSituation).getExtero()) {
			if(this.inputsConstraints.get(input).getOffers().isEmpty()) {
				//System.out.println(input);
			}
			this.myPlaning.setExteroChosen(input, this.inputsConstraints.get(input).getOffers().get(0).getAgent().getData());
		}


		// Decision des objectifs en fonction des donnees choisies


		this.planActions();


	}

	private boolean allConstraintNotSatisfied() {
		boolean satisfied = false;
		for(String input : this.decisionProcess.get(this.currentSituation).getExtero()) {
			if(!this.inputsConstraints.get(input).isSatisfied()) {
				return true;
			}
		}
		for(String dataCom: this.dataPerceived) {
			if(!this.dataConstraint.get(dataCom).isSatisfied()) {
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


	/**
	 * Create the planning according to the data chosen
	 * if the new planning is different, morphingAgent will learn
	 */
	private void planActions() {
		// Use the CAV function to get the planing using 
		//Result res = this.cav.computeDecision(this.chosen,this.dpercom, this.myObjectiveState).getLastRes();
		for(int i=0; i < this.window;i++) {
			Result res = new Result(this.currentStep+i, this.decisionProcess.get(this.currentSituation).computeMorph(effectorsBefore, this.currentStep+i));
			this.myPlaning.addRes(res);
		}

		//System.out.println(myPlaning);
		//System.out.println(this.lastPlaning);
		//this.myMatrix.updateMatrixFromSub(subMatrix);
		//float feed = this.myPlaning.isAlmostIdenticalToLast(this.lastPlaning);
		if(!this.myPlaning.isIdenticalToLast(this.lastPlaning)) {
			this.learn();

		}

		this.myMatrix.updateUI();
	}


	private void learn() {
		for(String in: this.myPlaning.getExteroChosen().keySet()) {
			if(!this.myPlaning.getExteroChosen().get(in).equals(this.lastPlaning.getExteroChosen().get(in)) && this.myPlaning.isUnderstandedInput(in)) {
				float valueNew = this.cav.getValueOfData(this.myPlaning.getExteroChosen().get(in));
				float valueOld = this.cav.getValueOfData(this.lastPlaning.getExteroChosen().get(in));
				//if(this.cav.getValueOfData(this.myPlaning.getExteroChosen().get(in)) != this.cav.getValueOfData(this.lastPlaning.getExteroChosen().get(in))){
				/*if(valueNew != valueOld) {
					this.myMatrix.setWeight(in, this.lastPlaning.getExteroChosen().get(in), 0.0f);
				}
				else {
					this.myMatrix.setWeight(in, this.lastPlaning.getExteroChosen().get(in), 1.0f);
				}*/
				MorphingAgent worst = null;
				MorphingAgent best = null;
				Float worstValue = null;
				Float bestValue = null;
				float correctValue = this.cav.getValueOfData(this.myPlaning.getExteroChosen().get(in));
				/*for(MorphingAgent morph : this.morphlings.get(in)) {
					if(morph.getData().equals(this.lastPlaning.getExteroChosen().get(in))) {
						morph.sendFeedback(correctValue, this.myPlaning.isTolerant(this.lastPlaning));
					}
					if(this.morphActifs.contains(morph)) {
						float valueMorph = morph.getPredict();
						if(bestValue == null || Math.abs(valueMorph-correctValue) < Math.abs(bestValue - correctValue)) {
							bestValue = valueMorph;
							best = morph;
						}
						if(!best.equals(morph) && (worstValue == null || Math.abs(valueMorph-correctValue) > Math.abs(worstValue - correctValue))) {
							worstValue = valueMorph;
							worst = morph;
						}
					}
				}*/
				for(MorphingAgent morph : this.morphlings.get(in)) {
					if(this.morphActifs.contains(morph)) {
						float valueMorph = morph.getPredict();
						morph.sendFeedback(correctValue, this.myPlaning.isTolerant(this.lastPlaning));
						if(bestValue == null || Math.abs(valueMorph-correctValue) < Math.abs(bestValue - correctValue)) {
							bestValue = valueMorph;
							best = morph;
						}
						if(!best.equals(morph) && (worstValue == null || Math.abs(valueMorph-correctValue) > Math.abs(worstValue - correctValue))) {
							worstValue = valueMorph;
							worst = morph;
						}
					}
				}
				/*if(best != null) {
					System.out.println("BEST:"+best+" for "+in);
					System.out.println();
					best.increaseUsefull();
				}
				if(worst != null) {
					System.out.println("WORST:"+worst+" for "+in);
					worst.decreaseUsefull();
				}*/
			}
		}
	}



	public void act(Integer time) {
		//System.out.println("Act");
		// Faire l'action suivante
		// TEST
		this.cav.effect(this.myObjectiveState,this.myPlaning.getResAtTime(time));
		//System.out.println("Avancement:"+time+"::"+this.cav.getValueOfState(myObjectiveState));
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



	/*private void sendOfferToLinks(Snapshot s) {
		for(String data : this.dataPerceived) {
			for(Offer off : this.dataConstraint.get(data).getOffers()) {
				Relation r2 = s.addRelation(off.getMorph().getName(),"Data:"+off.getMorph().getData(),off.getMorph().getName()+"To Data:"+off.getMorph().getData(),  true, "applyToData");
			}
		}

		for(String input : this.decisionProcess.get(this.currentSituation).getExtero()) {
			for(Offer off: this.inputsConstraints.get(input).getOffers()) {
				Relation r = s.addRelation(off.getMorph().getName(),"IN:"+off.getMorph().getInput(),off.getMorph().getName()+"To Input:"+off.getMorph().getInput(),  true, "applyToInput");
			}
		}
	}*/


	public void updateMatrix(String inputName, String dataName, float usefulness) {
		this.myMatrix.setWeight(inputName, dataName, usefulness);
	}


	public String getMorphValue(String input, String data) {
		for(MorphingAgent morph : this.morphlings.get(input)) {
			if(morph.getData().equals(data)) {
				return morph.getMorphLRFormula();
			}
		}
		return "MORPH NOT FOUND";
	}


	public int sendUI(MatrixUITable myUI) {
		return this.cav.sendUI(myUI) ;
	}


	public DecisionProcess getDecisionProcess(Situation current) {
		return this.decisionProcess.get(current);
	}


}
