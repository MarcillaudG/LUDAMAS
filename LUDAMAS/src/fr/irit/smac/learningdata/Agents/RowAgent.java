package fr.irit.smac.learningdata.Agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.learningdata.Agents.InputAgent.Operator;
import fr.irit.smac.learningdata.requests.Offer;
import fr.irit.smac.learningdata.requests.Request;
import fr.irit.smac.learningdata.requests.RequestColumn;
import fr.irit.smac.learningdata.requests.RequestForRow;
import fr.irit.smac.learningdata.requests.RequestForWeight;
import fr.irit.smac.learningdata.requests.RequestRow;
import fr.irit.smac.learningdata.requests.RequestRow.Reason;

public class RowAgent extends AgentLearning{

	private static final Double CRITICALITY_EMPTY = 20.0;

	private float criticality;
	
	private float old_criticality;

	private InputAgent input;

	private LearningFunction function;

	private String name;

	private Reason reason;

	private Map<String,Double> row;

	private Map<Integer,RequestRow> waitingRequest;

	private List<Offer> offersData; 

	private List<RequestForRow> mailBox;

	private int idRequest;

	private Configuration currentConfig;

	public RowAgent(String name,InputAgent input, LearningFunction function ) {
		this.criticality = 0.0f;
		this.old_criticality = 0.0f;
		this.input = input;
		this.name = name;
		this.function = function;
		this.idRequest = 0;

		this.row = new HashMap<String,Double>();
		this.waitingRequest = new TreeMap<Integer,RequestRow>();
		this.mailBox = new ArrayList<RequestForRow>();
		this.offersData = new ArrayList<Offer>();

	}

	public float getCriticality() {
		return criticality;
	}

	public InputAgent getInput() {
		return input;
	}

	public Set<String> getDataAgents() {
		return row.keySet();
	}

	public Map<String,Double> getRow(){
		return this.row ;
	}

	public Set<String> getDataApplying(){
		Set<String> ret = new TreeSet<String>();
		for(String name : this.row.keySet()) {
			if(this.row.get(name) == 1) {
				ret.add(name);
			}
		}
		return ret;
	}

	public String getName() {
		return name;
	}

	public void perceive() {
		this.old_criticality = this.criticality;
		this.criticality = 0.0f;
		this.currentConfig = this.function.getCurrentConfig();
		if(this.currentConfig != null) {
			for(String name: this.row.keySet()) {
				this.row.put(name, this.currentConfig.getDataValueForInput(this.input.getName(), name));
			}
		}
	}

	public void decideAndAct() {

		this.decideRequests();
		
		double max =0.0;
		for(String dataTmp : this.row.keySet()) {
			max = Math.max(this.row.get(dataTmp), max);
		}
		this.criticality = (float) max;
		
	}

	/**
	 * Decision of which request will be send to the weight
	 */
	private void decideRequests() {
		double max = 0.0;
		int countNbMax = 0;
		double sum = 0.0;
		// Identification of the value of the maximum weight
		for(Double value : this.row.values()) {
			if(value == max) {
				countNbMax++;
				sum += -value;
			}else {
				if (value >max) {
					sum += (1-value) +(max-1)*2;
					max = value;
					countNbMax = 1;
				}
				else {
					sum += -value;
				}
			}
		}
		sum = sum / this.row.values().size();
		// In case of need of global criticality
		if(max == 1.0 && countNbMax >1) {
			boolean sncSolve = false;
			for(String data : this.row.keySet()) {
				RequestForWeight requestToSend = new RequestForWeight(0, this.name, 0, null, "ROW");
				if(this.row.get(data)==max) {
					if(!sncSolve) {
						sncSolve = true;
						requestToSend.setDecision(Operator.MOINS);
					}else {
						requestToSend.setDecision(Operator.PLUS);
						requestToSend.setCriticality(Math.abs(1-this.row.get(data)));
					}
				}
				else {
					requestToSend.setDecision(Operator.MOINS);
					requestToSend.setCriticality(this.row.get(data));
				}
				this.function.sendRequestForWeight(this.input.getName(),data,requestToSend);
			}
		}
		else {
			for(String data : this.row.keySet()) {
				RequestForWeight requestToSend = new RequestForWeight(0, this.name, 0, null, "ROW");
				if(this.row.get(data)==max) {
					if(max > 1.0) {
						requestToSend.setDecision(Operator.MOINS);
						requestToSend.setCriticality(Math.abs(1-this.row.get(data)));
					}
					else {
						if(countNbMax >1) {
							requestToSend.setDecision(Operator.NONE);
						}else {
							requestToSend.setDecision(Operator.PLUS);
							requestToSend.setCriticality(Math.abs(1-this.row.get(data)));
						}
					}
				}
				else {
					requestToSend.setDecision(Operator.MOINS);
					requestToSend.setCriticality(this.row.get(data));
				}
				this.function.sendRequestForWeight(this.input.getName(),data,requestToSend);
			}
		}

	}

	

	public void onCycleBegin() {
		for(String name: this.row.keySet()) {
			this.row.put(name, 0.0);
		}
	}


	/**
	 * Compute the criticality if a dataAgent apply
	 * 
	 * @return the criticality
	 */
	public double criticalityIfApplying() {
		return criticality + Math.pow(criticality, 2);
	}

	/*
	 * Add a new DataAgent
	 */
	public void addDataAgent(DataAgent dataAgent) {
		this.row.put(dataAgent.getName(), 0.0);
	}

	public void dataAgentApplying(DataAgent dataAgent) {
		this.row.put(dataAgent.getName(), 1.0);
	}

	@Override
	public void requestAccepted(int id) {

	}

	@Override
	public void requestDenied(int id) {
		// TODO Auto-generated method stub

	}

	public void addRequest(RequestForRow requestForRow) {
		this.mailBox.add(requestForRow);
	}

	public double getCriticalityAfterUpdate(String data, Operator decision) {
		Map<String,Double> tmp = new TreeMap<String,Double>(this.row);
		switch(decision) {
		case MOINS:
			tmp.put(data, Math.max(0.0,tmp.get(data)-0.05));
			break;
		case NONE:
			break;
		case PLUS:
			tmp.put(data, Math.min(1.0,tmp.get(data)+0.05));
			break;
		default:
			break;

		}
		Double max =0.0;
		for(String dataTmp : tmp.keySet()) {
			max = Math.max(tmp.get(dataTmp), max);
		}
		return Math.abs(1.0-max);
	}

	public int wasItBetter() {
		if(this.criticality > this.old_criticality) {
			return -1;
		}
		if(this.criticality < this.old_criticality) {
			return 1;
		}
		return 0;
	}
}
