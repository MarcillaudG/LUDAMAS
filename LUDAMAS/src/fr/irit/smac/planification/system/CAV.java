package fr.irit.smac.planification.system;

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

import com.sun.scenario.effect.Effect;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.generator.ShieldUser;
import fr.irit.smac.planification.Objective;
import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.Situation;
import fr.irit.smac.planification.agents.CoalitionAgent;
import fr.irit.smac.planification.agents.DataAgent;
import fr.irit.smac.planification.agents.DecisionProcess;
import fr.irit.smac.planification.agents.EffectorAgent;
import fr.irit.smac.planification.ui.MatrixUI;
import fr.irit.smac.planification.ui.MatrixUITable;
import fr.irit.smac.planification.ui.VisuEffector;
import fr.irit.smac.shield.model.Variable;

public class CAV {

	private static final int MAX_DATA_MISSING = 6;

	public static final int NB_EXTEROCEPTIVES = 4;

	private String name;

	private Objective myObjective;

	private int nbObjectiveStates;

	private int nbCopy;

	private ComposedFunction composedFunction;

	private int nbEffectors;

	private int nbSituation;

	private Map<String,EffectorAgent> effectors;

	private float[] internalState;

	private float[] internalEffect;

	private List<String> internalData;

	private List<String> exteroData;

	private Map<String, Integer> exteroDataCorrect;
	
	private Map<String,DataAgent> allDataAgents;
	
	private List<CoalitionAgent> allCoalitions;


	private List<String> effectorData;

	private Situation[] situations;

	private Situation currentSituation;

	//private Environment environment;
	
	private EnvironmentGeneral environment;

	// TODO
	private Set<String> dataPerceived;

	private Set<String> dataCom;

	private List<String> dataPerceivedInSituation;

	private List<String> dataCommunicatedInSituation;

	private Integer currentTime;

	private List<Objective> objectives;

	private int nbStep = 5;

	private VisuEffector mainWindow;

	private Integer nbVarEff;

	public CAV(String name, int nbEffectors, int nbSituation) {
		this.name = name;
		this.currentTime = 0;


		this.nbObjectiveStates = nbEffectors;
		this.nbEffectors = nbEffectors;
		this.nbSituation = nbSituation;
		this.internalState = new float[this.nbObjectiveStates];
		this.internalEffect = new float[this.nbObjectiveStates];
		this.situations = new Situation[this.nbSituation];

		this.environment = new EnvironmentDataset("C:\\\\Users\\\\gmarcill\\\\Desktop\\\\dataset_mock_enhanced.csv");
		this.dataPerceived = new TreeSet<String>();
		//this.environment.getSubsetOfVariables(4);

		initDataset();

	}

	/**
	 * Constructor using a dataset
	 * 
	 * @param name
	 * @param nbEffectors
	 * @param nbSituation
	 * @param nbVarEff
	 * @param nbCopy
	 * @param filePath
	 */
	public CAV(String name, Integer nbEffectors, Integer nbSituation, Integer nbVarEff, Integer nbCopy, String filePath) {

		this.name = name;
		this.currentTime = 0;
		this.nbCopy = nbCopy;
		this.nbVarEff = nbVarEff;

		this.nbObjectiveStates = nbEffectors;
		this.nbEffectors = nbEffectors;
		this.nbSituation = nbSituation;
		this.internalState = new float[this.nbObjectiveStates];
		this.internalEffect = new float[this.nbObjectiveStates];
		this.situations = new Situation[this.nbSituation];

		this.environment = new EnvironmentDataset(filePath);
		this.dataPerceived = new TreeSet<String>();
		//this.environment.getSubsetOfVariables(4);

		initDataset();
	}

	/**
	 * Constructor using shield
	 * @param name
	 * @param nbEffectors
	 * @param nbSituation
	 * @param nbVarEff
	 * @param nbCopy
	 * @param value5
	 */
	public CAV(String name, Integer nbEffectors, Integer nbSituation, Integer nbVarEff, Integer nbCopy, Integer nbVar) {
		this.name = name;
		this.currentTime = 0;
		this.nbCopy = nbCopy;
		this.nbVarEff = nbVarEff;

		this.nbObjectiveStates = nbEffectors;
		this.nbEffectors = nbEffectors;
		this.nbSituation = nbSituation;
		this.internalState = new float[this.nbObjectiveStates];
		this.internalEffect = new float[this.nbObjectiveStates];
		this.situations = new Situation[this.nbSituation];
		this.dataPerceived = new TreeSet<String>();
		this.environment = new Environment(nbVar, -100, 100, nbEffectors);
		initShield();
	}

	/**
	 * Method that use the shield generator variables
	 */
	private void initShield() {
		System.out.println("Init");
		this.effectors = new TreeMap<String,EffectorAgent>();
		this.dataPerceivedInSituation = new ArrayList<String>();
		this.dataCommunicatedInSituation = new ArrayList<String>();
		this.objectives = new ArrayList<Objective>();
		this.internalData = new ArrayList<>();
		this.effectorData = new ArrayList<>();
		this.exteroData = new ArrayList<>();
		this.exteroDataCorrect = new TreeMap<>();
		Random rand = new Random();
		List<String> variablesAvailable = new ArrayList<>(this.environment.getAllVariable());
		for(int i =0; i < 4;i++) {
			this.internalData.add(variablesAvailable.remove(rand.nextInt(variablesAvailable.size())));
		}
		this.exteroData.addAll(variablesAvailable);
		for(String s : this.exteroData) {
			//this.environment.generateSimilarData(s, 1);
			this.environment.generateSimilarDataDifferent(s,this.nbCopy);
		}
		/*int i = 0;
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
		}*/

		for(int i =0; i < this.nbObjectiveStates;i++) {
			EffectorAgent eff = new EffectorAgent("Effector:"+i, this, i, 10.0f);
			this.effectors.put(eff.getName(), eff);
		}

		for(int k = 0; k < this.nbObjectiveStates;k++) {
			this.internalState[k] = 0.0f;
		}
		List<String> dataInSituation = new ArrayList<String>();
		// TODO je n'en rajoute qu'une
		for(String s : this.exteroData) {
			dataInSituation.add(this.environment.getCopyOfVar(s));
		}
		dataInSituation.addAll(this.exteroData);
		for(int k = 0; k < this.nbSituation;k++) {
			Situation s = new Situation(k, rand.nextInt(30)+5, dataInSituation, 2);
			this.situations[k] = s;
			for(EffectorAgent eff : this.effectors.values()) {
				DecisionProcess dp = new DecisionProcess(s, eff, this.environment);
				eff.addDP(dp,s);
				Collections.shuffle(this.internalData);
				Collections.shuffle(this.exteroData);
				dp.initComposedFunction(this.internalData.subList(0, 2), this.exteroData.subList(0, 3), new ArrayList<String>());
			}
		}
	}
	
	/**
	 * Methode that use a dataset store in a file
	 */
	private void initDataset() {
		System.out.println("Init");
		
		// Init the collections
		this.effectors = new TreeMap<String,EffectorAgent>();
		this.dataPerceivedInSituation = new ArrayList<String>();
		this.dataCommunicatedInSituation = new ArrayList<String>();
		this.objectives = new ArrayList<Objective>();
		this.internalData = new ArrayList<>();
		this.effectorData = new ArrayList<>();
		this.exteroData = new ArrayList<>();
		this.exteroDataCorrect = new TreeMap<>();
		this.allDataAgents = new TreeMap<>();
		
		
		Random rand = new Random();
		List<String> variablesAvailable = new ArrayList<>(this.environment.getAllVariable());
		for(String s : this.environment.getAllVariable()) {
			if(s.contains("copy")) {
				variablesAvailable.remove(s);
			}
		}
		
		// set the internal data of the CAV
		for(int i =0; i < 2;i++) {
			this.internalData.add(variablesAvailable.remove(rand.nextInt(variablesAvailable.size())));
		}
		this.exteroData.addAll(variablesAvailable);

		// Create a number of effector equal to the number of state of the CAV
		for(int i =0; i < this.nbObjectiveStates;i++) {
			EffectorAgent eff = new EffectorAgent("Effector:"+i, this, i, 10.0f);
			this.effectors.put(eff.getName(), eff);
		}

		// I think it is useless
		for(int k = 0; k < this.nbObjectiveStates;k++) {
			this.internalState[k] = 0.0f;
		}
		
		// TODO Rework with more situation
		List<String> dataInSituation = new ArrayList<String>();
		for(String s : this.exteroData) {
			dataInSituation.add(this.environment.getCopyOfVar(s));
		}
		dataInSituation.addAll(this.exteroData);
		
		// Create a number of different situation
		for(int k = 0; k < this.nbSituation;k++) {
			Situation s = new Situation(k, rand.nextInt(30)+5, dataInSituation, 2);
			this.situations[k] = s;
			for(EffectorAgent eff : this.effectors.values()) {
				DecisionProcess dp = new DecisionProcess(s, eff, this.environment);
				eff.addDP(dp,s);
				Collections.shuffle(this.internalData);
				Collections.shuffle(this.exteroData);
				dp.initComposedFunction(this.internalData.subList(0, 2), this.exteroData.subList(0, 3), new ArrayList<String>());
			}
		}
		System.out.println("END");
	}

	private void initComposedFunction() {
		Random rand = new Random();
		//Shield
		//this.shield = new ShieldUser();

		// TODO changer generation variable

		//this.shield.initGeneratorOfComposedFunction();
		//List<String> variablesAvailable = new ArrayList<>(this.shield.getAllVariables());
		List<String> variablesAvailable = new ArrayList<>(this.environment.getAllVariable());
		List<String> input = new ArrayList<String>();
		int ind = 0;
		//Etat interne
		for(int i = 0; i < 3; i++) {
			if(i%2 == 0) {
				input.add("float");
			}
			else {
				input.add("int");
			}
			this.internalData.add(variablesAvailable.remove(rand.nextInt(variablesAvailable.size())));
			ind++;
		}
		// Etat effecteur
		input.add("float");
		for(int i =0; i < this.internalState.length;i++) {
			this.effectorData.add(variablesAvailable.remove(rand.nextInt(variablesAvailable.size())));
		}
		ind++;
		// nbstep
		//input.add("int");
		//ind++;
		// Donnees exteroceptive
		for(int i = 0; i < NB_EXTEROCEPTIVES; i++) {
			if(rand.nextInt()%2 == 0) {
				input.add("float");
			}
			else {
				input.add("int");
			}
			String var = variablesAvailable.remove(rand.nextInt(variablesAvailable.size()));
			this.exteroData.add(var);
			this.exteroDataCorrect.put(var, ind);

			//this.shield.generateSimilarData(var, 2);
			this.environment.generateSimilarData(var, 3);
			ind++;
		}

		List<String> outputs = new ArrayList<String>();
		outputs.add("int");
		outputs.add("float");
		this.environment.generateComposedFunction(this.name+"ComposedFunction", input, outputs, 3, 3);
		this.composedFunction = this.environment.getComposedFunctionWithName(this.name+"ComposedFunction");
		//System.out.println("END ICF");

	}

	/**
	 * Choose randomly a situation and init it
	 */
	private void startSituation() {
		Random rand = new Random();
		this.currentTime =0;
		int idSituation = rand.nextInt(this.nbSituation);
		this.currentSituation = this.situations[idSituation];
		this.myObjective = this.situations[idSituation].getMyobjective();
		this.currentSituation.startSituation2();
		
		for(EffectorAgent eff: this.effectors.values()) {
			eff.initSituation();
		}
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
	public Planing computeDecision( List<String> chosen, Map<String,Float> exteroceptive, Integer effectorState) {
		Planing plan = new Planing();
		// ajout des valeurs pour chaque entree de la fonction
		int i = 0;
		// ajout des donnees prioceptives
		for(int j =0; j < this.internalData.size();j++) {
			//this.composedFunction.setInitInput(i,(float)this.environment.getValueOfVariableWithName(this.internalData.get(j)));
			this.currentSituation.setInitInputCF(i,(float)this.environment.getValueOfVariableWithName(this.internalData.get(j)));
			i++;
		}

		// ajout de l'etat effecteur
		//this.composedFunction.setInitInput(i, (float)this.environment.getValueOfVariableWithName(this.effectorData.get(effectorState)));
		this.currentSituation.setInitInputCF(i,(float)this.environment.getValueOfVariableWithName(this.effectorData.get(effectorState)));
		i++;

		// ajout du step courant
		//this.composedFunction.setInitInput(i, this.currentTime);
		//this.currentSituation.setInitInputCF(i, this.currentTime);
		//i++;


		// ajout des donnees Exteroceptive
		for(int j =0; j < chosen.size();j++) {
			//this.composedFunction.setInitInput(i,(float)this.environment.getValueOfVariableWithName(chosen.get(j)));
			this.currentSituation.setInitInputCF(i,(float)this.environment.getValueOfVariableWithName(chosen.get(j)));
			i++;
		}
		this.currentSituation.compute();

		//System.out.println("NBSTEPI1:"+this.currentSituation.getCf().getOutput(0).getValue());
		//System.out.println("Value to achieve:"+this.currentSituation.getCf().getOutput(1).getValue());
		plan.addRes(new Result((int) this.composedFunction.getOutput(0).getValue(),(float) this.composedFunction.getOutput(1).getValue()));

		return plan;
	}

	/**
	 * The global method use to pass a situation
	 */
	public void manageSituation() {
		this.senseData();
		this.communication();

		//this.computeObjective();
		this.startSituation();
		this.currentTime = 0;
		boolean over = false;
		while(this.currentTime < this.currentSituation.getTime()) {
			this.senseData();
			this.planificationEffectors();
			this.currentTime++;

		}
		for(EffectorAgent eff : this.effectors.values()) {
			eff.saveExperiment();
		}
	}




	/**
	 * Put all data that can be sense by the sensors in dataPerceivedInSituation
	 */
	private void senseData() {
		this.dataPerceivedInSituation.clear();
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
		this.internalEffect[myObjectiveState] = resAtTime.getValue();
		this.internalState[myObjectiveState] += this.internalEffect[myObjectiveState];

	}


	public String getDataEffector(int myObjectiveState) {
		return this.effectorData.get(myObjectiveState);
	}

	public List<String> getExteroceptiveData() {
		return this.exteroData;
	}

	public float getValueOfState(int myObjectiveState) {
		return this.internalState[myObjectiveState];
	}

	public float getValueOfEffect(int myObjectiveState) {
		return this.internalEffect[myObjectiveState];
	}

	public Collection<? extends String> getInformationAvailable(int myObjectiveState) {
		return this.currentSituation.getInformationAvailable(this.internalState[myObjectiveState]);
	}

	public Situation getCurrentSituation() {
		return this.currentSituation;
	}

	public int getCurrentStep() {
		return this.nbStep;
	}

	public Float getValueOfData(String dataName) {
		return (float) this.environment.getValueOfVariableWithName(dataName);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		CAV other = (CAV) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}



	public static void main(String args[]) {
		CAV cav = new CAV("CAV1", 1, 2);
		/*cav.startSituation();
		cav.senseData();
		cav.planificationEffectors();*/
		int i =0;
		while(i < 1000) {
			cav.manageSituation();
			i++;
			if(i!=0 && i % 50 == 0) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			cav.environment.generateNewValues(i);
		}
	}

	public void generateNewValues(int i) {
		this.environment.generateNewValues(i);
	}

	public void setMainWindow(VisuEffector visuEffector) {
		this.mainWindow = visuEffector;
		
	}

	public int sendUI(MatrixUITable myUI) {
		if(this.mainWindow != null) {
			this.mainWindow.addEff(myUI);
			return 1;
		}
		return 0;
	}
	
	public int getCurrentTime() {
		return this.currentTime;
	}

	public void createCoalition(String dataName, String asker) {
		// TODO Auto-generated method stub
		
	}

	public DataAgent addDataAgentToCoalition(String asker, CoalitionAgent coalition) {
		this.allDataAgents.get(asker).mergeToCoalition(coalition);
		return this.allDataAgents.get(asker);
	}

	/**
	 * 
	 * @param coal
	 */
	public void coalitionDestroyed(CoalitionAgent coal) {
		this.allCoalitions.remove(coal);
	}

}
