package fr.irit.smac.learningdata.Agents;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;

import fr.irit.smac.amak.Agent;
import fr.irit.smac.learningdata.AmasLearning;
import fr.irit.smac.learningdata.EnvironmentLearning;
import fr.irit.smac.learningdata.Agents.InputAgent.Operator;
import fr.irit.smac.learningdata.requests.Offer;
import fr.irit.smac.learningdata.requests.Request;
import fr.irit.smac.learningdata.requests.RequestForRow;
import fr.irit.smac.learningdata.requests.RequestForWeight;
import fr.irit.smac.learningdata.requests.RequestRow;
import fr.irit.smac.learningdata.ui.HistoricWindow;
import fr.irit.smac.learningdata.ui.Matrix;
import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.modelui.learning.DataLearningModel;
import fr.irit.smac.modelui.learning.InputLearningModel;
import fr.irit.smac.shield.c2av.SyntheticFunction;
import links2.driver.connection.LinksConnection;
import links2.driver.connection.LocalLinksConnection;
import links2.driver.marshaler.Link2DriverMarshaler;
import links2.driver.marshaler.MarshallingMode;
import links2.driver.model.Entity;
import links2.driver.model.Experiment;
import links2.driver.model.Relation;
import links2.driver.model.Snapshot;

public class LearningFunction extends Agent<AmasLearning, EnvironmentLearning>{

	private SyntheticFunction function;
	private Deque<Double> valueOfOperand;


	private Set<String> variableInEnvironment;

	private List<Double> historyFeedback;

	private Map<String,InputAgent> allInputAgent;

	private Map<String,DataAgent> allDataAgent;

	private Map<InputAgent, RowAgent> allRowAgent;

	private Map<DataAgent, ColumnAgent> allColumnAgent;

	private Map<String,AgentLearning> allAgents;

	private Map<Pair<String,String>, WeightAgent> allWeightAgent;

	private Map<String,Double> inputsValues;

	private Map<Request,List<Offer>> auctions;

	private Map<Integer, Configuration> configurations;

	private Map<Integer,Map<String,Double>> oldValues; 

	private Map<Integer,Double> historicFeedbacks;

	private Map<Integer,Historic> historicsM;
	private Map<Integer,Historic> historicsG;

	private Configuration lastBestConfig;

	private int worstHistoricG;

	private int worstHistoricM;

	private double old_crit_hist;

	private double crit_hist;

	private Snapshot snapshot;

	private Experiment experiment;

	private FileWriter file;

	private FileWriter fileHisto;

	private Matrix matrix;

	private HistoricWindow historicWindow;

	private String name;
	private double feedback;

	private int nbDataAgent = 0;

	private boolean modificationHappened;

	private int idConfig;

	private Configuration currentConfig;
	private Map<String, Operator> inputsDecisions;
	private double worstG;
	private double worstM;


	public LearningFunction(AmasLearning amas, Object[] params, String name, SyntheticFunction function) {
		super(amas, params);
		this.name = name;
		this.function = function;
		init();
	}

	private void init() {
		// Agents
		this.allDataAgent = new TreeMap<String,DataAgent>();
		this.allInputAgent = new TreeMap<String,InputAgent>();
		this.allColumnAgent = new HashMap<DataAgent, ColumnAgent>();
		this.allRowAgent = new HashMap<InputAgent, RowAgent>();

		// Usefuls Collections
		this.historyFeedback = new ArrayList<Double>();
		this.allAgents = new TreeMap<String,AgentLearning>();
		this.auctions = new HashMap<Request,List<Offer>>();
		this.configurations = new TreeMap<Integer,Configuration>();
		this.inputsDecisions = new TreeMap<String,Operator>();
		this.inputsValues = new TreeMap<String,Double>();
		this.allWeightAgent = new TreeMap<Pair<String,String>,WeightAgent>();
		this.oldValues = new TreeMap<Integer,Map<String,Double>>();
		this.historicFeedbacks = new TreeMap<Integer,Double>();
		this.historicsM = new TreeMap<Integer,Historic>();
		this.historicsG = new TreeMap<Integer,Historic>();

		// Crit
		this.old_crit_hist = 0.0;
		this.crit_hist = 0.0;
		this.feedback = 0.0;


		// Writing the csv
		try {
			this.file = new FileWriter(new File("C:\\\\Users\\\\gmarcill\\\\Desktop\\\\matrix.csv"));
		} catch (IOException e) {
			e.printStackTrace();
		}


		try {
			this.fileHisto = new FileWriter(new File("C:\\\\Users\\\\gmarcill\\\\Desktop\\\\histo.csv"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		experiment = new Experiment("Variable Selection ID : TEST");

		for(Integer i : this.function.getInputIDRemoved()) {
			this.createInputAgent("Input:"+i, i);
		}
		//this.amas.setValueOfVariableNonDegraded(this);
		for(InputAgent input : this.allInputAgent.values()) {
			String nameOfCorrect = this.amas.getNameOfCorrectDataForInput(input.getId(), this.name);
			try {
				this.file.write(input.getName()+";");
				this.file.write(nameOfCorrect+"\n");
				//this.function.setValueOfOperand(input.getId(), this.amas.getValueOfVariable(nameOfCorrect));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String, Integer> tmpInput = new TreeMap<String,Integer>();
		for(String s : this.allInputAgent.keySet()) {
			tmpInput.put(s, this.allInputAgent.get(s).getId());
		}
		this.amas.writeInputsInFileDatas(tmpInput,this.name);

	}

	public String getName() {
		return this.name;
	}

	@Override
	protected void onAgentCycleBegin() {

		System.out.println("Cycle : "+this.getAmas().getCycle());
		this.amas.generateNewValues();
		try {
			this.file.write("Cycle : " + this.getAmas().getCycle()+ "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.valueOfOperand = new ArrayDeque<Double>();
		this.variableInEnvironment = new TreeSet<String>();

		snapshot = new Snapshot();
		snapshot.setSnapshotNumber(this.getCycle());

		for(String input : this.allInputAgent.keySet()) {
			Entity ent = new Entity(input, "Input");
			ent.setAttribute(Entity.ATTRIBUTE_NAME, input);
			snapshot.addEntity(ent);
		}
		// Update the influence and the trust

		this.idConfig = 0;

	}

	@Override
	protected void onPerceive() {
		this.historyFeedback.add(this.feedback);
		Iterator<String> iter = this.function.getOperands().iterator();
		while(iter.hasNext()) {
			this.valueOfOperand.offer(this.getAmas().getValueOfVariable(iter.next()));
		}

		this.calculOfTheWorstHistoric();
		// Update the hist criticality
		if(this.worstG > this.worstM) {
			this.crit_hist = this.worstG;
		}
		else {
			this.crit_hist = this.worstM;
		}
		// Getting the variables in the environment
		this.variableInEnvironment.addAll(this.getAmas().getVariableInEnvironment());
		this.variableInEnvironment.removeAll(this.function.getOperandNotRemoved());

		// Creation of the data agent
		List<String> dataAgentToCreate = new ArrayList<String>(this.variableInEnvironment);
		dataAgentToCreate.removeAll(this.allDataAgent.keySet());

		for(String s : dataAgentToCreate) {
			this.createDataAgent(s);
		}


		// Give the feedback to the input agent
		for(InputAgent inputAgent : this.allInputAgent.values()) {
			inputAgent.updateInfluence(this.feedback);
			inputAgent.clearApplying();
		}



		// Give the feedback of the function to all dataAgent
		for(DataAgent dataAgent : this.allDataAgent.values()) {
			dataAgent.setInputAvailable(this.allInputAgent.keySet());
			//dataAgent.updateTrust(this.feedback);
			dataAgent.updateTrust(this.lastBestConfig);
			dataAgent.clearInput();
			dataAgent.fireTrustValues();
			String data = dataAgent.getName();
			Entity ent = new Entity(data, "Data");
			ent.setAttribute(Entity.ATTRIBUTE_NAME, data);
			for(String trust : dataAgent.getTrustValues().keySet()) {
				ent.setAttribute(trust, dataAgent.getTrustValues().get(trust));
			}
			this.snapshot.addEntity(ent);
		}

		this.modificationHappened = false;

	}

	private void calculOfTheWorstHistoric() {

		// Compute the worst historic with the current configuration

		// Find the worst historic G
		this.worstG = 0.0;
		this.worstHistoricG = -1;
		//System.out.println("HIST G"+this.historicsG);
		for(Integer i : this.historicsG.keySet()) {
			Double resConf = this.calculResConfigPast(this.currentConfig, i);
			if(this.historicsG.get(i).getValue() <= resConf) {
			//	System.out.println("RESCONF G: "+resConf+" HIST : "+this.historicsG.get(i).getValue());
				if(Math.abs(this.historicsG.get(i).getValue() - resConf) > worstG) {
					this.worstG = Math.abs(this.historicsG.get(i).getValue() - resConf);
					this.worstHistoricG = i;
				}
			}
		}

		// Find the worts historic M
		this.worstM = 0.0;
		this.worstHistoricM = -1;
		//System.out.println("HIST M"+this.historicsM);
		for(Integer i : this.historicsM.keySet()) {
			Double resConf = this.calculResConfigPast(this.currentConfig, i);
			if(this.historicsM.get(i).getValue() >= resConf) {
			//	System.out.println("RESCONF M: "+resConf+" HIST : "+this.historicsM.get(i).getValue());
				if(Math.abs(this.historicsM.get(i).getValue() - resConf) > worstM) {
					this.worstM = Math.abs(this.historicsM.get(i).getValue() - resConf);
					this.worstHistoricM = i;
				}
			}
		}
	}


	/**
	 * Active the different agent in the system
	 */
	@Override
	protected void onDecide() {

		this.startInputAgent();
		for(InputAgent input : this.allInputAgent.values()) {
			this.inputsDecisions.put(input.getName(), input.getDecision());
		}
		this.solveHistoricConstaints();
		
		if(this.feedback !=0) {
			this.startHistoricDecision();

			this.startDataAgent();

			this.startRowAgent();

			this.startColumnAgent();
			try {
				this.startWeightsAgent();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.currentConfig = new Configuration(this.amas.getCycle(), 0);




		for(String input : this.allInputAgent.keySet()) {
			this.currentConfig.addInput(input);
			for(String data : this.allDataAgent.keySet()) {
				this.currentConfig.addDataValueToInput(input, data, this.allDataAgent.get(data).getWeightOfInput(input));
			}
		}


	}

	/**
	 * Tend to achieve a stable state from the constraints perspective
	 */
	private void solveHistoricConstaints() {
		this.modificationHappened = true;
		while(this.modificationHappened && (this.worstHistoricG !=-1 || this.worstHistoricM !=-1)) {
			this.modificationHappened = false;
			this.calculOfTheWorstHistoric();
			this.startHistoricDecision();
			
			this.startWeightHistoric();
			

			this.currentConfig = new Configuration(this.amas.getCycle(), 0);

			for(String input : this.allInputAgent.keySet()) {
				this.currentConfig.addInput(input);
				for(String data : this.allDataAgent.keySet()) {
					this.currentConfig.addDataValueToInput(input, data, this.allDataAgent.get(data).getWeightOfInput(input));
				}
			}
			
			this.updateMatrix();
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}

	private void startWeightHistoric() {
		for(WeightAgent weight : this.allWeightAgent.values()) {
			weight.onPerceiveHistoric();
			weight.onDecideAndActHistoric();
		}
		
	}

	/**
	 * 
	 */
	private void startHistoricDecision() {
		if(this.worstHistoricG != -1 || this.worstHistoricM != -1) {
			for(String input : this.allInputAgent.keySet()) {
				for(String data : this.allDataAgent.keySet()) {
					RequestForWeight request = new RequestForWeight(this.worstG, "HISTORIC", 0, Operator.NONE, "HISTORIC");
					Configuration configTmp = new Configuration(this.currentConfig);

					// Si on veut ameliorer la trop grande
					if(this.worstG > this.worstM) {

						// On test en augmentant le poids
						configTmp.addDataValueToInput(input, data, configTmp.getDataValueForInput(input, data)+0.05);
						Double res = this.calculResConfigPast(configTmp, this.worstHistoricG);
						// Si la nouvelle config donne un resultat plus petit
						if(res < this.historicsG.get(this.worstHistoricG).getValue()) {
							Double resOpp = this.calculResConfigPast(configTmp, this.worstHistoricM);
							// Sa la nouvelle config est pas pire pour l'oppose
							if((this.worstHistoricM > 1 && resOpp >= this.historicsM.get(this.worstHistoricM).getValue()) || this.worstHistoricM < 2 ) {
								request.setDecision(Operator.PLUS);
							}
						}
						// On test en reduisant le poids
						configTmp.addDataValueToInput(input, data, configTmp.getDataValueForInput(input, data)-0.05);

						// Il faut pouvoir diminuer le poids
						if(configTmp.getDataValueForInput(input, data) > 0.00) {
							configTmp.addDataValueToInput(input, data, Math.max(0.0, configTmp.getDataValueForInput(input, data)-0.05));
						}

						res = this.calculResConfigPast(configTmp, this.worstHistoricG);
						// Si la nouvelle config donne un resultat plus petit
						if(res < this.historicsG.get(this.worstHistoricG).getValue()) {
							Double resOpp = this.calculResConfigPast(configTmp, this.worstHistoricM);
							// Sa la nouvelle config est pas pire pour l'oppose
							if((this.worstHistoricM > 1 && resOpp >= this.historicsM.get(this.worstHistoricM).getValue()) || this.worstHistoricM < 2 ) {
								request.setDecision(Operator.MOINS);
								System.out.println(" GRAND RES : "+res + "OPP "+ resOpp+ "his " + this.historicsG.get(this.worstHistoricG).getValue());
							}
						}
					}
					// Si on veut ameliorer la plus petite
					else {
						// On test en augmentant le poids
						configTmp.addDataValueToInput(input, data, configTmp.getDataValueForInput(input, data)+0.05);
						Double res = this.calculResConfigPast(configTmp, this.worstHistoricM);
						// Si la nouvelle config donne un resultat plus grand
						if(res > this.historicsM.get(this.worstHistoricM).getValue()) {
							Double resOpp = this.calculResConfigPast(configTmp, this.worstHistoricG);
							// Sa la nouvelle config est pas pire pour l'oppose
							if((this.worstHistoricG > 1 && resOpp >= this.historicsG.get(this.worstHistoricG).getValue()) || this.worstHistoricG < 2 ) {
								request.setDecision(Operator.PLUS);
							}
						}

						// On test en reduisant le poids
						configTmp.addDataValueToInput(input, data, configTmp.getDataValueForInput(input, data)-0.05);

						// Il faut pouvoir diminuer le poids
						if(configTmp.getDataValueForInput(input, data) > 0.00) {
							configTmp.addDataValueToInput(input, data, Math.max(0.0, configTmp.getDataValueForInput(input, data)-0.05));
						}

						res = this.calculResConfigPast(configTmp, this.worstHistoricM);
						// Si la nouvelle config donne un resultat plus grand
						if(res > this.historicsM.get(this.worstHistoricM).getValue()) {
							Double resOpp = this.calculResConfigPast(configTmp, this.worstHistoricG);
							// Sa la nouvelle config est pas pire pour l'oppose
							if((this.worstHistoricG > 1 && resOpp >= this.historicsG.get(this.worstHistoricG).getValue()) || this.worstHistoricG < 2 ) {
								request.setDecision(Operator.MOINS);
								System.out.println("PETIT RES : "+res + "OPP "+ resOpp+ "his " + this.historicsM.get(this.worstHistoricM).getValue());
							}
						}

					}
					this.sendRequestForWeight(input, data, request);
				}
			}
		}
	}

	private void startWeightsAgent() throws Exception {
		/*for(DataAgent data : this.allDataAgent.values()) {
			data.startWeights();
		}*/

		List<WeightAgent> tmp = new ArrayList<WeightAgent>(this.allWeightAgent.values());
		Collections.shuffle(tmp);
		for(WeightAgent weight : tmp) {
			weight.onPerceive();
		}
		int  step = 0;
		this.modificationHappened = false;
		while(!this.modificationHappened) {
			Collections.shuffle(tmp);
			for(WeightAgent weight : tmp) {
				weight.onDecideAndAct(step);
			}
			step++;
			if(step >4) {
				throw new Exception("No one want to change");
			}
		}

	}

	/**
	 * Start the cycle for all row agent
	 */
	private void startRowAgent() {
		// Random order
		List<RowAgent> rowAgentRemaining = new ArrayList<RowAgent>(this.allRowAgent.values());
		Collections.shuffle(rowAgentRemaining);

		for(RowAgent rowAgent : rowAgentRemaining) {
			rowAgent.perceive();
		}
		Collections.shuffle(rowAgentRemaining);
		for(RowAgent rowAgent : rowAgentRemaining) {
			rowAgent.decideAndAct();
		}
	}

	/**
	 * Start the cycle for all ColumnAgent
	 */
	private void startColumnAgent() {
		// Random order
		List<ColumnAgent> columnAgentRemaining = new ArrayList<ColumnAgent>(this.allColumnAgent.values());
		Collections.shuffle(columnAgentRemaining);

		for(ColumnAgent columnAgent : columnAgentRemaining) {
			columnAgent.perceive();
		}
		Collections.shuffle(columnAgentRemaining);
		for(ColumnAgent columnAgent : columnAgentRemaining) {
			columnAgent.decideAndAct();
		}
	}

	/**
	 * Start the cycle for all the InputAgent
	 */
	private void startInputAgent() {


		// All inputAgent perceives in random order
		List<InputAgent> inputAgentRemaining = new ArrayList<InputAgent>(this.allInputAgent.values());
		Collections.shuffle(inputAgentRemaining);
		for(InputAgent inputAgent : inputAgentRemaining) {
			inputAgent.perceive();
		}

		// All inputAgent decide and act in random order
		Collections.shuffle(inputAgentRemaining);
		for(InputAgent inputAgent : inputAgentRemaining) {
			inputAgent.decideAndAct();
		}



	}

	/**
	 * Start the cycle for all the dataAgent
	 */
	private void startDataAgent() {

		List<DataAgent> dataAgentRemaining = new ArrayList<DataAgent>(this.allDataAgent.values());
		Collections.shuffle(dataAgentRemaining);

		// All dataAgent perceives
		for(DataAgent dataAgent : dataAgentRemaining) {
			dataAgent.perceive();
		}

		Collections.shuffle(dataAgentRemaining);
		// All DataAgent decide and act
		for(DataAgent dataAgent : dataAgentRemaining) {
			dataAgent.decideAndAct();
			/*for(String will : dataAgent.getInputChosen()) {
				acquisition.get(will).add(dataAgent.getName());
			}*/

		}

	}

	@Override
	protected void onAct() {
		System.out.println("WORSTG : "+this.worstHistoricG);
		System.out.println("WORSTM : "+this.worstHistoricM);
		this.oldValues.put(this.getCycle(), new TreeMap<String,Double>());
		for(String data : this.allDataAgent.keySet()) {
			this.oldValues.get(this.getCycle()).put(data, this.amas.getValueOfVariable(data));
		}

		this.amas.setValueOfVariableNonDegraded(this);
		List<Integer> inputs = new ArrayList<Integer>();
		Double res =0.0;
		for(InputAgent input : this.allInputAgent.values()) {
			inputs.add(input.getId());
		}
		this.configurations.put(this.getCycle(), this.currentConfig);
		try {
			this.feedback = this.calculResConfig(this.currentConfig);
			res = this.feedback;
			if(this.feedback > this.amas.getResultOracle(this.name)) {
				this.feedback = 1.0;
			}
			else {
				if(this.feedback < this.amas.getResultOracle(this.name)) {
					this.feedback = -1.0;
				}
				else {
					this.feedback = 0.0;
				}
			}
			this.file.write("\n"+this.feedback+";"+res+";"+this.amas.getResultOracle(this.name)+"\n");
			//this.feedback = this.feedback - this.amas.getResultOracle(this.name);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		this.historicFeedbacks.put(this.getCycle(), this.feedback);
		// Update the window matrix
		updateMatrix();

		// update the window historic
		updateHistoric(res,this.feedback);


		// Write in the data file
		this.amas.writeBorneAndFeedbackAndDatas(res, this.feedback);

		// LOGS
		System.out.println("Feedback : "+ res + " : "+this.amas.getResultOracle(this.name));
		LxPlot.getChart("Feedback").add("Difference",this.getCycle(), Math.abs(res-this.amas.getResultOracle(this.name)));
		LxPlot.getChart("Results").add("Result",this.getCycle(), res);
		LxPlot.getChart("Results").add("oracle",this.getCycle(), this.amas.getResultOracle(this.name));
		/*for(InputAgent input : this.allInputAgent.values()) {
			for(DataAgent data : this.allDataAgent.values()) {
				double critInput = this.allRowAgent.get(input).getCriticalityAfterUpdate(data.getName(), Operator.NONE);
				double critData = this.allColumnAgent.get(data).getCriticalityAfterUpdate(input.getName(),Operator.NONE);
				LxPlot.getChart(input.getName() + " and "+data.getName()).add("CritInput", this.getCycle(), critInput);
				LxPlot.getChart(input.getName() + " and "+data.getName()).add("CritData", this.getCycle(), critData);
			}
		}*/


		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.experiment.addSnapshot(this.snapshot);

		try {
			this.writeMatrixInFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Restore the calcul of the function
		this.calculResConfigNoWrite(this.currentConfig);

		if(this.getCycle() == 10000) {
			try {
				this.file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.amas.getScheduler().stop();
			//Helper to get connection with default parameters
			LinksConnection connection = LocalLinksConnection.getLocalConnexion();

			//Save the experiment 
			Link2DriverMarshaler.marshalling(connection, experiment, MarshallingMode.OVERRIDE_EXP_IF_EXISTING);

			//Don't forget to close the DB connection
			connection.close();

		}
	}

	/**
	 * Update the historic with a new feedback and the result compared with the new conf
	 * @param value
	 * @param feedback2
	 */
	private void updateHistoric(Double value, double feedback2) {
		if(this.allDataAgent != null && this.allDataAgent.keySet() != null) {
			if(this.historicWindow==null) {
				this.historicWindow = new HistoricWindow();
				this.historicWindow.setVisible(true);
			}
			TreeMap<Integer,Double> line = new TreeMap<Integer,Double>();
			for(int i = 0; i < this.configurations.keySet().size();i++) {
				int j = i+2;
				Configuration config = this.configurations.get(j);
				/*for(String input : this.allInputAgent.keySet()) {
					for(String data : this.allDataAgent.keySet()) {
						Double wei = this.allDataAgent.get(data).getWeightOfInput(input);
						config.addDataValueToInput(input, data, wei);
					}
				}*/
				//Double res = this.calculResConfigPast(config);
				Double res = this.calculResConfigPast(this.currentConfig,j);
				line.put(i, res);
				try {
					if(j != this.getCycle())
						this.fileHisto.write(""+res+";");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String strValue = "";
			if(this.feedback == 1.0) {
				strValue = "G:"+value;
			}
			if(this.feedback == -1.0) {
				strValue = "M:"+value;
			}
			try {
				this.fileHisto.write(""+strValue+";");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.historicWindow.addCycle(this.getCycle(), strValue, line,feedback2);


			try {
				this.fileHisto.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			this.fileHisto.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(feedback2 > 0.0)
			this.historicsG.put(this.getCycle(), new Historic(this.getCycle(), value, feedback2, this.currentConfig));
		else
			this.historicsM.put(this.getCycle(), new Historic(this.getCycle(), value, feedback2, this.currentConfig));
	}

	/**
	 * Ask the ui to update the matrix
	 */
	private void updateMatrix() {
		// The first row
		String[] head = new String[this.allDataAgent.keySet().size()+1];
		head[0] = "";
		int i = 1;
		for(String s : this.allDataAgent.keySet()) {
			head[i] = s;
			i++;
		}
		String[] headOracle = new String[this.allDataAgent.keySet().size()+1];
		headOracle[0] = "";
		i = 1;
		for(String s : this.allDataAgent.keySet()) {
			headOracle[i] = s;
			i++;
		}

		// The resolution matrix
		Object[][] donnees = new Object[this.allInputAgent.keySet().size()+1][this.allDataAgent.keySet().size()+1];
		donnees[0] = head;
		i = 1;
		for(String inp : this.allInputAgent.keySet()) {
			donnees[i][0] = inp +":"+ this.amas.getNameOfInputFormula(this.allInputAgent.get(inp).getId(), this.name);
			int j = 1;
			for(String dat : this.allDataAgent.keySet()) {
				//donnees[i][j] = inp+":"+dat+":"+this.currentConfig.getDataValueForInput(inp, dat);
				donnees[i][j] = this.currentConfig.getDataValueForInput(inp, dat);
				j++;
			}
			i++;
		}

		// The oracle matrix
		Object[][] truth = new Object[this.allInputAgent.keySet().size()+1][this.allDataAgent.keySet().size()+1];
		truth[0] = head;
		i = 1;
		for(String inp : this.allInputAgent.keySet()) {
			truth[i][0] = inp +":"+ this.amas.getNameOfInputFormula(this.allInputAgent.get(inp).getId(), this.name);
			int j = 1;
			for(String dat : this.allDataAgent.keySet()) {
				if(dat.equals(this.amas.getNameOfCorrectDataForInput(this.allInputAgent.get(inp).getId(), this.name))) {
					//truth[i][j] = inp+":"+dat+":"+1.0;
					truth[i][j] = 1.0;
				}
				else {
					//truth[i][j] = inp+":"+dat+":"+0.0;
					truth[i][j] = 0.0;
				}
				j++;
			}
			i++;
		}
		if(this.matrix == null) {
			this.matrix = new Matrix(head, donnees,headOracle,truth);
			this.matrix.setVisible(true);
		}
		else {
			this.matrix.updateTable(head, donnees);
			this.matrix.updateOracleMatrix(head, truth);
		}
	}

	private void writeMatrixInFile() throws IOException {
		this.file.write("\t");
		List<String> datasTmp = new ArrayList<String>(this.allDataAgent.keySet());
		List<String> inputTmp = new ArrayList<String>(this.allInputAgent.keySet());
		for(String data : datasTmp) {
			this.file.write(";"+data+" : "+this.allDataAgent.get(data).getValue());
		}
		for(String input : inputTmp) {
			String sensy ="";
			switch(this.inputsDecisions.get(input)) {
			case MOINS:
				sensy = "Decrois";
				break;
			case NONE:
				sensy = "None";
				break;
			case PLUS:
				sensy = "Crois";
				break;
			default:
				sensy = "Default";
				break;

			}
			this.file.write("\n"+input + ":"+sensy);
			for(String data : datasTmp) {
				String strDouble = String.format("%.2f", this.currentConfig.getDataValueForInput(input, data));  
				this.file.write(";"+strDouble+"|"+this.allWeightAgent.get(Pair.of(input, data)).getMemory());
			}
		}
		this.file.write("\n"+"\n");
	}

	private Double calculResConfig(Configuration config) throws IOException {
		Double res = 0.0;
		this.file.write("\n values;");
		for(String input : this.allInputAgent.keySet()) {
			Double valueForInput = 0.0;
			for(String data : this.allDataAgent.keySet()) {
				valueForInput += config.getDataValueForInput(input, data) * this.allDataAgent.get(data).getValue();
			}
			this.function.setValueOfOperand(this.allInputAgent.get(input).getId(),valueForInput);
			this.inputsValues.put(input, valueForInput);
			this.file.write(""+valueForInput+";");
		}
		res = this.function.computeCustom();
		return res;
	}

	private Double calculResConfigNoWrite(Configuration config){
		Double res = 0.0;
		for(String input : this.allInputAgent.keySet()) {
			Double valueForInput = 0.0;
			for(String data : this.allDataAgent.keySet()) {
				valueForInput += config.getDataValueForInput(input, data) * this.allDataAgent.get(data).getValue();
			}
			this.function.setValueOfOperand(this.allInputAgent.get(input).getId(),valueForInput);
			this.inputsValues.put(input, valueForInput);
		}
		res = this.function.computeCustom();
		return res;
	}

	protected Double calculResConfigPast(Configuration config, int cycle) {
		Double res = 0.0;
		/*System.out.println("CONFIG : "+cycle);
		System.out.println(this.oldValues);
		System.out.println("PAST");*/
		if(cycle > 1 ) {
			this.amas.setupOraclePastValues(this.name,cycle);
			for(String input : this.allInputAgent.keySet()) {
				Double valueForInput = 0.0;
				for(String data : this.allDataAgent.keySet()) {
					valueForInput += config.getDataValueForInput(input, data) * this.oldValues.get(cycle).get(data);
				}
				this.function.setValueOfOperand(this.allInputAgent.get(input).getId(),valueForInput);
			}
			res = this.function.computeCustom();
		}
		return res;
	}


	/**
	 * Create a data agent
	 * 
	 * @param name
	 * 
	 * @return true if the agent does not already exist
	 */
	private boolean createDataAgent(String name) {
		if(this.allDataAgent.keySet().contains(name)) {
			return false;
		}
		DataAgent dag = new DataAgent(name,this, nbDataAgent);
		nbDataAgent++;
		for(InputAgent inputAgent : this.allInputAgent.values()) {
			dag.addNewInputAgent(inputAgent.getName());
		}
		this.allDataAgent.put(dag.getName(), dag);
		this.allAgents.put(name, dag);

		for(RowAgent rowAgent: this.allRowAgent.values()) {
			rowAgent.addDataAgent(dag);
		}

		this.createColumnAgent("Column"+name,dag,this.allInputAgent.values());
		return true;
	}
	/**
	 * Create an input Agent 
	 * @param name
	 * @return true if the agent does not already exist
	 */
	private boolean createInputAgent(String name, int id) {
		if(this.allInputAgent.containsKey(name)) {
			return false;
		}
		InputAgent inag = new InputAgent(name,this, id);
		this.allInputAgent.put(inag.getName(), inag);
		this.allAgents.put(inag.getName(), inag);

		this.createRowAgent(name, inag);
		return true;
	}

	private boolean createRowAgent(String name, InputAgent input) {
		if(this.allRowAgent.containsKey(input)) {
			return false;
		}
		RowAgent rowAgent = new RowAgent( name,input,this);
		this.allRowAgent.put(input, rowAgent);
		this.allAgents.put(name, rowAgent);
		return true;
	}

	private void createColumnAgent(String name, DataAgent dag, Collection<InputAgent> inputs) {
		ColumnAgent columnAgent = new ColumnAgent(name,dag,this);
		this.allColumnAgent.put(dag, columnAgent);
		this.allAgents.put(name, columnAgent);
	}


	/**
	 * Set the function
	 * 
	 * @param fun
	 */
	public void setFunction(SyntheticFunction fun) {
		this.function = fun;
	}

	public List<Double> getHistoryFeedback(){
		return this.historyFeedback;
	}

	public DataAgent getDataAgentWithName(String nameOfData) {
		return this.allDataAgent.get(nameOfData);
	}

	public Map<String, Double> getInfluences() {
		Map<String, Double> influences = new TreeMap<String,Double>();
		for(String nameOfInput : this.allInputAgent.keySet()) {
			influences.put(nameOfInput, this.allInputAgent.get(nameOfInput).getInfluence());
		}
		return influences;
	}

	public int getCycle() {
		return this.getAmas().getCycle();
	}

	public double getDataValue(String name2) {
		return this.getAmas().getValueOfVariable(name2);
	}

	public Set<String> getInputsName() {
		return this.allInputAgent.keySet();
	}



	/**
	 * Return the column agent with the name
	 * 
	 * @param name
	 * 
	 * @return the column agent
	 */
	public ColumnAgent getColumnAgentWithName(String name) {
		return this.allColumnAgent.get(name);
	}

	/**
	 * Return the row agent with the name
	 * 
	 * @param name
	 * 
	 * @return the row agent
	 */
	public RowAgent getRowAgentWithName(String name) {
		return (RowAgent) this.allAgents.get(name);
	}

	public void informDecision(DataAgent dataAgent,Set<String> inputsChosen) {
		for(String input : inputsChosen) {
			this.allRowAgent.get(this.allInputAgent.get(input)).dataAgentApplying(dataAgent);
		}

	}

	public void acceptRequest(String agentName, int idRequest) {
		this.allAgents.get(agentName).requestAccepted(idRequest);
	}

	public void rejectRequest(String agentName, int id) {
		this.allAgents.get(agentName).requestDenied(id);

	}



	/**
	 * 
	 * @param request
	 * @param offer
	 */
	public void applyForRequest(Request request, Offer offer) {
		this.auctions.get(request).add(offer);
	}

	public void proposeRequest(Request request) {
		this.auctions.put(request, new ArrayList<Offer>());
		for(DataAgent dataAgent : this.allDataAgent.values()) {
			dataAgent.sendRequest(request);
		}

	}

	public List<String> getAllDataAgentApplyingForInput(String name2) {
		List<String> res = new ArrayList<String>();
		for(DataAgent dataAgent : this.allDataAgent.values()) {
			if(dataAgent.getInputChosen().contains(name2)) {
				res.add(dataAgent.getName());
			}
		}
		return res;
	}

	public void addListenerToData(String data, DataLearningModel model) {
		this.allDataAgent.get(data).addPropertyChangeListener(model);
	}

	public void addListenerToInput(String input, InputLearningModel model) {
		this.allInputAgent.get(input).addPropertyChangeListener(model);

	}

	public Set<String> getDatasNames() {
		return this.allDataAgent.keySet();
	}

	public String getCorrectData(int idInput) {
		return this.amas.getNameOfCorrectDataForInput(idInput, this.name);
	}

	public double getFeedback() {
		return this.feedback;
	}

	/**
	 * Return the previous configuration
	 * 
	 * @return Map<String,String> res
	 * 			First, the name of the input, then the name of the data
	 */
	public Map<String,String> getPreviousConfiguration() {
		Map<String,String> res = new TreeMap<String,String>();
		for(InputAgent input : this.allInputAgent.values()) {
			res.put(input.getName(), input.getCurrentData().getName());
		}
		return res;
	}

	public List<DataAgent> getAllDataAgent() {
		return new ArrayList<DataAgent>(this.allDataAgent.values());
	}

	public Operator getBestInfluenceFromInput(String name) {
		Operator res = null;
		Double value = -1.0;
		Map<Operator,Double> influences = this.allInputAgent.get(name).getInfluences();
		for(Operator ope : influences.keySet()) {
			if(value < influences.get(ope)) {
				value = influences.get(ope);
				res = ope;
			}
		}
		return res;
	}



	public Configuration getCurrentConfig() {
		return this.currentConfig;
	}

	public Operator getInputDecision(String input) {
		return this.allInputAgent.get(input).getDecision();
	}

	public Double computeResult(int id,double value, double initValue) {
		this.function.setValueOfOperand(id, value);
		double res = this.function.computeCustom();
		this.function.setValueOfOperand(id, initValue);
		return res;
	}

	public void askRow(String input, RequestForRow requestForRow) {
		this.allRowAgent.get(input).addRequest(requestForRow);

	}

	public Offer askData(String data,Operator decision, String input) {
		return this.allDataAgent.get(data).askData(decision,input);
	}

	/**
	 * Transfer a request to a weight agent by the data agent
	 * 
	 * @param input
	 * 		The input (row) which send the request
	 * @param data
	 * 		The data holding the weight agent	 * 
	 * @param requestToSend
	 * 		The request
	 */
	public void sendRequestForWeight(String input, String data, RequestForWeight requestToSend) {
		this.allDataAgent.get(data).sendRequestForWeight(input,requestToSend);

	}

	public Map<String, Operator> getInputsDecisions() {
		return this.inputsDecisions;
	}

	public Double getLastValueOfinput(String input) {
		if(!this.inputsValues.containsKey(input))
			return null;
		return this.inputsValues.get(input);
	}

	public void modification() {
		this.modificationHappened = true;
	}

	public void addNewWeightAgent(WeightAgent weight,String data,String input) {
		boolean helped = false;
		if(helped) {
			if(this.amas.getNameOfCorrectDataForInput(this.allInputAgent.get(input).getId(), this.name).equals(data)) {
				weight.setWeight(0.6);
			}
			else {
				Random rand = new Random();
				if(rand.nextBoolean())
					weight.setWeight(0.1);
				else
					weight.setWeight(0.6);
			}
			this.allWeightAgent.put(Pair.of(input, data), weight);
		}
		else {
			weight.setWeight(0.5);
			this.allWeightAgent.put(Pair.of(input, data), weight);
		}

	}

	public double getInputCriticalityAfterUpdate(String data,String input, Operator decision) {
		return this.allRowAgent.get(this.allInputAgent.get(input)).getCriticalityAfterUpdate(data,decision);
	}

	public double getDataCriticalityAfterUpdate(String data, String input, Operator decision) {
		return this.allColumnAgent.get(this.allDataAgent.get(data)).getCriticalityAfterUpdate(input,decision);
	}

	public SyntheticFunction getFunction() {
		return this.function;
	}

	/**
	 * Return a pair of the two opposite worst historic
	 * 
	 * @return Pair<Integer,Integer>
	 */
	public Pair<Integer,Integer> getWorstHistoric() {
		int great = -1;
		int min  = -1;
		great = this.worstHistoricG;
		min = this.worstHistoricM;
		Pair<Integer,Integer> res = Pair.of(min,great );
		return res;
	}

	/**
	 * Return the feedback from a historic
	 * 
	 * @return Pair<Double,Double>
	 */
	public Double getFeedBackFromHistoric(Integer worstHist) {
		if(this.historicFeedbacks.containsKey(worstHist)) {
			return this.historicFeedbacks.get(worstHist);
		}
		else {
			return 0.0;
		}
	}



	/**
	 * Return the data value from the worst historic in input
	 * 
	 * @param data
	 * 			The name of the data
	 * @param worstHistoric
	 * 			The id of the historic
	 * @return
	 */
	public double getWorstOldDataValue(String data, int worstHistoric) {
		if(this.oldValues.containsKey(worstHistoric))
			return this.oldValues.get(worstHistoric).get(data);
		else
			return this.oldValues.get(this.getCycle()-1).get(data);
	}

	public int getWorstHistoricG() {
		return this.worstHistoricG;
	}


	public int getWorstHistoricM() {
		return this.worstHistoricM;
	}

	/**
	 * The difference between the old and the new configuration G
	 * 
	 * @return worstG
	 */
	public double getWorstG() {
		return this.worstG;
	}

	/**
	 * The difference between the old and the new configuration M
	 * 
	 * @return worstM
	 */
	public double getWorstM() {
		return this.worstM;
	}

	/**
	 * 
	 * @param worstHistoric
	 * @param resConf
	 * @param feed
	 * @return
	 */
	public boolean historicConfigIsBetter(int worstHistoric, Double resConf, int feed) {
		if(worstHistoric == -1) {
			return true;
		}
		if(feed >0) {
			return this.historicsG.get(worstHistoric).isConfigBetter(resConf);
		}
		else {
			return this.historicsM.get(worstHistoric).isConfigBetter(resConf);
		}
	}

	/**
	 * 
	 * @param worstHistoric
	 * @param resConf
	 * @param feed
	 * @return
	 */
	public boolean historicConfigIsBetterOrEquals(int worstHistoric, Double resConf, int feed) {
		if(worstHistoric == -1) {
			return true;
		}
		if(feed >0) {
			return this.historicsG.get(worstHistoric).isConfigBetter(resConf);
		}
		else {
			return this.historicsM.get(worstHistoric).isConfigBetter(resConf);
		}
	}

	/**
	 * Return if the previous modification was better
	 * @param name
	 * 		the name of the agent or HIST for historic
	 * @return res
	 * 		1 if it was better, -1 if it was worse and 0 else
	 */
	public int wasItBetter(String name) {
		if(name.equals("HIST")) {
			if(this.old_crit_hist > this.crit_hist) {
				return 1;
			}
			if(this.old_crit_hist < this.crit_hist) {
				return -1;
			}
			return 0;
		}
		else {
			AgentLearning ag = this.allAgents.get(name);
			if(ag instanceof DataAgent) {
				return this.allColumnAgent.get(this.allDataAgent.get(name)).wasItBetter();
			}
			else {
				return this.allRowAgent.get(this.allInputAgent.get(name)).wasItBetter();
			}
		}
	}

}
