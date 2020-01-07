package fr.irit.smac.learningdata.Agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.learningdata.Agents.InputAgent.Operator;
import fr.irit.smac.learningdata.requests.RequestColumn;
import fr.irit.smac.learningdata.requests.RequestForWeight;

public class ColumnAgent extends AgentLearning{

	private float criticality;
	
	private float old_criticality;

	private DataAgent dataAgent;

	private Map<String,Double> column;

	private String name;

	private int idRequest;

	private Map<Integer,RequestColumn> waitingRequest;

	private boolean requestDenied;

	private LearningFunction function;

	private Configuration currentConfig;

	public ColumnAgent(String name, DataAgent dataAgent, LearningFunction function) {
		this.name = name;
		this.dataAgent = dataAgent;
		this.column = new TreeMap<String,Double>();
		this.idRequest = 0;
		this.criticality = 0.0f;
		this.old_criticality = 0.0f;
		this.requestDenied = false;
		this.waitingRequest = new TreeMap<Integer,RequestColumn>();
		this.function = function;
	}

	public void perceive() {
		this.column.clear();
		for(String input : this.function.getInputsName()) {
			this.column.put(input, this.dataAgent.getWeightOfInput(input));
		}
		this.old_criticality = this.criticality;
	}

	public void decideAndAct() {
		this.decideRequests();

		double max =0.0;
		for(String inputTmp : this.column.keySet()) {
			max = Math.max(this.column.get(inputTmp), max);
		}
		if(max < 0.5) {
			this.criticality = (float)max;
		}
		else {
			this.criticality = (float) Math.abs(1.0-max);
		}
	}
	/**
	 * Decision of which request will be send to the weight
	 */
	private void decideRequests() {
		double max = 0.0;
		int countNbMax = 0;
		// Identification of the value of the maximum weight
		for(Double value : this.column.values()) {
			if(value == max) {
				countNbMax++;
			}
			if (value >max) {
				max = value;
				countNbMax = 1;
			}
		}
		double sum = 0.0;
		if(max == 1.0 && countNbMax > 1 ) {
			boolean sncSolve = false;
			for(String input : this.column.keySet()) {
				RequestForWeight requestToSend = new RequestForWeight(0, this.name, 0, null, "COLUMN");
				if(this.column.get(input)==max) {
					if(!sncSolve ) {
						sncSolve = true;
						requestToSend.setCriticality(this.column.get(input));
						requestToSend.setDecision(Operator.MOINS);
					}
					else {
						requestToSend.setCriticality(Math.abs(1-this.column.get(input)));
						requestToSend.setDecision(Operator.PLUS);
						sum += this.column.get(input);
					}
				}
				else {
					requestToSend.setCriticality(this.column.get(input));
					requestToSend.setDecision(Operator.MOINS);
					sum += this.column.get(input);
				}
				this.function.sendRequestForWeight(input,this.dataAgent.getName(),requestToSend);
			}
		}
		else {
			for(String input : this.column.keySet()) {
				RequestForWeight requestToSend = new RequestForWeight(0, this.name, 0, null, "COLUMN");
				if(this.column.get(input)==max) {
					if(max > 1.0) {
						requestToSend.setCriticality(Math.abs(1-this.column.get(input)));
						requestToSend.setDecision(Operator.MOINS);
					}
					else {
						if(countNbMax >1 ) {
							sum += this.column.get(input);
							requestToSend.setCriticality(this.column.get(input));
							requestToSend.setDecision(Operator.NONE);
						}
						else {
							sum += 1-this.column.get(input);
							requestToSend.setCriticality(Math.abs(1-this.column.get(input)));
							requestToSend.setDecision(Operator.PLUS);
						}
					}
				}
				else {
					sum += this.column.get(input);
					requestToSend.setCriticality(this.column.get(input));
					requestToSend.setDecision(Operator.MOINS);
				}
				this.function.sendRequestForWeight(input,this.dataAgent.getName(),requestToSend);
			}
		}

	}

	/**
	 * Request the data agent
	 */
	private void searchForService() {
		this.waitingRequest.put(idRequest, new RequestColumn(this.criticality, this.name,idRequest));
		this.dataAgent.sendRequest(this.waitingRequest.get(idRequest));
		idRequest++;
		this.requestDenied = false;
	}

	public double getCriticality() {
		return criticality;
	}

	public DataAgent getDataAgent() {
		return dataAgent;
	}

	public Map<String,Double> getRow() {
		return this.column;
	}

	public String getName() {
		return name;
	}

	@Override
	public void requestAccepted(int id) {
		this.waitingRequest.remove(id);

	}

	@Override
	public void requestDenied(int id) {
		this.waitingRequest.remove(id);
		this.requestDenied = true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataAgent == null) ? 0 : dataAgent.hashCode());
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
		ColumnAgent other = (ColumnAgent) obj;
		if (dataAgent == null) {
			if (other.dataAgent != null)
				return false;
		} else if (!dataAgent.equals(other.dataAgent))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * Return the criticality calculated after the possible update
	 * @param input
	 * @param decision
	 * @return the criticality
	 */
	public double getCriticalityAfterUpdate(String input, Operator decision) {
		Map<String,Double> tmp = new TreeMap<String,Double>(this.column);
		switch(decision) {
		case MOINS:
			tmp.put(input, Math.max(0.0,tmp.get(input)-0.05));
			break;
		case NONE:
			break;
		case PLUS:
			tmp.put(input, Math.min(1.0,tmp.get(input)+0.05));
			break;
		default:
			break;

		}
		Double max =0.0;
		for(String inputTmp : tmp.keySet()) {
			max = Math.max(tmp.get(inputTmp), max);
		}
		if(max < 0.5) {
			return max;
		}
		else {
			return Math.abs(1.0-max);
		}
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
