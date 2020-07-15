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


	private static final int MAX_SIZE_HISTORIC = 100;

	private String dataName;

	private String inputName;

	private float morphValue;

	private String name;

	private DataAgent superiorAgent;

	//private Matrix matrix;

	private Float value;

	private float usefulness;

	private Map<Integer, Pair<Float,Float>> historic;

	private List<Historic> historiques;

	private Map<Float,Float> distribution;

	private InputConstraint inputConstraint;

	private DataUnicityConstraint dataConstraint;

	private List<CompetitiveAgent> neighbours;

	private LinearRegression lr;

	private Float a = 1.0f;

	private Float b = 0.0f;

	private final float sensibility = 5.f;

	private final float accepted_error = 15.f;

	private float etendu;

	private float error;

	private Float min;
	private Float max;
	private int cycle;

	private Float valueForCycle;

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
		this.cycle = 0;

		this.name = inputName+":"+dataName;
		this.historic = new TreeMap<>();
		this.distribution = new TreeMap<>();
		this.neighbours = new ArrayList<>();
		this.historiques = new ArrayList<>();
	}


	public void perceive() {
		this.value = null;
		// voit sa valeur
		this.value = this.superiorAgent.askValue();


		//this.morphValue = this.linearRegression();
		this.morphingLinear(this.value);

		this.valueForCycle = this.morphingLinear(this.value);
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
			Offer myOffer =null;
			CompetitiveAgent agentNegociating = null;
			if(this.superiorAgent.getCoalition() != null) {
				agentNegociating = this.superiorAgent.getCoalition(); 
			}
			else {
				agentNegociating = this;
			}
			myOffer =  new Offer(agentNegociating,this.inputConstraint,this.superiorAgent.getCurrentTime(),this.usefulness, this.morphValue);
			Offer dataOffer = new Offer(this,this.inputConstraint,this.superiorAgent.getCurrentTime(),this.usefulness, this.morphValue);

			if(!this.dataConstraint.hasMyOffer(this) && !this.inputConstraint.hasMyOffer(agentNegociating)) {
				if(this.dataConstraint.isOfferBetter(dataOffer) && this.inputConstraint.isOfferBetter(myOffer)) {
					this.dataConstraint.addOffer(dataOffer);
					this.inputConstraint.addOffer(myOffer);
				}
			}
			else {
				if(this.inputConstraint.hasMyOffer(agentNegociating)) {
					if(this.inputConstraint.isOfferBetter(myOffer)) {
						//Remove less crit
						this.inputConstraint.removeOffer(agentNegociating);
						this.dataConstraint.removeOffer(this.inputConstraint);

						//Add mine which is better
						this.inputConstraint.addOffer(myOffer);
						this.dataConstraint.addOffer(dataOffer);
					}
				}

				// Cas remove
				if(!this.inputConstraint.isSatisfied() && this.dataConstraint.hasMyOffer(this)) {
					if(!this.dataConstraint.isOfferBetter(dataOffer) || !this.inputConstraint.isOfferBetter(myOffer)) {

						this.dataConstraint.removeOffer(this);
						this.inputConstraint.removeOffer(agentNegociating);
					}
					else {
						System.out.println(myOffer);
						System.out.println(this.dataConstraint.getOffers());
						System.out.println(this.inputConstraint.getOffers());
						System.out.println("MERDE");
					}
				}
				if(!this.dataConstraint.isSatisfied() && this.dataConstraint.hasMyOffer(this)) {
					if(!this.dataConstraint.isOfferBetter(dataOffer)) {
						if(!this.inputConstraint.isSatisfied()) {
							this.dataConstraint.removeOffer(this);
							this.inputConstraint.removeOffer(agentNegociating);
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
								this.dataConstraint.removeOffer(this);
								this.inputConstraint.removeOffer(agentNegociating);
								oth.cycleOffer();
							}
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



	public void sendFeedback(Float correctValue, boolean tolerant) {
		this.value = this.superiorAgent.askValue();
		//this.morphValue = this.linearRegression();
		this.morphValue = this.morphingLinear(this.value);
		this.addMorph(this.value, correctValue);
		float diffPourcent = this.sensibility+1;

		this.error = this.computeError(correctValue);

		/*if(lr != null) {
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
		}*/

		diffPourcent = Math.abs(((correctValue - this.morphingLinear(this.value))/this.morphingLinear(this.value)*100));
		if((correctValue == this.morphingLinear(this.value)) 
				|| this.error <= this.accepted_error) {
			this.usefulness = Math.min(1.0f, this.usefulness+0.05f);
		}
		else {
			this.usefulness = Math.max(.0f, this.usefulness-0.05f);
		}
		this.linearAdaptation(correctValue);
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

	/**
	 * Learn how to morph without LR
	 */
	private void linearAdaptation(float feedback) {
		if(this.historiques.size()>1) {
			Integer indMaxSup = null;
			Integer indMaxInf = null;
			Float maxCritInf = null;
			Float maxCritSup = null;


			Integer indSecondSup = null;
			Integer indSecondInf = null;

			// look for the most critic historic
			/*for(Integer i : this.historic.keySet()) {
				float crit = this.morphingLinear(this.historic.get(i).getLeft()) - this.historic.get(i).getRight(); 


				if(indMaxSup != null && indSecondSup == null && crit >= 0) {
					indSecondSup = i.intValue();
				}
				if(indMaxInf != null && indSecondInf == null && crit <= 0) {
					indSecondInf = i.intValue();
				}
				if(indMaxSup == null || (crit >= 0 && crit > maxCritSup)) {
					if(indMaxSup != null) {
						indSecondSup = indMaxSup.intValue();
					}
					indMaxSup = i.intValue();
					maxCritSup = crit;
				}
				if(indMaxInf == null || (crit <= 0 && crit < maxCritInf)) {
					if(indMaxInf != null) {
						indSecondInf = indMaxInf.intValue();
					}
					indMaxInf = i.intValue();
					maxCritInf = crit;
				}

			}*/
			boolean end = false;
			int i = 0;
			while(i < this.historiques.size() && !end) {
				Historic histo = this.historiques.get(i);
				if(histo.valueData < histo.valueInput) {
					if(indMaxInf == null) {
						indMaxInf = i;
						maxCritInf = histo.crit;
					}else {
						if(indSecondInf == null) {
							indSecondInf = i;
						}
					}
				}
				else {
					if(indMaxSup == null) {
						indMaxSup = i;
						maxCritSup = histo.crit;
					}else {
						if(indSecondSup == null) {
							indSecondSup = i;
						}
					}
				}
				end = indMaxSup != null && indMaxInf != null;
				i++;
			}

			if(end) {
				adaptCoeff(indMaxSup, indMaxInf, maxCritSup, maxCritInf);
			}
			else {
				if(indMaxSup != null) {
					adaptCoeff(indMaxSup, indSecondSup, maxCritSup, 0);
				}
				else {
					if(indMaxInf != null ) {
						adaptCoeff(indMaxInf, indSecondInf, maxCritInf, 0);
					}
					else {
						if(!this.dataName.equals(this.inputName)) {
							System.out.println("NORMALEMENT NON");
						}
					}
				}
			}

			/*// Deux plus critique sup
			if(maxCritInf > 0 ) {
				adaptCoeff(indMaxSup, indSecondSup, maxCritSup, 0);
			}else {
				// Deux plus critique inf
				if(maxCritSup < 0 ) {
					adaptCoeff(indMaxInf, indSecondInf, maxCritInf, 0);
				}
				else {

					if(indMaxSup.intValue() != indMaxInf.intValue()) {
						adaptCoeff(indMaxSup, indMaxInf, maxCritSup, maxCritInf);
					}
					else {
						if(!this.dataName.equals(this.inputName)) {
							System.out.println("NORMALEMENT NON");
						}
					}
				}
			}*/

		}

	}

	private void adaptCoeff(Integer indMaxSup, Integer indMaxInf, float critFirst, float critSecond) {
		/*float y1 = this.historic.get(indMaxSup).getRight();
		float y2 = this.historic.get(indMaxInf).getRight();
		float x1 = this.historic.get(indMaxSup).getLeft();
		float x2 = this.historic.get(indMaxInf).getLeft();

		if(this.historic.keySet().size() > 2) {
			if(Math.abs(critFirst) > Math.abs(critSecond)) {
				float newa = (y2 - y1)/(x2 -x1);
				float newb = y1 - (y2-y1)/(x2-x1) * x1;
				this.a = (this.a + newa) / 2;
				this.b = (this.b + newb) / 2;
			}
			else {
				this.a = (y2 - y1)/(x2 -x1);
				this.b = y1 - (y2-y1)/(x2-x1) * x1;
			}
		}
		 */
		float maxi = critFirst > critSecond ? critFirst : critSecond;
		float y1 = this.historiques.get(indMaxSup).valueInput;
		float y2 = this.historiques.get(indMaxInf).valueInput;
		float x1 = this.historiques.get(indMaxSup).valueData;
		float x2 = this.historiques.get(indMaxInf).valueData;

		if(this.historiques.size() > 2) {
			/*if(Math.abs(critFirst) > Math.abs(critSecond)) {
				float newa = (y2 - y1)/(x2 -x1);
				float newb = y1 - (y2-y1)/(x2-x1) * x1;
				this.a = (this.a + newa) / 2;
				this.b = (this.b + newb) / 2;
			}
			else {
				this.a = (y2 - y1)/(x2 -x1);
				this.b = y1 - (y2-y1)/(x2-x1) * x1;
			}*/

			float newa = (y2 - y1)/(x2 -x1);
			float newb = y1 - (y2-y1)/(x2-x1) * x1;
			this.a = (this.a + newa) / 2;
			this.b = (this.b + newb) / 2;
			for(Historic histo : this.historiques) {
				histo.computeCrit(this.a, this.b);
			}
			Collections.sort(this.historiques);
			if(maxi > this.historiques.get(0).crit) {
				this.usefulness = Math.min(this.usefulness +0.05f, 1.0f);
			}
			this.usefulness = 1.0f - this.historiques.get(0).crit / this.etendu;
			int ind = this.dataName.indexOf(':');
			/*if(ind != -1) {
				String subStr = this.dataName.substring(0, ind);
				if(subStr.equals(this.inputName) && Math.abs(this.historiques.get(0).crit) > this.etendu) {
					System.out.println(this.name);
					System.out.println(this.getLinearFormula());
					System.out.println(this.historiques.get(0).getValueData());
					System.out.println(this.historiques.get(0).getValueInput());
				}
			}*/
			/*if(this.dataName.contains(this.inputName)) {
				if(Math.abs(this.historiques.get(0).crit) > this.etendu) {
					System.out.println(this.name);
					System.out.println(this.a * this.historiques.get(0).getValueData() + this.b);
					System.out.println(this.historiques.get(0).getValueInput());
				}
			}*/
		}
		
	}

	/**
	 * Morph the value into a more adequate one
	 * 
	 * @return the value morphed
	 */
	public float morphingLinear(float value) {
		return value * this.a + this.b;
	}

	/**
	 * Add a feedback to the historic
	 * 
	 * If there is too much feedback, remove the less critical
	 * 
	 * @param myValue
	 * 		The value of my dataAgent
	 * 
	 * @param otherValue
	 * 		The real value I aim to become
	 */
	public void addMorph(Float myValue, Float otherValue) {
		if(otherValue !=0)
			this.distribution.put(myValue, myValue/otherValue);
		else
			this.distribution.put(myValue, 0.f);

		//OBJET HISTO
		Historic histo = new Historic(this.cycle,myValue,otherValue);
		histo.computeCrit(this.a, this.b);


		// Remove the less critical historic
		if(this.historiques.size() >= MAX_SIZE_HISTORIC) {
			/*Float minDiff = Math.abs(this.morphingLinear(myValue) - otherValue);
			Integer indMin = -1;
			int indMax = -1;
			for(Integer i : this.historic.keySet()) {
				//Trouver le moins critique
				float diff  = Math.abs(this.morphingLinear(this.historic.get(i).getLeft())- this.historic.get(i).getRight());
				if(diff < minDiff) {
					minDiff = diff;
					indMin = i;
				}
				if(i > indMax) {
					indMax = i;
				}
			}
			if(indMin != -1) {
				this.historic.remove(indMin);
				this.historic.put(indMax+1, Pair.of(myValue, otherValue));
			}*/
			if(this.historiques.get(MAX_SIZE_HISTORIC-1).crit < histo.crit) {
				this.historiques.remove(MAX_SIZE_HISTORIC-1);
				this.historiques.add(histo);
			}
		}
		else {
			//this.historic.put(this.historic.keySet().size(), Pair.of(myValue, otherValue));
			this.historiques.add(histo);
		}


		// Compute the max and the min
		if(min == null || this.min > otherValue) {
			this.min = otherValue;
		}
		if(this.max == null || this.max < otherValue) {
			this.max = otherValue;
		}


		if(min != null && max != null) {
			this.etendu = max -min;
		}
		Collections.sort(this.historiques);
		this.cycle++;
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
	public void prepareToNegociate() {
		this.inputConstraint = null;
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
		//this.morphValue = this.linearRegression();
		this.morphValue = this.morphingLinear(this.value);
		float valueToSend = this.value;
		valueToSend = this.morphValue;
		/*if(this.lr != null) {
			valueToSend = this.morphValue;
		}*/
		return valueToSend;
	}



	@Override
	public void cycleValue(String input) {
		this.morphValue = this.morphingLinear(this.superiorAgent.askValue());
	}

	@Override
	public String getCompetitiveName() {
		return this.dataName;
	}

	public float getError() {
		return this.error;
	}

	/**
	 * Clear both offer
	 */
	public void clearOffer() {
		CompetitiveAgent agent = this;
		if(this.superiorAgent.getCoalition() != null) {
			agent = this.superiorAgent.getCoalition();
		}
		if(this.dataConstraint != null && this.dataConstraint.hasMyOffer(this)) {
			this.dataConstraint.removeOffer(this);
		}
		if(this.inputConstraint != null && this.inputConstraint.hasMyOffer(agent)) {
			this.inputConstraint.removeOffer(agent);
		}
	}

	/**
	 * Return a String corresponding of the linear formula
	 * 
	 * @return
	 */
	public String getLinearFormula() {
		return this.a +"x + " + this.b;
	}

	private class Historic implements Comparable<Object>{

		private int cycle;

		private float valueData;

		private float valueInput;

		private float crit;

		public Historic(int cycle, float valueData, float valueInput) {
			this.cycle = cycle;
			this.valueData = valueData;
			this.valueInput = valueInput;
		}

		public float getValueInput() {
			return this.valueInput;
		}

		public float getValueData() {
			return this.valueData;
		}

		public void computeCrit(float a , float b) {
			this.crit = Math.abs(this.valueData * a + b - valueInput);
		}

		@Override
		public int compareTo(Object other) {
			Historic oth = (Historic) other;
			// TODO Auto-generated method stub
			if(this.crit < oth.crit) {
				return 1;
			}
			if(this.crit > oth.crit) {
				return -1;
			}
			return 0;
		}





		@Override
		public String toString() {
			return "Historic [cycle=" + cycle + ", crit=" + crit + "]";
		}


	}

}
