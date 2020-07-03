package fr.irit.smac.planification.system;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.sun.scenario.effect.Effect;

import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.core.Links;
import fr.irit.smac.generator.ShieldUser;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.planification.Objective;
import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.Situation;
import fr.irit.smac.planification.agents.CoalitionAgent;
import fr.irit.smac.planification.agents.DataAgent;
import fr.irit.smac.planification.agents.DataMorphAgent;
import fr.irit.smac.planification.agents.EffectorAgent;
import fr.irit.smac.planification.generic.CompetitiveAgent;
import fr.irit.smac.planification.matrix.InputConstraint;
import fr.irit.smac.planification.matrix.Matrix;
import fr.irit.smac.planification.ui.MatrixUI;
import fr.irit.smac.planification.ui.MatrixUITable;
import fr.irit.smac.planification.ui.VisuEffector;
import fr.irit.smac.shield.model.Variable;

public class CAV {

	private static final int MAX_DATA_MISSING = 6;

	public static final int NB_EXTEROCEPTIVES = 4;

	public static final int WINDOW = 5;

	private String name;

	private Objective myObjective;

	private int nbObjectiveStates;

	private int nbCopy;

	private ComposedFunction composedFunction;

	private int nbEffectors;

	private int nbSituation;

	//private Map<String,EffectorAgent> effectors;

	private Map<String,Effector> effectors;

	private float[] internalState;

	private float[] internalEffect;

	private List<String> internalData;

	private List<String> exteroData;

	private Map<String, Integer> exteroDataCorrect;

	private Map<String,DataAgent> allDataAgents;

	private List<CoalitionAgent> allCoalitions;

	private Map<String,List<CompetitiveAgent>> competitiveActives;

	private Map<String,InputConstraint> inputConstraints;


	private List<String> effectorData;

	private List<String> allInputs;

	private Situation[] situations;

	private Situation currentSituation;

	//private Environment environment;

	private EnvironmentGeneral environment;

	// TODO
	private Set<String> dataPerceived;

	private Set<String> dataCom;

	private Set<String> dataPerceivedInSituation;

	private List<String> dataCommunicatedInSituation;

	private Integer currentTime;

	private List<Objective> objectives;


	private VisuEffector mainWindow;

	private Integer nbVarEff;

	private Map<String,Planing> planningSubProcess;

	private Planing lastPlaning;

	private Planing myPlaning;

	private Matrix matrix;

	private MatrixUITable matrixTable;

	private Links links;

	private List<CoalitionAgent> coalitionsToRemove;

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
		Date date = new Date(System.currentTimeMillis());
		this.name = name+"->"+filePath+":"+date;
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

		initDatasetCoalition();
	}
	
	/**
	 * Constructor using a noised dataset and its not noised one
	 * 
	 * @param name
	 * @param nbEffectors
	 * @param nbSituation
	 * @param nbVarEff
	 * @param nbCopy
	 * @param filePath
	 * @param filePathNoised
	 */
	public CAV(String name, Integer nbEffectors, Integer nbSituation, Integer nbVarEff, Integer nbCopy, String filePath, String filePathNotNoised) {
		Date date = new Date(System.currentTimeMillis());
		this.name = name+"->"+filePath+":"+date;
		this.currentTime = 0;
		this.nbCopy = nbCopy;
		this.nbVarEff = nbVarEff;

		this.nbObjectiveStates = nbEffectors;
		this.nbEffectors = nbEffectors;
		this.nbSituation = nbSituation;
		this.internalState = new float[this.nbObjectiveStates];
		this.internalEffect = new float[this.nbObjectiveStates];
		this.situations = new Situation[this.nbSituation];

		try {
			this.environment = new EnvironmentDataset(filePath, filePathNotNoised);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dataPerceived = new TreeSet<String>();
		//this.environment.getSubsetOfVariables(4);
		

		initDatasetCoalition();
		
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
		//this.effectors = new TreeMap<String,EffectorAgent>();
		this.dataPerceivedInSituation = new TreeSet<String>();
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

		/*for(int i =0; i < this.nbObjectiveStates;i++) {
			EffectorAgent eff = new EffectorAgent("Effector:"+i, this, i, 10.0f);
			this.effectors.put(eff.getName(), eff);
		}*/

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
			/*for(EffectorAgent eff : this.effectors.values()) {
				DecisionProcess dp = new DecisionProcess(s, eff, this.environment);
				eff.addDP(dp,s);
				Collections.shuffle(this.internalData);
				Collections.shuffle(this.exteroData);
				dp.initComposedFunction(this.internalData.subList(0, 2), this.exteroData.subList(0, 3), new ArrayList<String>());
			}*/
		}
	}

	/**
	 * Methode that use a dataset store in a file
	 */
	private void initDataset() {
		System.out.println("Init");

		// Init the collections
		//this.effectors = new TreeMap<String,EffectorAgent>();
		this.dataPerceivedInSituation = new TreeSet<String>();
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

		// With coalition
		for(String s : variablesAvailable) {
			this.allDataAgents.put(s, new DataAgent(this, s, variablesAvailable));
		}


		// set the internal data of the CAV
		for(int i =0; i < 2;i++) {
			this.internalData.add(variablesAvailable.remove(rand.nextInt(variablesAvailable.size())));
		}
		this.exteroData.addAll(variablesAvailable);

		// Create a number of effector equal to the number of state of the CAV
		/*for(int i =0; i < this.nbObjectiveStates;i++) {
			EffectorAgent eff = new EffectorAgent("Effector:"+i, this, i, 10.0f);
			this.effectors.put(eff.getName(), eff);
		}*/

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
			/*for(EffectorAgent eff : this.effectors.values()) {
				DecisionProcess dp = new DecisionProcess(s, eff, this.environment);
				eff.addDP(dp,s);
				Collections.shuffle(this.internalData);
				Collections.shuffle(this.exteroData);
				dp.initComposedFunction(this.internalData.subList(0, 2), this.exteroData.subList(0, 3), new ArrayList<String>());
			}*/
		}
		System.out.println("END");
	}

	private void initDatasetCoalition() {
		System.out.println("Init");
		
		this.links = new Links(this.name,"C:\\Users\\gmarcill\\git\\LUDAMAS\\LUDAMAS\\linksCoal.css");
		//this.links.deleteExperiment(name);

		// Init the collections
		this.effectors = new TreeMap<String,Effector>();
		this.dataPerceivedInSituation = new TreeSet<String>();
		this.dataCommunicatedInSituation = new ArrayList<String>();
		this.objectives = new ArrayList<Objective>();
		this.internalData = new ArrayList<>();
		this.effectorData = new ArrayList<>();
		this.exteroData = new ArrayList<>();
		this.exteroDataCorrect = new TreeMap<>();
		this.allDataAgents = new TreeMap<>();
		this.inputConstraints = new TreeMap<>();
		this.competitiveActives = new TreeMap<>();
		this.planningSubProcess = new TreeMap<>();
		this.allCoalitions = new ArrayList<>();
		this.coalitionsToRemove = new ArrayList<>();

		Random rand = new Random();
		List<String> variablesAvailable = new ArrayList<>(this.environment.getAllVariable());
		for(String s : this.environment.getAllVariable()) {
			if(s.contains("copy")) {
				variablesAvailable.remove(s);
			}
		}

		// to keep in mind initial
		this.allInputs = new ArrayList<>(variablesAvailable);

		// With coalition
		for(String s : variablesAvailable) {
			this.allDataAgents.put(s, new DataAgent(this, s, variablesAvailable));
			this.inputConstraints.put(s,new InputConstraint(s));
		}


		// set the internal data of the CAV
		for(int i =0; i < 2;i++) {
			this.internalData.add(variablesAvailable.remove(rand.nextInt(variablesAvailable.size())));
		}
		this.exteroData.addAll(variablesAvailable);

		// Create a number of effector equal to the number of state of the CAV
		for(int i =0; i < this.nbObjectiveStates;i++) {
			Effector eff = new Effector("Effector:"+i, this, i);
			this.effectors.put(eff.getName(), eff);
		}

		// I think it is useless
		for(int k = 0; k < this.nbObjectiveStates;k++) {
			this.internalState[k] = 0.0f;
		}

		// TODO Rework with more situation
		List<String> dataInSituation = new ArrayList<String>();
		for(String s : this.exteroData) {
			//dataInSituation.add(this.environment.getCopyOfVar(s));
			dataInSituation.addAll(this.environment.getAllCopyOfVar(s));
		}
		dataInSituation.addAll(this.exteroData);

		// Create a number of different situation
		for(int k = 0; k < this.nbSituation;k++) {
			Situation s = new Situation(k, rand.nextInt(30)+5, dataInSituation, 2);
			this.situations[k] = s;
			for(Effector eff : this.effectors.values()) {
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

		for(Effector eff: this.effectors.values()) {
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
		List<Effector> effShuffle = new ArrayList<Effector>(this.effectors.values());
		Collections.shuffle(effShuffle);
		Iterator<Effector> it = effShuffle.iterator();
		while(it.hasNext()) {
			it.next().cycle();
		}
	}
	
	private void planificationEffectorsOracle() {
		List<Effector> effShuffle = new ArrayList<Effector>(this.effectors.values());
		Collections.shuffle(effShuffle);
		Iterator<Effector> it = effShuffle.iterator();
		while(it.hasNext()) {
			it.next().cycleOracle();
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

		//this.computeObjective();
		this.startSituation();
		this.currentTime = 0;
		boolean over = false;
		while(this.currentTime < this.currentSituation.getTime()) {

			//Perception
			this.senseData();

			// agents cycle
			this.chooseValuesForEffector();

			// planification
			this.planificationEffectors();

			//Ma planification
			this.lastPlaning = this.myPlaning;
			this.myPlaning = new Planing();
			for(int i = 0 ; i < CAV.WINDOW;i++) {
				float res = 0.0f;
				for(String effect : this.planningSubProcess.keySet()) {
					res += this.planningSubProcess.get(effect).getResAtTime(this.getCurrentTime()+i).getValue();
				}
				this.myPlaning.addRes(new Result(this.getCurrentTime()+i, res));
			}
			
			
			
			// TODO visu Difference
			// TODO NB replaning
			// TODO Result final
			
			
			//TODO historiques

			this.currentTime++;

		}
		
		//True planning

		// rerun the decision process
		this.planificationEffectorsOracle();
		
		// Look at the real planing
		Planing truePlaning = new Planing();
		for(int i = 0 ; i < this.currentSituation.getTime();i++) {
			float res = 0.0f;
			for(String effect : this.planningSubProcess.keySet()) {
				res += this.planningSubProcess.get(effect).getResAtTime(i).getValue();
			}
			truePlaning.addRes(new Result(i, res));
		}
		
		learnFromSituation();
		for(CoalitionAgent coal : this.allCoalitions) {
			coal.lookForOtherCoalition();
		}
		for(CoalitionAgent coal: this.coalitionsToRemove) {
			this.allCoalitions.remove(coal);
		}
		this.coalitionsToRemove.clear();

		this.linksManagement();
		//UI
		//this.updateMatrix();
		
		//Links
		

		/*for(EffectorAgent eff : this.effectors.values()) {
			eff.saveExperiment();
		}*/
	}


	/**
	 * Manage links
	 */
	private void linksManagement() {
		Snapshot snap = new Snapshot();
		
		
		for(String data : this.dataPerceivedInSituation) {
			snap.addEntity(data, "DATAACTIVE");
			snap.getEntity(data).addOneAttribute("Value", "Value", this.environment.getValueOfVariableWithName(data));
			for(DataMorphAgent morph : this.allDataAgents.get(data).getAllMorphs()) {
				snap.getEntity(data).addOneAttribute("USEFULNESS", morph.getInput(), morph.getUsefulness());
				snap.getEntity(data).addOneAttribute("MORPH", morph.getInput(), morph.morph(this.environment.getValueOfVariableWithName(data)));
				snap.getEntity(data).addOneAttribute("ERROR", morph.getInput(), morph.getError());
			}
		}
		List<String> dataInactive = new ArrayList<>(this.allDataAgents.keySet());
		dataInactive.removeAll(this.dataPerceivedInSituation);
		
		for(String inact: dataInactive) {
			snap.addEntity(inact,"DATAINACTIVE");
			for(DataMorphAgent morph : this.allDataAgents.get(inact).getAllMorphs()) {
				snap.getEntity(inact).addOneAttribute("USEFULNESS", morph.getInput(), morph.getUsefulness());
			}
		}
		
		for(CoalitionAgent coal : this.allCoalitions) {
			snap.addEntity(coal.getName(), "COALITION");
			for(String dataInCoal : coal.getAllData()) {
				snap.addRelation(dataInCoal, coal.getName(), dataInCoal +" submissed to "+ coal.getName(),true, "SUBMISSED");
			}
		}

		for(Effector eff : this.effectors.values()) {
			snap.addEntity(eff.getName(), "EFFECTOR");
			int i = 0;
			for(String inpu : eff.getDecisionProcess(this.currentSituation).getExtero()) {
				snap.getEntity(eff.getName()).addOneAttribute("INPUTS", "input"+i, inpu);
				String nameData = this.inputConstraints.get(inpu).getOffers().get(0).getAgent().getCompetitiveName();
				snap.addRelation(nameData, eff.getName(), nameData + "used for "+ inpu, true, "USED");
				//snap.getRelation(nameData + "used for "+ inpu).addOneAttribute("Value", this.inputConstraints.get(inpu).getOffers().get(0).);
				i++;
			}
		}
		this.links.addSnapshot(snap);
		
	}

	private void updateMatrix() {
		if(this.matrix == null) {
			this.matrix = new Matrix(this,this.allInputs);
		}
		this.matrix.addAllNewData(this.allDataAgents.values());

		for(DataAgent data : this.allDataAgents.values()) {
			data.updateMatrix(this.matrix);
		}

		if(this.matrixTable == null) {
			this.matrixTable = new MatrixUITable(this.matrix);
			this.mainWindow.addMatrix(matrixTable);
		}
		
		
		this.matrixTable.updateUI();
	}

	private void learnFromSituation() {
		for(DataAgent data : this.allDataAgents.values()) {
			if(this.dataPerceivedInSituation.contains(data.getDataName()))
			data.sendFeedBackToMorphs(true);
		}
	}

	/**
	 * Start the decision for the agent to apply for input
	 */
	private void chooseValuesForEffector() {
		for(InputConstraint constr : this.inputConstraints.values()) {
			constr.restart();
		}

		this.competitiveActives.clear();
		for(String s: this.allInputs) {
			this.competitiveActives.put(s, new ArrayList<>());
		}

		List<String> dataAgentsActives = new ArrayList<>(this.dataPerceivedInSituation);
		Collections.shuffle(dataAgentsActives);

		// management of coalition
		for(String ag : dataAgentsActives) {
			this.allDataAgents.get(ag).cycle();
		}

		// put all conpetitive agent together
		List<CompetitiveAgent> allCompets = new ArrayList<>(this.allCoalitions);
		for(String ag : dataAgentsActives) {
			//allCompets.addAll(this.allDataAgents.get(ag).getAllMorphActives());
			allCompets.addAll(this.allDataAgents.get(ag).getAllMorphInCompet());
			for(CompetitiveAgent comp : this.allDataAgents.get(ag).getAllMorphInCompet()) {
				this.competitiveActives.get(comp.getInput()).add(comp);
			}
		}
		boolean satisfied = false;

		// look which constraint are to look at
		List<InputConstraint> inputConstrActive = new ArrayList<>();
		for(String input : this.getInputInSituation()) {
			inputConstrActive.add(this.inputConstraints.get(input));
		}


		// choose the competitive agent for an input
		while(!satisfied) {
			for(CompetitiveAgent compet : allCompets) {
				compet.cycleOffer();
			}
			satisfied = true;
			for(InputConstraint constr : inputConstrActive) {
				if(!constr.isSatisfied()) {
					satisfied = false;
				}
			}
			for(String data: dataAgentsActives) {
				if(!this.allDataAgents.get(data).getDataUnicityConstraint().isSatisfied()) {
					satisfied = false;
				}
			}
		}
	}

	/**
	 * Put all data that can be sense by the sensors in dataPerceivedInSituation
	 * Create new DataAgent
	 */
	private void senseData() {
		this.dataPerceivedInSituation.clear();
		this.dataPerceivedInSituation.addAll(this.currentSituation.getInformationAvailable(this.currentTime));
		if(!this.allDataAgents.keySet().containsAll(this.dataPerceivedInSituation)) {
			List<String> missingData = new ArrayList<>(this.dataPerceivedInSituation);
			missingData.removeAll(this.allDataAgents.keySet());
			for(String missing : missingData) {
				this.allDataAgents.put(missing,new DataAgent(this, missing, this.allInputs));
			}
		}
	}




	public Set<String> getDataPerceivedInSituation() {
		return this.dataPerceivedInSituation;

	}

	public Set<String> getDataExteroceptiveInSituation() {
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

	/**
	 * Return the value of the given data
	 * 
	 * @param dataName
	 * 
	 * @return the float value
	 */
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

	/**
	 * Create a coalition with the two data initiating
	 * 
	 * @param dataName
	 * 		the first data
	 * @param asker
	 * 		the second data
	 */
	public void createCoalition(String dataName, String asker) {
		int idMax = 0;
		if(this.allCoalitions.size()>0) {
			idMax = this.allCoalitions.get(this.allCoalitions.size()-1).getID() + 1;
		}
		this.allCoalitions.add(new CoalitionAgent(idMax, this, this.allDataAgents.get(dataName), this.allDataAgents.get(asker)));

	}

	public DataAgent addDataAgentToCoalition(String asker, CoalitionAgent coalition) {
		this.allDataAgents.get(asker).mergeToCoalition(coalition);
		return this.allDataAgents.get(asker);
	}

	/**
	 * Remove the no longer useful coalition
	 * Make submissed agent decide again
	 * @param coal
	 * @param set 
	 */
	public void coalitionDestroyed(CoalitionAgent coal, Set<String> agentToDecide) {
		this.coalitionsToRemove.add(coal);
		this.allCoalitions.remove(coal);
		for(String agent : agentToDecide) {
			this.allDataAgents.get(agent).cycle();
		}
	}
	

	/**
	 * Remove the no longer useful coalition
	 * 
	 * @param coalitionAgent
	 */
	public void coalitionDestroyed(CoalitionAgent coalitionAgent) {
		this.coalitionsToRemove.add(coalitionAgent);
	}

	/**
	 * Return all input for the current situation
	 * 
	 * @return a set of all the names of input
	 */
	public Collection<? extends String> getInputInSituation() {
		Set<String> res = new TreeSet<>();
		for(Effector eff : this.effectors.values()) {
			res.addAll(eff.getDecisionProcess(this.currentSituation).getExtero());
		}
		return res;
	}

	public void sendProposition(String input, float proposition) {
		// TODO Auto-generated method stub

	}

	public boolean applyForCoalition(String dataName, float bestUseful, String otherData) {
		if(dataName.equals(otherData)) {
			return false;
		}
		return this.allDataAgents.get(otherData).proposeCoalition(dataName,bestUseful) ;			
	}

	/**
	 * Return the inputconstraint linked to the input
	 * 
	 * @param input
	 * 
	 * 
	 * @return the input constraint
	 */
	public InputConstraint getInputConstraint(String input) {
		return this.inputConstraints.get(input);
	}

	/**
	 * Return the list of all competitive agent for an input
	 * 
	 * @param inputName
	 * 		Then name of the input
	 * @return a list of competitive agent
	 */
	public List<CompetitiveAgent> getCompetitiveAgentActives(String inputName) {
		return this.competitiveActives.get(inputName);
	}

	public Float getValueForInput(String input) {
		if(this.inputConstraints.get(input).getOffers().get(0).getAgent() != null) {
			return this.inputConstraints.get(input).getOffers().get(0).getAgent().getValue();
		}
		else {
			return this.inputConstraints.get(input).getOffers().get(0).getValue();
		}
	}

	public void sendPlanning(String name, Planing plan) {
		this.planningSubProcess.put(name,plan);
	}

	public String getName() {
		return this.name;
	}

	public String getMorphLR(String input, String data) {
		return this.allDataAgents.get(data).askMorphLR(input);
	}

	public Collection<? extends CoalitionAgent> getOtherCoalitionAgent(CoalitionAgent coal) {
		List<CoalitionAgent> res = new ArrayList<>(this.allCoalitions);
		res.remove(coal);
		
		return res;
	}

	public boolean isACoalitionWithMe(DataAgent dataAgent) {
		for(CoalitionAgent coal : this.allCoalitions) {
			if(coal.getAllData().contains(dataAgent.getDataName())) {
				System.out.println(coal);
				return true;
			}
		}
		return false;
	}

	public Float getTrueValueForInput(String input) {
		return this.environment.getValueForFeedbackWithName(input);
	}



}
