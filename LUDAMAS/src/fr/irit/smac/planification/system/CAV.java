package fr.irit.smac.planification.system;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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


import fr.irit.smac.complex.ComposedFunction;
import fr.irit.smac.core.Links;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.planification.Planing;
import fr.irit.smac.planification.Result;
import fr.irit.smac.planification.Situation;
import fr.irit.smac.planification.agents.AVTAgent;
import fr.irit.smac.planification.agents.CoalitionAgent;
import fr.irit.smac.planification.agents.DataAgent;
import fr.irit.smac.planification.agents.DataMorphAgent;
import fr.irit.smac.planification.generic.CompetitiveAgent;
import fr.irit.smac.planification.matrix.InputConstraint;
import fr.irit.smac.planification.matrix.Offer;
import fr.irit.smac.planification.ui.MatrixUITable;
import fr.irit.smac.planification.ui.VisuEffector;

public class CAV {


	public static final int NB_EXTEROCEPTIVES = 4;

	public static final int WINDOW = 5;

	private String name;


	private int nbObjectiveStates;

	private int nbCopy;

	private ComposedFunction composedFunction;


	private int nbSituation;

	//private Map<String,EffectorAgent> effectors;

	private Map<String,Effector> effectors;



	private List<String> internalData;

	private List<String> exteroData;


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


	private Set<String> dataPerceivedInSituation;

	private List<String> dataCommunicatedInSituation;

	private Integer currentTime;



	private VisuEffector mainWindow;


	private Map<String,Planing> planningSubProcess;


	private Planing myPlaning;

	private Planing planingSituation;

	private Planing truePlaning;



	private Links links;

	private List<CoalitionAgent> coalitionsToRemove;

	private List<CoalitionAgent> coalitionsToAdd;


	private int cycle;

	private FileWriter resultingDataset;

	private FileWriter resultingCoalition;

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
		//this.name = name+"->"+filePath+":"+date;
		String[] splitFile = filePath.split("\\\\");
		System.out.println(splitFile[0]);
		this.name = name+"_"+splitFile[splitFile.length-1]+":"+date;
		this.currentTime = 0;
		this.nbCopy = nbCopy;

		this.nbObjectiveStates = nbEffectors;
		this.nbSituation = nbSituation;
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
		//this.name = name+"->"+filePath+":"+date;
		String[] splitFile = filePath.split("\\\\");
		this.name = name+"_"+splitFile[splitFile.length-1]+":"+date;
		this.currentTime = 0;
		this.nbCopy = nbCopy;

		this.nbObjectiveStates = nbEffectors;
		this.nbSituation = nbSituation;
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

		this.nbObjectiveStates = nbEffectors;
		this.nbSituation = nbSituation;
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
		this.internalData = new ArrayList<>();
		this.effectorData = new ArrayList<>();
		this.exteroData = new ArrayList<>();
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
	 * Set the file result
	 * @param string
	 */
	public void setResult(String path) {
		try {
			this.resultingDataset.close();
			this.resultingCoalition.close();
			this.resultingDataset = new FileWriter(new File(path+".csv"));
			this.resultingCoalition = new FileWriter(new File(path+"dp.csv"));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the param for the experiment
	 * 
	 * @param params
	 */
	public void setParams(Map<String,Number> params) {
		for(String param : params.keySet()) {
			if(param.equals("delta")) {
				CoalitionAgent.setParam(param, params.get(param));
			}

			if(param.equals("alpha")) {
				AVTAgent.setParam(param, params.get(param));
			}
			if(param.equals("beta")) {
				AVTAgent.setParam(param, params.get(param));
			}

			if(param.equals("gama")) {
				AVTAgent.setParam(param, params.get(param));
			}
			if(param.equals("adapt")) {
				DataMorphAgent.setParam(param, params.get(param));
			}
			if(param.equals("memory_size")) {
				DataMorphAgent.setParam(param, params.get(param));
			}
			if(param.equals("tolerance")) {
				DataMorphAgent.setParam(param, params.get(param));
			}
			if(param.equals("destroy_cycle")) {
				DataMorphAgent.setParam(param, params.get(param));
			}
			if(param.equals("destroy_crit")) {
				DataMorphAgent.setParam(param, params.get(param));
			}
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
		this.internalData = new ArrayList<>();
		this.effectorData = new ArrayList<>();
		this.exteroData = new ArrayList<>();
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

		Date date = new Date(System.currentTimeMillis());
		this.links = new Links(this.name,"C:\\Users\\gmarcill\\git\\LUDAMAS\\LUDAMAS\\linksCoal.css");
		this.links.createExperiment(this.name + "IN SITU", "C:\\Users\\gmarcill\\git\\LUDAMAS\\LUDAMAS\\linksCoal.css");
		//this.links.deleteExperiment(name);
		try {
			this.resultingDataset = new FileWriter(new File("C:\\Users\\gmarcill\\Documents\\Dataset\\Results\\result_Diff3.csv"));
			this.resultingCoalition = new FileWriter(new File("C:\\Users\\gmarcill\\Documents\\Dataset\\Results\\resultCoal_Diff3.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("ERROR 0");
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		// Init the collections
		this.effectors = new TreeMap<String,Effector>();
		this.dataPerceivedInSituation = new TreeSet<String>();
		this.dataCommunicatedInSituation = new ArrayList<String>();
		this.internalData = new ArrayList<>();
		this.effectorData = new ArrayList<>();
		this.exteroData = new ArrayList<>();
		this.allDataAgents = new TreeMap<>();
		this.inputConstraints = new TreeMap<>();
		this.competitiveActives = new TreeMap<>();
		this.planningSubProcess = new TreeMap<>();
		this.allCoalitions = new ArrayList<>();
		this.coalitionsToRemove = new ArrayList<>();
		this.coalitionsToAdd = new ArrayList<>();

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
			CoalitionAgent coal = new CoalitionAgent(this.allCoalitions.size(), this, this.allDataAgents.get(s));
			this.allCoalitions.add(coal);
		}


		// set the internal data of the CAV
		/*for(int i =0; i < 2;i++) {
			this.internalData.add(variablesAvailable.remove(rand.nextInt(variablesAvailable.size())));
		}*/
		this.exteroData.addAll(variablesAvailable);

		// Create a number of effector equal to the number of state of the CAV
		for(int i =0; i < this.nbObjectiveStates;i++) {
			Effector eff = new Effector("Effector:"+i, this, i);
			this.effectors.put(eff.getName(), eff);
		}


		// TODO Rework with more situation
		List<String> dataInSituation = new ArrayList<String>();
		for(String s : this.exteroData) {
			//dataInSituation.add(this.environment.getCopyOfVar(s));
			//dataInSituation.addAll(this.environment.getAllCopyOfVar(s));
			dataInSituation.addAll(this.environment.getAllCopyOfVar());
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
				//dp.initComposedFunction(this.internalData.subList(0, 2), this.exteroData.subList(0, 3), new ArrayList<String>());
				dp.initComposedFunction(this.internalData, this.exteroData.subList(0, this.exteroData.size()-6), new ArrayList<String>());
			}
		}
		System.out.println("END");
	}

	/**
	 * Choose randomly a situation and init it
	 */
	private void startSituation() {
		Random rand = new Random();
		this.currentTime =0;
		int idSituation = rand.nextInt(this.nbSituation);
		this.currentSituation = this.situations[idSituation];
		this.currentSituation.startSituationOneCopyMinimum();

		for(Effector eff: this.effectors.values()) {
			eff.initSituation();
		}

		for(DataAgent agent : this.allDataAgents.values()) {
			agent.startSituation();
		}

		for(InputConstraint constr : this.inputConstraints.values()) {
			constr.restart();
		}
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
	public void manageSituation(int cycle) {
		this.cycle = cycle;
		//this.computeObjective();
		this.startSituation();
		this.currentTime = 0;
		boolean over = false;
		this.planingSituation = new Planing();
		this.dataPerceivedInSituation.clear();

		//System.out.println("START");
		while(this.currentTime < this.currentSituation.getTime()) {


			//Perception
			this.senseData();
			if(this.currentTime == 0) {
				System.out.println(this.dataPerceivedInSituation);
			}


			// agents cycle
			this.chooseValuesForEffector();


			// planification
			this.planificationEffectors();


			//Ma planification
			this.myPlaning = new Planing();
			for(int i = 0 ; i < CAV.WINDOW;i++) {
				float res = 0.0f;
				/*for(String effect : this.planningSubProcess.keySet()) {
					res += this.planningSubProcess.get(effect).getResAtTime(this.getCurrentTime()+i).getValue();
				}*/
				for(String input : this.getInputInSituation()) {
					res+= this.inputConstraints.get(input).getOffers().get(0).getAgent().getValue();
				}
				this.myPlaning.addRes(new Result(this.getCurrentTime()+i, res));
			}


			boolean constraintHasChanged = false;
			for(InputConstraint constr : this.inputConstraints.values()) {
				if(constr.hasChanged()) {
					constraintHasChanged = true;
				}
			}
			this.planingSituation.addRes(this.myPlaning.getResAtTime(this.getCurrentTime()));
			for(String input : this.getInputInSituation()) {
				//this.planingSituation.getResAtTime(this.getCurrentTime()).addData(this.inputConstraints.get(input).getOffers().get(0).getAgent().toString());
				this.planingSituation.getResAtTime(this.getCurrentTime()).addData(buildStringForPlaning(this.inputConstraints.get(input).getOffers().get(0).getAgent()));
			}

			//System.out.println("END STEP");


			this.currentTime++;

		}
		int nbMorph = 0;
		for(DataAgent data : this.allDataAgents.values()) {
			nbMorph += data.getAllMorphs().size();
		}
		System.out.println("NBMORPH : -> "+nbMorph);

		//True planning


		//System.out.println("ORACLE");
		// rerun the decision process
		this.planificationEffectorsOracle();


		//System.out.println("REAL PLANING");
		// Look at the real planing
		this.truePlaning = new Planing();
		for(int i = 0 ; i < this.currentSituation.getTime();i++) {
			float res = 0.0f;
			/*for(String effect : this.planningSubProcess.keySet()) {
				res += this.planningSubProcess.get(effect).getResAtTime(i).getValue();
			}*/

			for(String input : this.getInputInSituation()) {
				res+= this.getTrueValueForInput(input);
			}
			this.truePlaning.addRes(new Result(i, res));
		}
		this.writePlaning(cycle);


		try {
			this.resultingCoalition.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.resultingDataset.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//System.out.println("LEARNING");
		// Gives feedback to agent
		learnFromSituation();


		this.linksManagement(this.name);
		// Coalition seek to merge
		for(CoalitionAgent coal : this.allCoalitions) {
			coal.evaluateCritData();
			if(coal.getAllData().size()>0) {
				coal.lookForOtherCoalition();
			}
		}


		for(CoalitionAgent coal : this.allCoalitions) {
			if(coal.getAllData().size() == 0) {
				this.coalitionsToRemove.add(coal);
			}
		}

		for(CoalitionAgent coal: this.coalitionsToRemove) {
			this.allCoalitions.remove(coal);
		}
		this.coalitionsToRemove.clear();

		for(CoalitionAgent coal: this.coalitionsToAdd) {
			this.allCoalitions.add(coal);
		}
		this.coalitionsToAdd.clear();


		this.writeResults(cycle);
		System.out.println("END CYCLE");

		//UI
		//this.updateMatrix();

		//Links


		/*for(EffectorAgent eff : this.effectors.values()) {
			eff.saveExperiment();
		}*/
	}

	/**
	 * Write the value computed by coalitions in a file
	 * 
	 * @param cycle
	 */
	private void writePlaning(int cycle) {
		if(cycle < 2) {
			for(String input: this.getInputInSituation()) {
				try {
					this.resultingCoalition.write(input+";");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				this.resultingCoalition.write("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(String input: this.allInputs) {
			try {
				if(this.getInputInSituation().contains(input)) {
					String toWrite = this.inputConstraints.get(input).getOffers().get(0).getAgent().getValue()+";";
					this.resultingCoalition.write(toWrite);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			this.resultingCoalition.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write results in a file
	 * @param cycle 
	 */
	private void writeResults(int cycle) {
		if(cycle < 2) {
			for(DataAgent data: this.allDataAgents.values()) {
				for(DataMorphAgent morph : data.getAllMorphs()) {
					try {
						this.resultingDataset.write(morph.getName()+";");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
				this.resultingDataset.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(DataAgent data: this.allDataAgents.values()) {
			for(DataMorphAgent morph : data.getAllMorphs()) {
				try {
					this.resultingDataset.write(morph.getValue()+";");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			this.resultingDataset.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String buildStringForPlaning(CompetitiveAgent competitiveAgent) {

		StringBuilder resultat = new StringBuilder();
		CoalitionAgent coalitionAgent = (CoalitionAgent) competitiveAgent;
		resultat.append(coalitionAgent.getData()+ " value: " + coalitionAgent.getValue() + " input: " + coalitionAgent.getInput() +"    DATA: ");
		Collection<? extends String> datas = coalitionAgent.getAllData();
		for(String data : datas) {
			resultat.append(data + "  ");
		}
		return resultat.toString();
	}


	/**
	 * Manage links
	 */
	private void linksManagement(String xpName) {
		Snapshot snap = new Snapshot();


		for(String data : this.dataPerceivedInSituation) {
			snap.addEntity(data, "DATAACTIVE");
			snap.getEntity(data).addOneAttribute("Value", "Value", this.environment.getValueOfVariableWithName(data));
			snap.getEntity(data).addOneAttribute("COALITION", "COAL", this.allDataAgents.get(data).getCoalition().getCompetitiveName());
			for(DataMorphAgent morph : this.allDataAgents.get(data).getAllMorphs()) {
				snap.getEntity(data).addOneAttribute("USEFULNESS", morph.getInput(), morph.getUsefulness());
				snap.getEntity(data).addOneAttribute("MORPH", morph.getInput(), morph.morph(this.environment.getValueOfVariableWithName(data)));
				snap.getEntity(data).addOneAttribute("ADAPTLIN", morph.getInput(), morph.morphingLinear(this.environment.getValueOfVariableWithName(data)));
				snap.getEntity(data).addOneAttribute("Formula", morph.getInput(), morph.getLinearFormula());
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
			snap.getEntity(coal.getName()).addOneAttribute("VALUE", "value", coal.getValue());

			snap.getEntity(coal.getName()).addOneAttribute("CRIT", "crit", coal.getCriticality());
			for(String dataInCoal : coal.getAllData()) {
				snap.addRelation(dataInCoal, coal.getName(), dataInCoal +" submissed to "+ coal.getName(),true, "SUBMISSED");
				snap.getEntity(coal.getName()).addOneAttribute("AVTAGENT", dataInCoal, coal.getAVTAgent(dataInCoal).getWeight());
			}
		}

		for(Effector eff : this.effectors.values()) {
			snap.addEntity(eff.getName(), "EFFECTOR");
			for(String inpu : eff.getDecisionProcess(this.currentSituation).getExtero()) {
				snap.getEntity(eff.getName()).addOneAttribute("INPUTS", "input -> " +inpu, this.getTrueValueForInput(inpu));
				String nameData = this.inputConstraints.get(inpu).getOffers().get(0).getAgent().getCompetitiveName();
				snap.addRelation(nameData, eff.getName(), nameData + "used for "+ inpu, true, "USED");
			}
		}
		this.links.addSnapshot(snap,xpName);

	}


	/**
	 * Gives the feedback to agents
	 */
	private void learnFromSituation() {
		// Learn how to morph into each other
		for(DataAgent data : this.allDataAgents.values()) {
			if(this.dataPerceivedInSituation.contains(data.getDataName()))
				data.sendFeedBackToMorphs(true);
		}


		// learn how to trust each other
		for(String input : this.getInputInSituation()) {
			if(this.inputConstraints.get(input).getOffers().get(0).getAgent() instanceof CoalitionAgent) {
				((CoalitionAgent)this.inputConstraints.get(input).getOffers().get(0).getAgent()).sendFeedbackToAVT(this.getTrueValueForInput(input));
			}
		}
	}



	/**
	 * Start the decision for the agent to apply for input
	 */
	private void chooseValuesForEffector() {

		//System.out.println("DEBUT CHOOSE");
		for(InputConstraint constr : this.inputConstraints.values()) {
			constr.newCycleOffer();
		}

		this.competitiveActives.clear();
		for(String s: this.allInputs) {
			this.competitiveActives.put(s, new ArrayList<>());
		}

		List<String> dataAgentsActives = new ArrayList<>(this.dataPerceivedInSituation);
		Collections.shuffle(dataAgentsActives);


		//System.out.println("DATAAGENT CYCLE --->> "+dataAgentsActives);
		// management of coalition
		for(String ag : dataAgentsActives) {
			this.allDataAgents.get(ag).cycle();
		}


		//System.out.println("ADD ALL COMPETS");
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


		//System.out.println("CONSTRAINT TO LOOK AT --->>> "+ allCompets);
		// look which constraint are to look at
		List<InputConstraint> inputConstrActive = new ArrayList<>();
		for(String input : this.getInputInSituation()) {
			inputConstrActive.add(this.inputConstraints.get(input));
			List<Offer> offerToRemove = new ArrayList<>();
			for(Offer offer : this.inputConstraints.get(input).getOffers()) {
				if(!allCompets.contains(offer.getAgent())) {
					//this.inputConstraints.get(input).removeOffer(offer);
					offerToRemove.add(offer);
				}
			}
			for(Offer offer : offerToRemove) {
				this.inputConstraints.get(input).removeOffer(offer);
			}
		}



		//System.out.println("RESOLUTION --->>> "+inputConstrActive);
		// choose the competitive agent for an input
		int i = 0;
		for(CompetitiveAgent compet : allCompets) {
			compet.prepareToNegociate();
		}

		//while(!satisfied) {
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
		i++;
		if(i > 20) {
			for(InputConstraint constr : inputConstrActive) {
				if(!constr.isSatisfied()) {
					System.out.println(constr.getOffers());
					for(Offer offer : constr.getOffers()) {
						System.out.println(offer.getAgent());
						for(String data : ((CoalitionAgent)offer.getAgent()).getAllData()) {
							System.out.println(this.allDataAgents.get(data).getCoalition());
							System.out.println(this.allDataAgents.get(data).getDataUnicityConstraint());
						}
					}
				}
			}

			for(String data: dataAgentsActives) {
				if(!this.allDataAgents.get(data).getDataUnicityConstraint().isSatisfied()) {
					System.out.println(this.allDataAgents.get(data).getDataUnicityConstraint().getOffers());

				}
			}
			for(String data: dataAgentsActives) {
				if(!this.allDataAgents.get(data).getDataUnicityConstraint().isSatisfied()) {
					System.out.println(this.allDataAgents.get(data).getDataUnicityConstraint().getOffers());
					for(Offer offer : this.allDataAgents.get(data).getDataUnicityConstraint().getOffers()) {
						System.out.println(offer.getInputConstraint().getOffers());
					}
				}
			}
			for(CoalitionAgent coal : this.allCoalitions) {
				System.out.println(coal.getID());
			}
			try {
				Thread.sleep(200000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//}
		for(InputConstraint constr : inputConstrActive) {
			constr.keepOnlyTheBest();
			constr.getOffers().get(0).getAgent().cycleValue(constr.getInput());
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
				CoalitionAgent coal = new CoalitionAgent(this.allCoalitions.get(this.allCoalitions.size()-1).getID()+1, this, this.allDataAgents.get(missing));
				this.allCoalitions.add(coal);
			}
		}


	}




	public Set<String> getDataPerceivedInSituation() {
		return this.dataPerceivedInSituation;

	}

	public List<String> getDataComInSituation() {
		return this.dataCommunicatedInSituation;
	}

	public Collection<String> getDataPerceived() {
		return this.dataPerceived;
	}



	public String getDataEffector(int myObjectiveState) {
		return this.effectorData.get(myObjectiveState);
	}

	public List<String> getExteroceptiveData() {
		return this.exteroData;
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
		//this.allCoalitions.remove(coal);
		for(String agent : agentToDecide) {
			this.allDataAgents.get(agent).RemoveFromCoalition();
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

	public Planing getMyPlaning() {
		return this.myPlaning;
	}
	public Planing getTruePlaning() {
		return this.truePlaning;
	}

	public Planing getPlaningSituation() {
		return this.planingSituation;
	}

	public void createOwnCoalition(String dataName) {
		CoalitionAgent coal = new CoalitionAgent(this.allCoalitions.get(this.allCoalitions.size()-1).getID()+1+this.coalitionsToAdd.size(), this, this.allDataAgents.get(dataName));
		this.coalitionsToAdd.add(coal);
		//this.allCoalitions.add(coal);
	}

	public int getCycle() {
		return this.cycle;
	}

	public List<CoalitionAgent> getAllCoalitions() {
		return this.allCoalitions;
	}

	public Collection<DataAgent> getAllDataAgent() {
		return this.allDataAgents.values();
	}

	public DataAgent getDataAgentWithName(String dataAgentName) {
		return this.allDataAgents.get(dataAgentName);
	}

	/**
	 * End the exp
	 */
	public void endExp() {
		try {
			this.resultingCoalition.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.resultingDataset.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Propose a dataAgent to coalition
	 * 
	 * @param dataName
	 * @param meanCrit
	 * @param coal
	 * @return
	 */
	public boolean proposeDataAgent(String dataName, float meanCrit, CoalitionAgent coal) {
		CoalitionAgent best = null;
		float bestCrit = 0.0f;
		for(CoalitionAgent other : this.allCoalitions) {
			if(!other.equals(coal)) {
				float crit = other.evaluateData(dataName);
				if(crit > bestCrit) {
					best = other;
					bestCrit = crit;
				}
			}
		}
		if(bestCrit > 0.0f) {
			this.allDataAgents.get(dataName).exchangeCoalition(best);
			return true;
		}
		return false;
	}





}
