package fr.irit.smac.planification.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.generator.ShieldUser;
import fr.irit.smac.planification.Objective;
import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.Situation;
import fr.irit.smac.shield.model.Variable;

public class CAV {

	private static final int MAX_DATA_MISSING = 6;

	private String name;

	private Objective myObjective;

	private int nbObjectiveStates;


	private ComposedFunction composedFunction;

	private int nbEffectors;

	private int nbSituation;

	private Map<String,EffectorAgent> effectors;

	private float[] internalState;

	private List<String> internalData;

	private List<String> exteroData;


	private List<String> effectorData;

	private Situation[] situations;

	private Environment environment;

	// TODO
	private Set<String> dataPerceived;

	private Set<String> dataCom;

	private List<String> dataPerceivedInSituation;

	private List<String> dataCommunicatedInSituation;

	private Integer currentTime;

	private List<Objective> objectives;
	
	private int nbStep = 5;

	public CAV(String name, int nbObjectiveStates, int nbEffectors, int nbSituation) {
		this.name = name;
		this.currentTime = 0;

		if(nbObjectiveStates >  nbEffectors) {
			System.err.println("ERROR creation PlanificationFunction");
		}

		this.nbObjectiveStates = nbObjectiveStates;
		this.nbEffectors = nbEffectors;
		this.nbSituation = nbSituation;
		this.internalState = new float[this.nbObjectiveStates];
		this.situations = new Situation[this.nbSituation];

		this.environment = new Environment(10, 0, 200, 1);
		this.dataPerceived = new TreeSet<String>();
		//this.environment.getSubsetOfVariables(4);

		init();

	}

	private void init() {
		this.effectors = new TreeMap<String,EffectorAgent>();
		this.dataPerceivedInSituation = new ArrayList<String>();
		this.dataCommunicatedInSituation = new ArrayList<String>();
		this.objectives = new ArrayList<Objective>();
		this.internalData = new ArrayList<>();
		this.effectorData = new ArrayList<>();
		this.exteroData = new ArrayList<>();

		int i = 0;
		this.initComposedFunction();
		while(i < this.nbEffectors) {
			int j = 0;
			while(j < this.nbObjectiveStates && i < this.nbEffectors) {
				String ne = "EffAgent:"+j+(i/this.nbObjectiveStates);
				EffectorAgent eff = new EffectorAgent(ne,this, j,-2.0f);
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

	private void initComposedFunction() {
		Random rand = new Random();
		//Shield
		//this.shield = new ShieldUser();

		// TODO changer generation variable
		/*this.shield.initSetOfTypedVariableWithRange(15, 0, 200, "Type 1");
		this.shield.generateAllFunctionsOfVariable();*/

		//this.shield.initGeneratorOfComposedFunction();
		//List<String> variablesAvailable = new ArrayList<>(this.shield.getAllVariables());
		List<String> variablesAvailable = new ArrayList<>(this.environment.getAllVariable());
		List<String> input = new ArrayList<String>();
		//Etat interne
		for(int i = 0; i < 3; i++) {
			if(rand.nextInt()%2 == 0) {
				input.add("float");
			}
			else {
				input.add("int");
			}
			this.internalData.add(variablesAvailable.remove(rand.nextInt(variablesAvailable.size())));
		}
		// Etat effecteur
		input.add("float");
		for(int i =0; i < this.internalState.length;i++) {
			this.effectorData.add(variablesAvailable.remove(rand.nextInt(variablesAvailable.size())));
		}
		// nbstep
		input.add("int");
		// Donnees exteroceptive
		for(int i = 0; i < 4; i++) {
			if(rand.nextInt()%2 == 0) {
				input.add("float");
			}
			else {
				input.add("int");
			}
			String var = variablesAvailable.remove(rand.nextInt(variablesAvailable.size()));
			this.exteroData.add(var);

			//this.shield.generateSimilarData(var, 2);
			this.environment.generateSimilarData(var, 2);
		}

		List<String> outputs = new ArrayList<String>();
		outputs.add("int");
		outputs.add("float");
		this.environment.generateComposedFunction(this.name+"ComposedFunction", input, outputs, 3, 3);
		this.composedFunction = this.environment.getComposedFunctionWithName(this.name+"ComposedFunction");

	}
	
	private void startSituation() {
		Random rand = new Random();
		int idSituation = rand.nextInt(this.nbSituation);
		this.myObjective = this.situations[idSituation].getMyobjective();
	}

	private void computeObjective() {
		Random rand = new Random();
		int idSituation = rand.nextInt(this.nbSituation);
		this.myObjective = this.situations[idSituation].getMyobjective();
		this.objectives = this.situations[idSituation].getSubObjective();
	}

	private void planificationEffectors() {
		List<EffectorAgent> effShuffle = new ArrayList<EffectorAgent>(this.effectors.values());
		Collections.shuffle(effShuffle);
		Iterator<EffectorAgent> it = effShuffle.iterator();
		while(it.hasNext()) {
			it.next().start(this.currentTime);
		}
		this.currentTime++;
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
	public Planing computeDecision( Map<String,Number> exteroceptive, Integer effectorState) {
		Planing plan = new Planing();
		for(int i =0; i < this.objectives.size();i++) {
			Objective subObj = this.objectives.get(i);
			float valueState = this.internalState[effectorState];
		}
		return plan;
	}

	public void manageSituation() {
		this.senseData();
		this.communication();

		//this.computeObjective();
		this.startSituation();
		this.currentTime = 1;
		boolean over = false;
		while(!over) {
			this.senseData();
			this.planificationEffectors();
			this.currentTime++;
		}
	}


	/**
	 * Put all data that can be sense by the sensors in dataPerceivedInSituation
	 */
	private void senseData() {
		this.dataPerceivedInSituation.clear();
		/*for(String s : this.internalData) {
			this.dataPerceivedInSituation.add(s);
		}*/
		Random rand = new Random();
		/*List<String> dpShuf = new ArrayList<String>(this.dataPerceived);
		Collections.shuffle(dpShuf);
		this.dataPerceivedInSituation.addAll(dpShuf.subList(0, dpShuf.size()-1-rand.nextInt(MAX_DATA_MISSING)));*/
		for(String s : this.exteroData) {
			this.dataPerceivedInSituation.add(this.environment.getCopyOfVar(s));
		}
	}



	/**
	 * Gather all data communicated in dataCommunicatedInSituation
	 */
	private void communication() {
		this.dataCommunicatedInSituation.clear();
		this.dataCommunicatedInSituation.addAll(this.environment.getOtherData(this.dataPerceived));

	}

	public List<String> getDataPerceivedInSituation() {
		return this.dataPerceivedInSituation;

	}

	public List<String> getDataExteroceptiveInSituation() {
		return this.dataPerceivedInSituation;

	}

	public List<String> getDataComInSituation() {
		return this.dataCommunicatedInSituation;
	}

	public Collection<String> getDataPerceived() {
		return this.dataPerceived;
	}

	public void effect(int myObjectiveState, Result resAtTime) {
		this.internalState[myObjectiveState] += resAtTime.getValue();

	}

	public float getStateOfState(int myObjectiveState) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String args[]) {
		CAV cav = new CAV("CAV1", 2, 2, 3);
		
	}

	public String getDataEffector(int myObjectiveState) {
		return this.effectorData.get(myObjectiveState);
	}

	public List<String> getExteroceptiveData() {
		return this.exteroData;
	}
}
