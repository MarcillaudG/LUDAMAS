package fr.irit.smac.planification.agents;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import fr.irit.smac.planification.generic.CompetitiveAgent;
import fr.irit.smac.planification.matrix.DataUnicityConstraint;
import fr.irit.smac.planification.matrix.Input;
import fr.irit.smac.planification.matrix.InputConstraint;
import fr.irit.smac.planification.matrix.Matrix;
import fr.irit.smac.planification.matrix.Offer;
import fr.irit.smac.planification.tools.LinearRegression;

public class DataMorphAgent implements CompetitiveAgent{


	private String dataName;

	private String inputName;

	private float morphValue;

	private String name;

	private DataAgent superiorAgent;

	//private Matrix matrix;

	private Float value;

	private float usefulness;

	private Map<Integer, Pair<Float,Float>> historic;

	private Map<Float,Float> distribution;

	private InputConstraint inputConstraint;

	private DataUnicityConstraint dataConstraint;

	private List<CompetitiveAgent> neighbours;

	private LinearRegression lr;
	
	private Float a;
	
	private Float b;

	private final float sensibility = 5.f;
	
	private final float accepted_error = 5.f;

	private float etendu;
	
	private float error;

	private boolean isActif;

	public DataMorphAgent(String dataName, String inputName, DataAgent agent) {
		this.dataName = dataName;
		this.inputName = inputName;
		this.superiorAgent = agent;
		this.morphValue = 1.0f;
		this.usefulness = 0.5f;
		if(dataName.equals(inputName)) {
			this.usefulness = 1.0f;
		}
		this.etendu = 1.0f;


		this.name = inputName+":"+dataName;
		this.historic = new TreeMap<>();
		this.distribution = new TreeMap<>();
		this.neighbours = new ArrayList<>();
	}

	public DataMorphAgent(String dataName, String inputName) {
		this.dataName = dataName;
		this.inputName = inputName;
		this.morphValue = 1.0f;
		this.usefulness = 0.5f;
		if(dataName.equals(inputName)) {
			this.usefulness = 1.0f;
		}
		this.etendu = 1.0f;
		this.historic = new TreeMap<>();
		this.distribution = new TreeMap<>();
	}

	public DataMorphAgent(String dataName, String inputName, DataAgent agent, Matrix mat, float value) {
		this.dataName = dataName;
		this.inputName = inputName;
		this.superiorAgent = agent;
		//this.matrix = mat;
		this.morphValue = 1.0f;
		this.usefulness = value;
		this.etendu = 1.0f;

		this.name = inputName+":"+dataName;
		this.historic = new TreeMap<>();
		this.distribution = new TreeMap<>();
		this.neighbours = new ArrayList<>();
	}

	public void perceive() {
		this.value = null;
		// voit sa valeur
		this.value = this.superiorAgent.askValue();


		this.morphValue = this.linearRegression();
		// recupere la valeur de son objective si dispo
		//this.superiorAgent.askValueOfOtherData(this.inputName);

		// Recupere les deux contraintes
		this.dataConstraint = this.superiorAgent.getDataUnicityConstraint();
		this.inputConstraint = this.superiorAgent.getInputConstraint(this.inputName);
		this.neighbours.clear();

		this.neighbours.addAll(this.superiorAgent.getNeighboursActives(this.inputName));

	}

	public void decide() {
		// si valeur != null
		if(this.value !=null) {
			Offer myOffer = new Offer(this,this.inputConstraint,this.superiorAgent.getCurrentTime(),this.usefulness);
			if(!this.dataConstraint.hasMyOffer(this) && !this.inputConstraint.hasMyOffer(this)) {
				if(this.dataConstraint.isOfferBetter(myOffer) && this.inputConstraint.isOfferBetter(myOffer)) {
					this.dataConstraint.addOffer(myOffer);
					this.inputConstraint.addOffer(myOffer);
				}
			}
			else {
				// Cas remove
				if(!this.inputConstraint.isSatisfied()) {
					if(!this.dataConstraint.isOfferBetter(myOffer) || !this.inputConstraint.isOfferBetter(myOffer)) {
						this.dataConstraint.removeOffer(myOffer);
						this.inputConstraint.removeOffer(myOffer);
					}
					else {
						System.out.println("MERDEs");
					}
				}
				if(!this.dataConstraint.isSatisfied()) {
					if(!this.inputConstraint.isSatisfied()) {
						this.dataConstraint.removeOffer(myOffer);
						this.inputConstraint.removeOffer(myOffer);
					}
					else {
						// SOLVE SNC
						CompetitiveAgent oth = null;
						for(CompetitiveAgent agent: this.neighbours) {
							if(agent.isAvailable()) {
								if(oth == null || agent.getUsefulness() > oth.getUsefulness()) {
									oth = agent;
								}
							}
						}
						if(oth != null) {
							this.dataConstraint.removeOffer(myOffer);
							this.inputConstraint.removeOffer(myOffer);
							oth.cycleOffer();
						}
					}
				}
			}
		}

	}
	public void act() {
		// si lie
		//this.morphValue = this.linearRegression();

		//System.out.println(valueToSend);
	}

	public void sendFeedback(float correctValue) {
		this.value = this.superiorAgent.askValue();
		this.addMorph(this.value, correctValue);
		if(correctValue == this.value * this.morphValue || (this.lr != null && correctValue == this.lr.predict(this.value))) {
			this.usefulness = Math.min(1.0f, this.usefulness+0.1f);
		}
		else {
			this.usefulness = Math.max(.0f, this.usefulness-0.1f);
		}

	}


	public void sendFeedback(Float correctValue, boolean tolerant) {
		this.value = this.superiorAgent.askValue();
		this.morphValue = this.linearRegression();
		this.addMorph(this.value, correctValue);
		float diffPourcent = this.sensibility+1;
		
		this.error = this.computeError(correctValue);

		if(lr != null) {
			diffPourcent = Math.abs(((correctValue - this.lr.predict(this.value))/this.lr.predict(this.value)*100));
		}
		if(this.lr == null) {

		}
		else {
			//||  diffPourcent < this.sensibility*this.etendu/100 
			if((this.lr != null && correctValue == this.lr.predict(this.value)) 
					|| this.error <= this.accepted_error) {
				this.usefulness = Math.min(1.0f, this.usefulness+0.05f);
			}
			else {
				this.usefulness = Math.max(.0f, this.usefulness-0.05f);
			}
		}
		this.superiorAgent.updateMatrix(this.inputName,this.dataName,this.usefulness);

	}


	private float computeError(Float correctValue) {
		if(this.etendu != 0.0f) {
			return Math.abs(this.morphValue-correctValue)/this.etendu *100;
		}
		else {
			return Math.abs(Math.abs(this.morphValue-correctValue)/correctValue) *100;
		}
	}

	private float linearRegression() {
		double x [] = new double[this.historic.keySet().size()];
		double y [] = new double[this.historic.keySet().size()];

		float morphedValue = this.value;

		int i =0;
		//System.out.println(this.historic);
		for(Integer cycle : this.historic.keySet()) {
			x[i] = this.historic.get(cycle).getLeft();
			y[i] = this.historic.get(cycle).getRight();
			i++;
		}

		if(x.length > 1) {
			this.lr = new LinearRegression(x, y);

			morphedValue = (float) lr.predict(this.value);
			/*System.out.println(lr.slope());
			System.out.println(lr.R2());
			System.out.println(lr.intercept());
			System.out.println(this.value);
			System.out.println(lr.predict(this.value));*/
		}
		else {
			// TODO CHANGED
			morphedValue = this.value;
		}

		return morphedValue;
	}


	public void addMorph(Float myValue, Float otherValue) {
		if(otherValue !=0)
			this.distribution.put(myValue, myValue/otherValue);
		else
			this.distribution.put(myValue, 0.f);

		this.historic.put(this.historic.keySet().size(), Pair.of(myValue, otherValue));

		Float max = null;
		Float min = null;
		if(this.historic.keySet().size()>1) {
			for(Pair<Float,Float> p : this.historic.values()) {
				if(max == null || p.getRight() > max) {
					max = p.getRight();
				}
				if(min == null || p.getRight() < min) {
					min = p.getRight();
				}
			}
		}
		if(min != null && max != null) {
			this.etendu = max -min;
		}
	}

	public static void main(String args[]) {
		DataMorphAgent morphling = new DataMorphAgent("Data", "Input");
		for(int i =0; i < 10; i++) {
			morphling.addMorph(10.f*(i+1), 15.f*(i+1)+i*10);
		}
		morphling.value = 35.f;
		//morphling.act();
		morphling.linearRegression();
		//System.out.println(morphling.morphValue);
	}

	public String getData() {
		return this.dataName;
	}

	@Override
	public String getInput() {
		return this.inputName;
	}

	public void start(int currentStep) {
		this.perceive();
		this.decide();
		this.act();
	}

	@Override
	public String toString() {
		return "DataMorphAgent:"+this.inputName+":"+this.dataName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataName == null) ? 0 : dataName.hashCode());
		result = prime * result + ((inputName == null) ? 0 : inputName.hashCode());
		result = prime * result + ((superiorAgent == null) ? 0 : superiorAgent.hashCode());
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
		DataMorphAgent other = (DataMorphAgent) obj;
		if (dataName == null) {
			if (other.dataName != null)
				return false;
		} else if (!dataName.equals(other.dataName))
			return false;
		if (inputName == null) {
			if (other.inputName != null)
				return false;
		} else if (!inputName.equals(other.inputName))
			return false;
		if (superiorAgent == null) {
			if (other.superiorAgent != null)
				return false;
		} else if (!superiorAgent.equals(other.superiorAgent))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public Float getMorphValue() {
		return this.morphValue;
	}

	public String getMorphLRFormula() {
		if(this.lr != null) {
			return this.lr.slope() +" * x + "+this.lr.intercept();
		}
		return "1.0";
	}

	public float getPredict() {
		if(lr != null) {
			return this.lr.predict(this.value);
		}
		else {
			return this.value;
		}
	}

	public void increaseUsefull() {
		this.usefulness = Math.min(1.0f, this.usefulness+0.05f);
	}

	public void decreaseUsefull() {
		this.usefulness = Math.max(.0f, this.usefulness-0.05f);
	}

	public boolean isActif() {
		return this.isActif;
	}

	public void activate() {
		this.isActif = true;
	}

	public void desactivate() {
		this.isActif = false;
	}

	public float getUsefulness() {
		return this.usefulness;
	}

	public float morph(float myValue) {
		if(lr != null) {
			return this.lr.predict(myValue);
		}
		else {
			return myValue;
		}
	}

	@Override
	public void sendOffer(Offer offer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAvailable() {
		return this.dataConstraint.isSatisfied();
	}


	@Override
	public void cycleOffer() {
		this.perceive();
		this.decide();
		this.act();
	}

	@Override
	public float getValue() {
		this.morphValue = this.linearRegression();
		float valueToSend = this.value;
		if(this.lr != null) {
			valueToSend = this.morphValue;
		}
		return valueToSend;
	}

	@Override
	public String getCompetitiveName() {
		return this.dataName;
	}
	
	public float getError() {
		return this.error;
	}

}
